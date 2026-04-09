package scrapers;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class VanierScraper extends OmnivoxScraper {
    private static final String loginUrl = "https://vaniercollege.omnivox.ca/Login/Account/Login?ReturnUrl=%2fintr%2f";

    public VanierScraper() throws IllegalArgumentException {
        super(loginUrl);
    }

    @Override
    public HtmlPage[] getDocumentPages() {
        List<HtmlElement> classes = this.LeaPage.getByXPath("//*[@class='card-panel section-spacing']");

        // Creating return array
        HtmlPage[] return_array = new HtmlPage[classes.size()];

        for (int i = 0; i < classes.size(); i++) {

            HtmlElement button = classes.get(i).getFirstByXPath("./div[2]/a[1]");

            try {
                return_array[i] = button.click();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return return_array;
    }

    @Override
    public HtmlPage[] getAssignmentPages() {
        List<HtmlElement> classes = this.LeaPage.getByXPath("//*[@class='card-panel section-spacing']");

        // Creating return array
        HtmlPage[] return_array = new HtmlPage[classes.size()];

        for (int i = 0; i < classes.size(); i++) {

            HtmlElement button = classes.get(i).getFirstByXPath("./div[2]/a[2]");

            try {
                return_array[i] = button.click();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return return_array;
    }

    @Override
    public void printWhatsNew() {
//		Check if there is anything new
        List<HtmlElement> whats_new = this.homePage.<HtmlElement>getByXPath("//*[@id=\"qdn-sans-bouton-wrapper\"]/a/div[1]");

        if (whats_new.size() > 0) {

//			If there is something new
//			Printing the text of the elements
            for (HtmlElement news : whats_new) {
                System.out.println(news.asText());
            }
        } else {
            System.out.println("Nothing New");
        }
    }

    @Override
    public void setLeaPage() {
        try {
            System.out.println(this.homePage.asXml());
            this.LeaPage = this.homePage.<HtmlElement>getFirstByXPath("//*[@id='region-raccourcis-services-skytech']/a").click();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
