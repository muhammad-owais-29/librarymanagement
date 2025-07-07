package org.tdd.librarymanagement.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.controller.MemberController;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.mongo.BookMongoRepository;
import org.tdd.librarymanagement.repository.mongo.MemberMongoRepository;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

@RunWith(org.assertj.swing.junit.runner.GUITestRunner.class)
public class CombinedSwingViewIT extends AssertJSwingJUnitTestCase {

	private static final String DB = "testDB";
	private static final String MEMBER_COLLECTION = "member";
	private static final String BOOK_COLLECTION = "book";
	private static final int MONGO_PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	private MongoClient mongoClient;
	private BookMongoRepository bookRepository;
	private MemberMongoRepository memberRepository;
	private BookController bookController;
	private MemberController memberController;

	private FrameFixture window;
	private CombinedSwingView view;

	@Before
	public void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress("localhost", MONGO_PORT));
		MongoDatabase database = mongoClient.getDatabase(DB);

		for (String collectionName : database.listCollectionNames()) {
			if (collectionName.equals(MEMBER_COLLECTION)) {
				database.getCollection(MEMBER_COLLECTION).drop();
			}
			if (collectionName.equals(BOOK_COLLECTION)) {
				database.getCollection(BOOK_COLLECTION).drop();
			}
		}

		bookRepository = new BookMongoRepository(mongoClient, DB, BOOK_COLLECTION);
		memberRepository = new MemberMongoRepository(mongoClient, DB, MEMBER_COLLECTION, bookRepository);

		view = GuiActionRunner.execute(() -> new CombinedSwingView());
		bookController = new BookController(view, bookRepository);
		memberController = new MemberController(view, memberRepository, bookRepository);
		view.setBookController(bookController);
		view.setMemberController(memberController);

		window = new FrameFixture(robot(), view);
		window.show();
		window.resizeTo(new Dimension(1200, 600));
	}

	@After
	@Override
	public void onTearDown() {
		if (mongoClient != null) {
			try {
				MongoDatabase database = mongoClient.getDatabase(DB);
				MongoIterable<String> collections = database.listCollectionNames();

				for (String collection : collections) {
					if (collection.equals(MEMBER_COLLECTION)) {
						database.getCollection(MEMBER_COLLECTION).drop();
					}
					if (collection.equals(BOOK_COLLECTION)) {
						database.getCollection(BOOK_COLLECTION).drop();
					}
				}
			} catch (IllegalStateException e) {
				System.err.println("MongoClient is already closed: " + e.getMessage());
			} finally {
				mongoClient.close();
				mongoClient = null;
			}
		}

		if (window != null) {
			window.cleanUp();
		}
	}

	@Test
	public void testAddBook() {
		// given
		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("Book added successfully");
		assertThat(window.label("bookStatusLabel").foreground().target()).isEqualTo(Color.GREEN);

		Book book = bookRepository.findBySerialNumber("SN123");
		// assert
		assertThat(book).isNotNull();
		assertThat(book.getName()).isEqualTo("Test Book");
		assertThat(book.getAuthorName()).isEqualTo("Author");
	}

	@Test
	public void testDeleteBook() {
		// given
		Book book = new Book(1, "SN123", "Test Book", "Author", "Genre", new ArrayList<>());
		bookRepository.save(book);
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(book)));

		// when
		window.table("bookTable").selectRows(0);
		window.button("deleteBookButton").click();

		// then
		Book deletedBook = bookRepository.findBySerialNumber("SN123");
		// assert
		assertThat(deletedBook).isNull();
	}

	@Test
	public void testAddMember() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author", "Genre", new ArrayList<>());
		bookRepository.save(book);
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		window.label("memberStatusLabel").requireText("Member added successfully");
		assertThat(window.label("memberStatusLabel").foreground().target()).isEqualTo(Color.GREEN);

		Member member = memberRepository.findById(1);
		// assert
		assertThat(member).isNotNull();
		assertThat(member.getName()).isEqualTo("owais");
		assertThat(member.getBook().getSerialNumber()).isEqualTo("SN1");
	}

	@Test
	public void testDeleteMember() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);
		memberRepository.save(member);
		GuiActionRunner.execute(() -> view.showAllMembers(Arrays.asList(member)));

		// when
		window.table("memberTable").selectRows(0);
		window.button("deleteMemberButton").click();

		// then
		Member deletedMember = memberRepository.findById(1);
		// assert
		assertThat(deletedMember).isNull();
	}

}