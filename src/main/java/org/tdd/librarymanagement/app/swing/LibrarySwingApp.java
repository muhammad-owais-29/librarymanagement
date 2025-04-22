package org.tdd.librarymanagement.app.swing;

import java.util.concurrent.Callable;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.controller.MemberController;
import org.tdd.librarymanagement.repository.mongo.BookMongoRepository;
import org.tdd.librarymanagement.repository.mongo.MemberMongoRepository;
import org.tdd.librarymanagement.view.swing.CombinedSwingView;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class LibrarySwingApp implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address", defaultValue = "localhost")
	private String mongoHost;

	@Option(names = { "--mongo-port" }, description = "MongoDB host port", defaultValue = "27017")
	private int mongoPort;

	@Option(names = { "--db-name" }, description = "Database name", defaultValue = "library")
	private String databaseName;

	@Option(names = { "--db-book-collection" }, description = "Book collection name", defaultValue = "book")
	private String bookCollectionName;

	@Option(names = { "--db-member-collection" }, description = "Member collection name", defaultValue = "member")
	private String memberCollectionName;

	public static void main(String[] args) {
		new CommandLine(new LibrarySwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		SwingUtilities.invokeLater(() -> {
			try {
				MongoClient mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort));

				BookMongoRepository bookRepository = new BookMongoRepository(mongoClient, databaseName,
						bookCollectionName);

				MemberMongoRepository memberRepository = new MemberMongoRepository(mongoClient, databaseName,
						memberCollectionName, bookRepository);

				CombinedSwingView combinedView = new CombinedSwingView();

				BookController bookController = new BookController(combinedView, bookRepository);

				MemberController memberController = new MemberController(combinedView, memberRepository,
						bookRepository);

				combinedView.setBookController(bookController);

				combinedView.setMemberController(memberController);

				combinedView.refreshBookDropdown();

				combinedView.setVisible(true);

				bookController.allBooks();

				memberController.allMembers();

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error initializing application: " + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		return null;
	}
}