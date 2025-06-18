package org.tdd.librarymanagement.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.controller.MemberController;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;

@RunWith(GUITestRunner.class)
public class CombinedSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	@Mock
	private BookController bookController;
	@Mock
	private MemberController memberController;

	private CombinedSwingView view;

	@Before
	public void onSetUp() {
		MockitoAnnotations.openMocks(this);

		GuiActionRunner.execute(() -> {
			view = new CombinedSwingView();
			view.setBookController(bookController);
			view.setMemberController(memberController);
			view.pack();
			view.setLocationRelativeTo(null);
			return view;
		});

		window = new FrameFixture(robot(), view);
		window.show();
		window.resizeTo(new Dimension(1200, 600));
	}

	@After
	public void onTearDown() {
		window.cleanUp();
	}

	@Test
	@GUITest
	public void testControlsInitialStates() {
		// Verify all components are in correct initial state
		window.textBox("bookIdField").requireEmpty();
		window.textBox("bookSerialNumberField").requireEmpty();
		window.textBox("bookNameField").requireEmpty();
		window.textBox("bookAuthorField").requireEmpty();
		window.textBox("bookGenreField").requireEmpty();

		window.button("addBookButton").requireEnabled();
		window.button("updateBookButton").requireEnabled();
		window.table("bookTable").requireRowCount(0);

		window.textBox("memberIdField").requireEmpty();
		window.textBox("memberNameField").requireEmpty();
		window.textBox("memberEmailField").requireEmpty();

		window.comboBox("bookDropdown").requireSelection("Please select a book");
		window.button("searchMemberButton").requireVisible();
		window.table("memberTable").requireRowCount(0);
	}

	@Test
	public void testAddBookSuccess() {
		// given
		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController).newBook(any(Book.class));
		window.label("bookStatusLabel").requireText("Book added successfully");
		assertThat(window.label("bookStatusLabel").foreground().target()).isEqualTo(Color.GREEN);
	}

	@Test
	public void testAddBookInvalidId() {
		// given
		window.textBox("bookIdField").enterText("abc");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("ID must be a number");
		assertThat(window.label("bookStatusLabel").foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testAddBookNegativeId() {
		// given
		window.textBox("bookIdField").enterText("-1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("ID must be positive");
	}

	@Test
	public void testAddBookZeroId() {
		// given
		window.textBox("bookIdField").enterText("0");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("ID must be positive");
	}

	@Test
	public void testAddBook_SerialNumberEmpty_ShowsError() {
		// given

		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddBook_NameEmpty_ShowsError() {
		// given

		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddBook_AuthorEmpty_ShowsError() {
		// given

		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("");
		window.textBox("bookGenreField").enterText("Fiction");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddBook_GenreEmpty_ShowsError() {
		// given

		window.textBox("bookIdField").enterText("1");
		window.textBox("bookSerialNumberField").enterText("SN123");
		window.textBox("bookNameField").enterText("Test Book");
		window.textBox("bookAuthorField").enterText("Author");
		window.textBox("bookGenreField").enterText("");

		// when
		window.button("addBookButton").click();

		// then
		verify(bookController, never()).newBook(any());
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testUpdateBook200() {
		// given
		Book book = new Book(1, "SN123", "Old Book", "Old Author", "Old Genre", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(book)));
		window.table("bookTable").selectRows(0);

		// when
		window.textBox("bookNameField").setText("New Book");
		window.textBox("bookAuthorField").setText("New Author");
		window.textBox("bookGenreField").setText("New Genre");
		window.button("updateBookButton").click();

		// then
		Book expectedBook = new Book(1, "SN123", "New Book", "New Author", "New Genre", new ArrayList<>());
		verify(bookController).updateBook(expectedBook);
	}

	@Test
	public void testUpdateBook_ClearFieldsAfterUpdating() {
		// given
		Book book = new Book(1, "SN123", "Old Book", "Old Author", "Old Genre", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(book)));

		// when
		window.table("bookTable").selectRows(0);
		window.button("updateBookButton").click();

		// then
		window.textBox("bookIdField").requireEmpty();
		window.textBox("bookSerialNumberField").requireEmpty();
		window.textBox("bookNameField").requireEmpty();
		window.textBox("bookAuthorField").requireEmpty();
		window.textBox("bookGenreField").requireEmpty();
	}

	@Test
	public void testUpdateBookNoSelection() {
		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("No book selected");
		assertThat(window.label("bookStatusLabel").foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testUpdateBook_EmptyFields() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookNameField").setText("");

		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testUpdateBook_InvalidId() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookIdField").setText("abc");

		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("ID must be a number");
		assertThat(window.label("bookStatusLabel").foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testUpdateBook_EmptySerialNumber_ShowsError() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", null);
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookSerialNumberField").setText("");

		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testUpdateBook_EmptyAuthor_ShowsError() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", null);
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookAuthorField").setText("");

		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testUpdateBook_EmptyGenre_ShowsError() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", null);
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookGenreField").setText("");

		// when
		window.button("updateBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testDeleteBook() {
		// given
		Book book = new Book(1, "SN123", "Test Book", "Author", "Genre", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(book)));

		// when
		window.table("bookTable").selectRows(0);
		window.button("deleteBookButton").click();

		// then
		Book expectedBook = new Book(0, "SN123", null, null, null, null);
		verify(bookController).deleteBook(expectedBook);
	}

	@Test
	public void testDeleteBook_NoSelection() {
		// when
		window.button("deleteBookButton").click();

		// then
		window.label("bookStatusLabel").requireText("No book selected");
	}

	@Test
	public void testShowAllBooks_ButtonClicked() {
		// when
		window.button("showAllBooksButton").click();

		// then
		verify(bookController).allBooks();
	}

	@Test
	public void testShowAllBooks200() {
		// given
		Book book1 = new Book(1, "SN1", "Book1", "Author1", "Genre1", new ArrayList<>());
		Book book2 = new Book(2, "SN2", "Book2", "Author2", "Genre2", new ArrayList<>());

		// when
		GuiActionRunner.execute(() -> view.showAllBooks(Arrays.asList(book1, book2)));

		// then
		JTableFixture table = window.table("bookTable");
		assertThat(table.valueAt(TableCell.row(0).column(0))).isEqualTo("1");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("SN1");
		assertThat(table.valueAt(TableCell.row(1).column(0))).isEqualTo("2");
		assertThat(table.valueAt(TableCell.row(1).column(1))).isEqualTo("SN2");
	}

	@Test
	public void testSearchBook_ControllerSearchBook() {
		// given
		String testSerialNumber = "SN123";

		// when
		window.button("searchBookButton").click();
		JOptionPaneFixture optionPane = window.optionPane();
		optionPane.textBox().enterText(testSerialNumber);
		optionPane.okButton().click();

		// then
		verify(bookController).searchBook(testSerialNumber);
	}

	@Test
	public void testSearchBookEmptyInput() {
		// when
		window.button("searchBookButton").click();
		JOptionPaneFixture optionPane = window.optionPane();
		optionPane.textBox().enterText("");
		optionPane.okButton().click();

		// then
		verify(bookController, never()).searchBook(any());
		window.label("bookStatusLabel").requireText("Please enter a serial number");
	}

	@Test
	public void testSearchBook_CancelDialog() {
		// when
		window.button("searchBookButton").click();
		window.optionPane().cancelButton().click();

		// then
		verify(bookController, never()).searchBook(any());
	}

	@Test
	public void testShowSearchedBooks_ShowBookInTable() {
		// given
		Book book = new Book(1, "SN123", "Book", "Author", "Genre", null);

		// when
		GuiActionRunner.execute(() -> view.showSearchedBooks(book));

		// then
		JTableFixture table = window.table("bookTable");
		assertThat(table.valueAt(TableCell.row(0).column(2))).isEqualTo("Book");
	}

	@Test
	public void testShowSearchedBooks_Null_ClearTable() {
		// when
		GuiActionRunner.execute(() -> view.showSearchedBooks(null));

		// then
		JTableFixture table = window.table("bookTable");
		table.requireRowCount(0);
	}

	@Test
	public void testBookAdded_RefreshBookTable() {
		// given
		Book book1 = new Book(1, "SN1", "Book1", "Author1", "Genre1", new ArrayList<>());

		// when
		GuiActionRunner.execute(() -> view.bookAdded(book1));

		// then
		verify(bookController).allBooks();
	}

	@Test
	public void testBookRemoved_RefreshBookTable() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author1", "Genre1", new ArrayList<>());

		// when
		GuiActionRunner.execute(() -> view.bookRemoved(book));

		// then
		verify(bookController).allBooks();
	}

	@Test
	public void testRefreshBookDropdown_UpdateComboBox() {
		// given
		Book book1 = new Book(1, "SN1", "Book1", "Author1", "Genre1", new ArrayList<>());
		Book book2 = new Book(2, "SN2", "Book2", "Author2", "Genre2", new ArrayList<>());
		when(bookController.allBooks()).thenReturn(Arrays.asList(book1, book2));

		// when
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		// then
		JComboBoxFixture comboBox = window.comboBox("bookDropdown");
		comboBox.requireItemCount(3);
	}

	@Test
	public void testPopulateBookDropdown_NoBooks() {
		// given
		when(bookController.allBooks()).thenReturn(Collections.emptyList());

		// when
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		// then
		JComboBoxFixture comboBox = window.comboBox("bookDropdown");
		comboBox.requireItemCount(1);
	}

	@Test
	public void testPopulateBookDropdown_NullBookList() {
		// given
		when(bookController.allBooks()).thenReturn(null);

		// when
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		// then
		JComboBoxFixture comboBox = window.comboBox("bookDropdown");
		comboBox.requireItemCount(1);
	}

	@Test
	public void testBookDropdown_CorrectRenderer() {
		// when
		JComboBoxFixture combo = window.comboBox("bookDropdown");
		Component comp = GuiActionRunner.execute(() -> {
			DefaultListCellRenderer renderer = (DefaultListCellRenderer) combo.target().getRenderer();
			return renderer.getListCellRendererComponent(new JList<>(), "NotABook", 0, false, false);
		});

		// then
		assertThat(comp).isInstanceOf(JLabel.class);
		assertThat(((JLabel) comp).getText()).isEqualTo("NotABook");
	}

	@Test
	public void testShowErrorBookNotFound_WithBook() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author", "Genre", new ArrayList<>());

		// when
		GuiActionRunner.execute(() -> view.showErrorBookNotFound("Error", book));

		// then
		JLabelFixture statusLabel = window.label("bookStatusLabel");
		assertThat(statusLabel.foreground().target()).isEqualTo(Color.RED);
		statusLabel.requireText("Error");
	}

	@Test
	public void testShowErrorBookNotFound() {
		// when
		GuiActionRunner.execute(() -> view.showErrorBookNotFound("Book not found!"));

		// then
		JLabelFixture label = window.label("bookStatusLabel");
		label.requireText("Book not found!");
		assertThat(label.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testShowBookError_SetErrorMessage() {
		// when
		GuiActionRunner.execute(() -> view.showBookError("Book processing failed", null));

		// then
		JLabelFixture bookStatusLabel = window.label("bookStatusLabel");
		bookStatusLabel.requireText("Book processing failed");
		assertThat(bookStatusLabel.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testBookTableSelectionListener_DoesNothingWhenAdjustingOrNone() {
		// given
		Book book = new Book(10, "SN10", "Book10", "Author10", "Genre10", new ArrayList<>());
		GuiActionRunner.execute(() -> view.showAllBooks(Collections.singletonList(book)));
		window.table("bookTable").selectRows(0);
		window.textBox("bookIdField").requireText("10");

		// when (isAdjusting = true)
		GuiActionRunner.execute(() -> {
			ListSelectionListener listener = view.getBookTableSelectionListener();
			ListSelectionEvent adjustingEvent = new ListSelectionEvent(view.getBookTable(), 0, 0, true);
			listener.valueChanged(adjustingEvent);
		});

		// then
		window.textBox("bookIdField").requireText("10");

		// when (No row selected)
		GuiActionRunner.execute(() -> {
			view.getBookTable().clearSelection();
			ListSelectionListener listener = view.getBookTableSelectionListener();
			ListSelectionEvent noRowEvent = new ListSelectionEvent(view.getBookTable(), -1, -1, false);
			listener.valueChanged(noRowEvent);
		});

		// then
		window.textBox("bookIdField").requireText("");
	}

	@Test
	public void testAddMemberSuccess() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author", "Genre", new ArrayList<>());
		when(bookController.allBooks()).thenReturn(Collections.singletonList(book));
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController).newMember(any(Member.class));
		window.label("memberStatusLabel").requireText("Member added successfully");
		assertThat(window.label("memberStatusLabel").foreground().target()).isEqualTo(Color.GREEN);
	}

	@Test
	public void testAddMember_ClearFieldsAfterAdding() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author1", "Genre1", new ArrayList<>());
		when(bookController.allBooks()).thenReturn(Arrays.asList(book));
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		window.textBox("memberIdField").requireEmpty();
		window.textBox("memberNameField").requireEmpty();
		window.textBox("memberEmailField").requireEmpty();
		window.comboBox("bookDropdown").requireSelection("Please select a book");
	}

	@Test
	public void testAddMember_InvalidId() {
		// given
		window.textBox("memberIdField").enterText("abc");

		// when
		window.button("addMemberButton").click();

		// then
		JLabelFixture statusLabel = window.label("memberStatusLabel");
		statusLabel.requireText("ID must be a number");
		assertThat(statusLabel.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testAddMember_NegativeId_ShowsError() {
		// given
		window.textBox("memberIdField").enterText("-5");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).newMember(any());
		window.label("memberStatusLabel").requireText("ID must be positive");
	}

	@Test
	public void testAddMember_ZeroId_ShowsError() {
		// given
		window.textBox("memberIdField").enterText("0");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).newMember(any());
		window.label("memberStatusLabel").requireText("ID must be positive");
	}

	@Test
	public void testAddMember_EmptyFields() {
		// given
		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("");

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).newMember(any());
		window.label("memberStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddMember_NameEmpty_ShowsError() {
		// given
		window.textBox("memberIdField").enterText("5");
		window.textBox("memberNameField").setText("");
		window.textBox("memberEmailField").enterText("owais@gmail.com");

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).newMember(any());
		window.label("memberStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddMember_EmailEmpty_ShowsError() {
		// given
		window.textBox("memberIdField").enterText("5");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").setText("");

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).newMember(any());
		window.label("memberStatusLabel").requireText("All fields must be filled");
	}

	@Test
	public void testAddMember_DoesNotBorrow() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author", "Genre", new ArrayList<>());
		Member otherMember = new Member(2, "Other", "other@gamil.com", null);
		when(bookController.allBooks()).thenReturn(Collections.singletonList(book));
		when(memberController.allMembers()).thenReturn(Collections.singletonList(otherMember));
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).borrowBook(any(Member.class), any(Book.class));
		window.label("memberStatusLabel").requireText("Member added successfully");
	}

	@Test
	public void testAddMember_WithNoBookSelected_DoesNotCallBorrowBook() {
		// given
		when(bookController.allBooks()).thenReturn(Collections.emptyList());
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("11");
		window.textBox("memberNameField").enterText("NoBookMem");
		window.textBox("memberEmailField").enterText("nobookmem@gmail.com");
		window.comboBox("bookDropdown").selectItem(0);

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).borrowBook(any(Member.class), any(Book.class));
		window.label("memberStatusLabel").requireText("Member added successfully");
	}

	@Test
	public void testAddMember_WithBookSelection_CallsBorrowBook() {
		// given
		Book book = new Book(1, "SN1", "Book1", "Author", "Genre", new ArrayList<>());
		Member member = new Member(1, "owais", "owais@gmail.com", null);
		when(bookController.allBooks()).thenReturn(Collections.singletonList(book));
		when(memberController.allMembers()).thenReturn(Collections.singletonList(member));
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("1");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController).borrowBook(any(Member.class), eq(book));
	}

	@Test
	public void testAddMember_WithBookSelection() {
		// given
		Book book = new Book(2, "SN2", "Book2", "Author2", "Genre2", null);
		when(bookController.allBooks()).thenReturn(Collections.singletonList(book));
		when(memberController.allMembers()).thenReturn(Collections.emptyList());
		GuiActionRunner.execute(() -> view.refreshBookDropdown());

		window.textBox("memberIdField").enterText("88");
		window.textBox("memberNameField").enterText("owais");
		window.textBox("memberEmailField").enterText("owais@gmail.com");
		window.comboBox("bookDropdown").selectItem(1);

		// when
		window.button("addMemberButton").click();

		// then
		verify(memberController, never()).borrowBook(any(Member.class), eq(book));
		window.label("memberStatusLabel").requireText("Member added successfully");
	}

	@Test
	public void testDeleteMember() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);
		GuiActionRunner.execute(() -> view.showAllMembers(Arrays.asList(member)));

		// when
		window.table("memberTable").selectRows(0);
		window.button("deleteMemberButton").click();

		// then
		ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
		verify(memberController).deleteMember(captor.capture());
		assertThat(captor.getValue().getId()).isEqualTo(1);
	}

	@Test
	public void testDeleteMember_NoSelection() {
		// when
		window.button("deleteMemberButton").click();

		// then
		window.label("memberStatusLabel").requireText("No member selected");
	}

	@Test
	public void testShowAllMembers_ButtonClicked() {
		// when
		window.button("showAllMembersButton").click();

		// then
		verify(memberController).allMembers();
	}

	@Test
	public void testShowAllMembers_BookNotFound() {
		// given
		Book book = new Book(123, "S123", "SomeBook", "Auth", "Genre", null);
		Member member = new Member(8, "owais", "owais@gmail.com", book);
		when(bookController.findById(123)).thenReturn(null);

		// when
		GuiActionRunner.execute(() -> view.showAllMembers(Collections.singletonList(member)));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Unknown");
	}

	@Test
	public void testShowAllMembers_ShowsNone() {
		// given
		Member member = new Member(8, "owais", "owais@gmail.com", null);

		// when
		GuiActionRunner.execute(() -> view.showAllMembers(Collections.singletonList(member)));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("None");
	}

	@Test
	public void testShowAllMembers_BookFound() {
		// given
		Book book = new Book(123, "S123", "Java Book", "Auth", "Genre", null);
		Member member = new Member(9, "Ali", "ali@test.com", book);
		when(bookController.findById(123)).thenReturn(book);

		// when
		GuiActionRunner.execute(() -> view.showAllMembers(Collections.singletonList(member)));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Java Book");
	}

	@Test
	public void testSearchMember_validInput() {
		// when
		window.button("searchMemberButton").click();
		JOptionPaneFixture optionPane = window.optionPane();
		optionPane.textBox().enterText("123");
		optionPane.okButton().click();

		// then
		verify(memberController).searchMember(123);
	}

	@Test
	public void testSearchMember_emptyInput_showsErrorMessage() {
		// when
		window.button("searchMemberButton").click();
		JOptionPaneFixture optionPane = window.optionPane();
		optionPane.textBox().enterText("");
		optionPane.okButton().click();

		// then
		verify(memberController, never()).searchMember(anyInt());
		window.label("memberStatusLabel").requireText("Please enter an ID");
		assertThat(window.label("memberStatusLabel").foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testSearchMember_cancelDialog() {
		// when
		window.button("searchMemberButton").click();
		window.optionPane().cancelButton().click();

		// then
		verify(memberController, never()).searchMember(anyInt());
	}

	@Test
	public void testSearchMember_nonNumericInput_showsErrorMessage() {
		// when
		window.button("searchMemberButton").click();
		JOptionPaneFixture optionPane = window.optionPane();
		optionPane.textBox().enterText("abc123");
		optionPane.okButton().click();

		// then
		verify(memberController, never()).searchMember(anyInt());
		window.label("memberStatusLabel").requireText("ID must be a number");
		assertThat(window.label("memberStatusLabel").foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testShowSearchedMembers_WithBook_ShowInTable() {
		// given
		Book book = new Book(101, "SN1", "Book1", "Author", "Genre", null);
		Member member = new Member(1, "owais", "owais@gmail.com", book);
		when(bookController.findById(101)).thenReturn(book);

		// when
		GuiActionRunner.execute(() -> view.showSearchedMembers(member));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("owais");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Book1");
	}

	@Test
	public void testShowSearchedMembers_WithoutBook_ShowNone() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);

		// when
		GuiActionRunner.execute(() -> view.showSearchedMembers(member));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("owais");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("None");
	}

	@Test
	public void testShowSearchedMembers_Null_ClearTable() {
		// when
		GuiActionRunner.execute(() -> view.showSearchedMembers(null));

		// then
		JTableFixture table = window.table("memberTable");
		table.requireRowCount(0);
	}

	@Test
	public void testShowSearchedMembers_BookNotFound() {
		// given
		Book book = new Book(101, "S101", "Java", "Author", "Tech", null);
		Member member = new Member(1, "owais", "owais@gmail.com", book);
		when(bookController.findById(101)).thenReturn(null);

		// when
		GuiActionRunner.execute(() -> view.showSearchedMembers(member));

		// then
		JTableFixture table = window.table("memberTable");
		assertThat(table.valueAt(TableCell.row(0).column(1))).isEqualTo("owais");
		assertThat(table.valueAt(TableCell.row(0).column(3))).isEqualTo("Unknown");
	}

	@Test
	public void testMemberAdded_RefreshMemberTable() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);

		// when
		GuiActionRunner.execute(() -> view.memberAdded(member));

		// then
		verify(memberController).allMembers();
	}

	@Test
	public void testMemberRemoved_RefreshMemberTable() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);

		// when
		GuiActionRunner.execute(() -> view.memberRemoved(member));

		// then
		verify(memberController).allMembers();
	}

	@Test
	public void testShowMemberError_WithMember() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);

		// when
		GuiActionRunner.execute(() -> view.showMemberError("Error", member));

		// then
		JLabelFixture label = window.label("memberStatusLabel");
		label.requireText("Error");
		assertThat(label.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testShowErrorMemberNotFound_SetErrorMessage() {
		// when
		GuiActionRunner.execute(() -> view.showErrorMemberNotFound("No member found"));

		// then
		JLabelFixture memberStatusLabel = window.label("memberStatusLabel");
		memberStatusLabel.requireText("No member found");
		assertThat(memberStatusLabel.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testShowErrorMemberNotFound() {
		// when
		GuiActionRunner.execute(() -> view.showErrorMemberNotFound("No member exists with ID 1",
				new Member(1, "owais", "owais@gmail.com", null)));

		// then
		JLabelFixture memberStatusLabel = window.label("memberStatusLabel");
		memberStatusLabel.requireText("No member exists with ID 1");
		assertThat(memberStatusLabel.foreground().target()).isEqualTo(Color.RED);
	}

	@Test
	public void testMemberTableSelectionListener_IgnoredWhenAdjustingTrue() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);
		GuiActionRunner.execute(() -> view.showAllMembers(Collections.singletonList(member)));
		window.table("memberTable").selectRows(0);
		window.textBox("memberIdField").requireText("1");

		// when
		GuiActionRunner.execute(() -> {
			ListSelectionEvent event = new ListSelectionEvent(view.getMemberTable(), 0, 0, true);
			view.getMemberTableSelectionListener().valueChanged(event);
		});

		// then
		window.textBox("memberIdField").requireText("1");
	}

	@Test
	public void testMemberTableSelectionListener_ClearsWhenNoRowSelected() {
		// given
		Member member = new Member(1, "owais", "owais@gmail.com", null);
		GuiActionRunner.execute(() -> view.showAllMembers(Collections.singletonList(member)));
		window.table("memberTable").selectRows(0);
		window.textBox("memberIdField").requireText("1");

		// when
		GuiActionRunner.execute(() -> {
			view.getMemberTable().clearSelection();
			ListSelectionEvent event = new ListSelectionEvent(view.getMemberTable(), -1, -1, false);
			view.getMemberTableSelectionListener().valueChanged(event);
		});
		robot().waitForIdle();

		// then
		window.textBox("memberIdField").requireText("");
	}

	@Test
	public void testShowMessage_SetsStatusLabels() {
		// when
		GuiActionRunner.execute(() -> view.showMessage("Success!"));

		// then
		JLabelFixture bookStatusLabel = window.label("bookStatusLabel");
		JLabelFixture memberStatusLabel = window.label("memberStatusLabel");
		bookStatusLabel.requireText("Success!");
		memberStatusLabel.requireText("Success!");
	}
}