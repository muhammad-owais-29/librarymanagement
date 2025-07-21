package org.tdd.librarymanagement.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.tdd.librarymanagement.bdd.LibraryManagementRunnerBDD;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibraryManagementSteps {

	private FrameFixture window;

	@After
	public void tearDown() {
		if (window != null) {
			window.cleanUp();
		}
	}

	@Given("The application is launched")
	public void the_application_is_launched() {
		application("org.tdd.librarymanagement.app.swing.LibrarySwingApp")
				.withArgs("--mongo-port=" + LibraryManagementRunnerBDD.mongoPort, "--db-name=" + DatabaseSteps.DB_NAME,
						"--db-book-collection=" + DatabaseSteps.BOOK_COLLECTION,
						"--db-member-collection=" + DatabaseSteps.MEMBER_COLLECTION)
				.start();

		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Library Management System".equals(frame.getTitle()) && frame.isShowing();
			}
		}).withTimeout(10, TimeUnit.SECONDS).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}

	@When("The user enters book details with id {string}, serial number {string}, name {string}, author {string}, and genre {string}")
	public void the_user_enters_book_details(String id, String serialNumber, String name, String author, String genre) {
		window.textBox("bookIdField").enterText(id);
		window.textBox("bookSerialNumberField").enterText(serialNumber);
		window.textBox("bookNameField").enterText(name);
		window.textBox("bookAuthorField").enterText(author);
		window.textBox("bookGenreField").enterText(genre);
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String buttonName) {
		window.button(buttonName).click();
	}

	@Then("The book status should show {string}")
	public void the_book_status_should_show(String expectedStatus) {
		window.label("bookStatusLabel").requireText(expectedStatus);
	}

	@Then("The book table should contain a book with id {string}, serial number {string}, name {string}, author {string}, and genre {string}")
	public void the_book_table_should_contain_a_book(String id, String serialNumber, String name, String author,
			String genre) {
		JTableFixture bookTable = window.table("bookTable");
		bookTable.requireRowCount(1);

		assertThat(bookTable.valueAt(TableCell.row(0).column(0))).isEqualTo(id);
		assertThat(bookTable.valueAt(TableCell.row(0).column(1))).isEqualTo(serialNumber);
		assertThat(bookTable.valueAt(TableCell.row(0).column(2))).isEqualTo(name);
		assertThat(bookTable.valueAt(TableCell.row(0).column(3))).isEqualTo(author);
		assertThat(bookTable.valueAt(TableCell.row(0).column(4))).isEqualTo(genre);
	}

	@When("The user enters member details with id {string}, name {string}, email {string}")
	public void the_user_enters_member_details(String id, String name, String email) {
		window.textBox("memberIdField").enterText(id);
		window.textBox("memberNameField").enterText(name);
		window.textBox("memberEmailField").enterText(email);
	}

	@When("The user selects book {string} from dropdown")
	public void the_user_selects_book_from_dropdown(String bookName) {
		window.comboBox("bookDropdown").selectItem(bookName);
	}

	@Then("The member status should show {string}")
	public void the_member_status_should_show(String expectedStatus) {
		window.label("memberStatusLabel").requireText(expectedStatus);
	}

	@Then("The member table should contain a member with id {string}, name {string}, email {string}, and book {string}")
	public void the_member_table_should_contain(String id, String name, String email, String bookName) {
		JTableFixture memberTable = window.table("memberTable");
		memberTable.requireRowCount(1);

		assertThat(memberTable.valueAt(TableCell.row(0).column(0))).isEqualTo(id);
		assertThat(memberTable.valueAt(TableCell.row(0).column(1))).isEqualTo(name);
		assertThat(memberTable.valueAt(TableCell.row(0).column(2))).isEqualTo(email);
		assertThat(memberTable.valueAt(TableCell.row(0).column(3))).isEqualTo(bookName);
	}

}
