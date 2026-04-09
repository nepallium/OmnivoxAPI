package scrapers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import assemblers.Assembler;

/**
 * This abstract class is used to collect the {@link HtmlPage} object from
 * Omnivox. You need to extend this class and implement its 4 methods. The job
 * of this class is to collect the corresponding pages from Omnivox so that the
 * {@link Assembler} will turn them into usable object.
 */
public abstract class OmnivoxScraper {

	/**
	 * This String represents the login url of the Omnivox page.
	 * 
	 * It should have the following form: https:// + [Your Cegep Name] +
	 * .omnivox.ca/intr/Module/Identification/Login/Login.aspx
	 */
	private final String loginUrl;

	/**
	 * This is the webclient used to connect and make requests to
	 */
	private final WebClient client = newClient();

	/**
	 * Represents the Html Version of the Omnivox homepage.
	 */
	protected HtmlPage homePage;

	/**
	 * Represents the Html Version of the Omnivox Lea page.
	 */
	protected HtmlPage LeaPage;

	/**
	 * The only constructor for the Omnivox Scraper.
	 * 
	 * @param loginUrl The login url for starting the Omnivox Scraper
	 */
	public OmnivoxScraper(String loginUrl) throws IllegalArgumentException {

		// Check if it matches the login pattern
//		if (!loginUrl
//				.matches("https:\\/\\/(.+?)\\.omnivox\\.ca\\/intr\\/Module\\/Identification\\/Login\\/Login\\.aspx")) {
//			throw new IllegalArgumentException("The login url is invalid it should match this pattern:"
//					+ "https:// + [Your Cegep Name] + .omnivox.ca/intr/Module/Identification/Login/Login.aspx");
//		}

		this.loginUrl = loginUrl;
	}

	/**
	 * This method needs to get all of the documents in the student's account.
	 * 
	 * When called, it should go and find all of the HtmlPages from every course
	 * containing all of the document pages. It need to go through the Lea page and
	 * return an {@link HtmlPage} for every course.
	 */
	public abstract HtmlPage[] getDocumentPages();

	/**
	 * This method needs to get all of the assignments in the student's account.
	 * 
	 * When called, it should go and find all of the HtmlPages from every course
	 * containing all of the assignment pages. It need to go through the Lea page
	 * and return an {@link HtmlPage} for every course.
	 */
	public abstract HtmlPage[] getAssignmentPages();

	/**
	 * This method prints the what's new section in the omnivox homepage.
	 * 
	 * This method will not break the program if it is not well implemented since it
	 * doesn't return anything.
	 */
	public abstract void printWhatsNew();

	/**
	 * This method needs to set the Lea Page field to the corresponding field.
	 * 
	 * However, if it is not set, it will not be usable since it will throw a
	 * {@link NullPointerException}.
	 */
	public abstract void setLeaPage();

	/**
	 * This final method creates a new instance of a {@link WebClient} with all of
	 * the required parameters to run well.
	 */
	private final static WebClient newClient() {
		WebClient client = new WebClient(BrowserVersion.CHROME);

		// Silence the noise
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setJavaScriptEnabled(true); // Keep it on, but we'll ignore errors
		client.getOptions().setCssEnabled(false);

		// Set a very high timeout for background tasks
		client.setJavaScriptTimeout(15000);
		client.getOptions().setTimeout(20000);

		return client;
	}

	/**
	 * This method will login to the Omnivox page and set the homePage field.
	 * 
	 * @implNote If you override this method because it can't login to your Omnivox,
	 *           make sure you set the homePage field to it's correct value.
	 */
	public void login(String username, String password) {
		try {
			// 1. Load the page
			HtmlPage loginPage = client.getPage(this.loginUrl);

			// 2. Ignore the JS errors and get the form directly
			HtmlForm form = loginPage.getFormByName("formLogin");

			// 3. MANUALLY set the fields that the JS button would have set
			// Based on the HTML you provided, these are the IDs
			HtmlInput userField = (HtmlInput) loginPage.getHtmlElementById("Identifiant");
			HtmlInput passField = (HtmlInput) loginPage.getHtmlElementById("Password");

			userField.setValueAttribute(username);
			passField.setValueAttribute(password);

			// 4. MANUALLY set the hidden student type
			HtmlInput typeId = form.getInputByName("TypeIdentification");
			typeId.setValueAttribute("Etudiant");

			// 5. Submit the form
			// We find the button by its class/text since the JS RoleClick didn't run
			HtmlButton submitBtn = loginPage.getFirstByXPath("//button[contains(., 'Log In')]");

			this.homePage = submitBtn.click();

			// 6. Wait for the server to process the POST request
			client.waitForBackgroundJavaScript(5000);

			System.out.println("Final URL after login: " + this.homePage.getUrl());

		} catch (Exception e) {
			System.err.println("Login Failed: " + e.getMessage());
		}
	}

	// Getters
	public HtmlPage getHomePage() {
		return this.homePage;
	}

	public HtmlPage getLeaPage() {
		return this.LeaPage;
	}

	public WebClient getClient() {
		return this.client;
	}

}
