package main;

import assemblers.Assembler;
import assemblers.ChamplainAssembler;
import assemblers.MaisonneuveAssembler;
import assemblers.VanierAssembler;
import scrapers.ChamplainScraper;
import scrapers.MaisonneuveScraper;
import scrapers.OmnivoxScraper;
import scrapers.VanierScraper;
import students.Student;
import students.StudentManager;
import students.StudentPrinter;

public class Main {

	public static void main(String[] args) {

		if (args.length != 3) {
			System.out.println("Usage: java Main [CegepName] [StudentNumber] [Password]");
			System.exit(0);
		}

		OmnivoxScraper scraper = null;
		Assembler assembler = null;

		switch (args[0].toLowerCase()) {
			case "champlain":
				scraper = new ChamplainScraper();
				assembler = new ChamplainAssembler();
				break;

			case "maisonneuve":
				scraper = new MaisonneuveScraper();
				assembler = new MaisonneuveAssembler();
				break;

			case "vanier":
				scraper = new VanierScraper();
				assembler = new VanierAssembler();
				break;

			default:
				System.out.println("The currently supported CEGEPs are:\n\t- Champlain\n\t- Maisonneuve\n\t- Vanier\n\n" + args[0] + "is not supported");
				System.exit(0);
		}

		Student student = new Student();
		StudentManager manager = new StudentManager(scraper, assembler, student);
		StudentPrinter printer = new StudentPrinter(student);

		System.out.println("Logging in...");

		String studentNumber = args[1];
		String password = args[2];

		manager.login(studentNumber, password);

		// Getting and printing documents
		manager.getDocuments();
		printer.printDocuments();

		// Getting and printing assignments
		manager.getAssignments();
		printer.printAssignments();

		// Getting and printing calendar events
		manager.getCalendarEvents();
		printer.printCalendarEvents();

		// Print what's new
		scraper.printWhatsNew();
		System.exit(0);
	}

}