package org.tdd.librarymanagement.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.controller.MemberController;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.view.BookView;

public class CombinedSwingView extends JFrame implements BookView {
	private static final long serialVersionUID = 1L;

	// Constants for error messages
	private static final String ALL_FIELDS_REQUIRED = "All fields must be filled";
	private static final String ID_MUST_BE_NUMBER = "ID must be a number";
	private static final String SELECT_BOOK = "Please select a book";

	private transient BookController bookController;
	private transient MemberController memberController;

	// Input fields
	private JTextField bookIdField;
	private JTextField bookSerialNumberField;
	private JTextField bookNameField;
	private JTextField bookAuthorField;
	private JTextField bookGenreField;

	private JTextField memberIdField;
	private JTextField memberNameField;
	private JTextField memberEmailField;

	// Status labels
	private JLabel bookStatusLabel;
	private JLabel memberStatusLabel;

	// Tables
	private JTable bookTable;
	private DefaultTableModel bookTableModel;
	private JTable memberTable;
	private DefaultTableModel memberTableModel;

	public void setBookController(BookController bookController) {
		this.bookController = bookController;
	}

	public void setMemberController(MemberController memberController) {
		this.memberController = memberController;
	}

	public JTable getBookTable() {
		return bookTable;
	}

	public JTable getMemberTable() {
		return memberTable;
	}

	// Add these fields:
	private transient ListSelectionListener bookTableSelectionListener;
	private transient ListSelectionListener memberTableSelectionListener;

	public ListSelectionListener getBookTableSelectionListener() {
		return bookTableSelectionListener;
	}

	public ListSelectionListener getMemberTableSelectionListener() {
		return memberTableSelectionListener;
	}

	// Dropdown
	private JComboBox<Book> bookDropdown;

	public CombinedSwingView() {
		setTitle("Library Management System");
		setSize(1200, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Status panel at bottom
		JPanel statusPanel = new JPanel(new GridLayout(1, 2));
		bookStatusLabel = new JLabel(" ", SwingConstants.CENTER);
		bookStatusLabel.setName("bookStatusLabel");
		bookStatusLabel.setForeground(Color.RED);

		memberStatusLabel = new JLabel(" ", SwingConstants.CENTER);
		memberStatusLabel.setName("memberStatusLabel");
		memberStatusLabel.setForeground(Color.RED);

		statusPanel.add(bookStatusLabel);
		statusPanel.add(memberStatusLabel);
		add(statusPanel, BorderLayout.SOUTH);

		// Split pane for book/member panels
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(createBookPanel());
		splitPane.setRightComponent(createMemberPanel());
		add(splitPane, BorderLayout.CENTER);

		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(600));

	}

	private JPanel createBookPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Book Form
		JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
		formPanel.setBorder(BorderFactory.createTitledBorder("Book Form"));

		// Add all book form components
		formPanel.add(new JLabel("ID:"));
		bookIdField = new JTextField();
		bookIdField.setName("bookIdField");
		formPanel.add(bookIdField);

		formPanel.add(new JLabel("Serial Number:"));
		bookSerialNumberField = new JTextField();
		bookSerialNumberField.setName("bookSerialNumberField");
		formPanel.add(bookSerialNumberField);

		formPanel.add(new JLabel("Name:"));
		bookNameField = new JTextField();
		bookNameField.setName("bookNameField");
		formPanel.add(bookNameField);

		formPanel.add(new JLabel("Author:"));
		bookAuthorField = new JTextField();
		bookAuthorField.setName("bookAuthorField");
		formPanel.add(bookAuthorField);

		formPanel.add(new JLabel("Genre:"));
		bookGenreField = new JTextField();
		bookGenreField.setName("bookGenreField");
		formPanel.add(bookGenreField);

		JButton addButton = new JButton("Add Book");
		addButton.setName("addBookButton");
		addButton.addActionListener(e -> addBook());
		formPanel.add(addButton);

		JButton updateButton = new JButton("Update Book");
		updateButton.setName("updateBookButton");
		updateButton.addActionListener(e -> updateBook());
		formPanel.add(updateButton);

		panel.add(formPanel, BorderLayout.NORTH);

		// Book Table
		bookTableModel = new DefaultTableModel(new String[] { "ID", "Serial Number", "Name", "Author", "Genre" }, 0);
		bookTable = new JTable(bookTableModel);
		bookTable.setName("bookTable");

		bookTableSelectionListener = e -> {
			if (!e.getValueIsAdjusting()) {
				int row = bookTable.getSelectedRow();
				if (row != -1) {
					bookIdField.setText(bookTableModel.getValueAt(row, 0).toString());
					bookSerialNumberField.setText(bookTableModel.getValueAt(row, 1).toString());
					bookNameField.setText(bookTableModel.getValueAt(row, 2).toString());
					bookAuthorField.setText(bookTableModel.getValueAt(row, 3).toString());
					bookGenreField.setText(bookTableModel.getValueAt(row, 4).toString());
				} else {
					clearBookFields();
				}
			}
		};
		bookTable.getSelectionModel().addListSelectionListener(bookTableSelectionListener);

		panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

		// Button panel
		JPanel buttonPanel = new JPanel();

		JButton deleteBookButton = new JButton("Delete Book");
		deleteBookButton.setName("deleteBookButton");
		deleteBookButton.addActionListener(e -> deleteBook());
		buttonPanel.add(deleteBookButton);

		JButton showAllBooksButton = new JButton("Show All Books");
		showAllBooksButton.setName("showAllBooksButton");
		showAllBooksButton.addActionListener(e -> showAllBooks());
		buttonPanel.add(showAllBooksButton);

		JButton searchBookButton = new JButton("Search Book");
		searchBookButton.setName("searchBookButton");
		searchBookButton.addActionListener(e -> searchBook());
		buttonPanel.add(searchBookButton);

		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createMemberPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Member Form
		JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
		formPanel.setBorder(BorderFactory.createTitledBorder("Member Form"));

		formPanel.add(new JLabel("ID:"));
		memberIdField = new JTextField();
		memberIdField.setName("memberIdField");
		formPanel.add(memberIdField);

		formPanel.add(new JLabel("Name:"));
		memberNameField = new JTextField();
		memberNameField.setName("memberNameField");
		formPanel.add(memberNameField);

		formPanel.add(new JLabel("Email:"));
		memberEmailField = new JTextField();
		memberEmailField.setName("memberEmailField");
		formPanel.add(memberEmailField);

		formPanel.add(new JLabel("Borrow Book:"));
		bookDropdown = new JComboBox<>();
		bookDropdown.setName("bookDropdown");
		bookDropdown.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Book) {
					Book book = (Book) value;
					if (book.getId() == -1) {
						setText(SELECT_BOOK);
						setEnabled(false);
					} else {
						setText(book.getName());
					}
				}
				return this;
			}
		});
		populateBookDropdown();
		formPanel.add(bookDropdown);
		JButton addMemberButton = new JButton("Add Member");
		addMemberButton.setName("addMemberButton");
		addMemberButton.addActionListener(e -> addMember());
		formPanel.add(addMemberButton);

		panel.add(formPanel, BorderLayout.NORTH);

		// Member Table
		memberTableModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Borrowed Books" }, 0);
		memberTable = new JTable(memberTableModel);
		memberTable.setName("memberTable");
		memberTableSelectionListener = e -> {
			if (!e.getValueIsAdjusting()) {
				int row = memberTable.getSelectedRow();
				if (row != -1) {
					memberIdField.setText(memberTableModel.getValueAt(row, 0).toString());
					memberNameField.setText(memberTableModel.getValueAt(row, 1).toString());
					memberEmailField.setText(memberTableModel.getValueAt(row, 2).toString());
				} else {
					clearMemberFields();
				}
			}
		};

		memberTable.getSelectionModel().addListSelectionListener(memberTableSelectionListener);

		panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

		// Button panel
		JPanel buttonPanel = new JPanel();

		JButton deleteMemberButton = new JButton("Delete Member");
		deleteMemberButton.setName("deleteMemberButton");
		deleteMemberButton.addActionListener(e -> deleteMember());
		buttonPanel.add(deleteMemberButton);

		JButton showAllMembersButton = new JButton("Show All Members");
		showAllMembersButton.setName("showAllMembersButton");
		showAllMembersButton.addActionListener(e -> showAllMembers());
		buttonPanel.add(showAllMembersButton);

		JButton searchMemberButton = new JButton("Search Member");
		searchMemberButton.setName("searchMemberButton");
		searchMemberButton.addActionListener(e -> searchMember());
		buttonPanel.add(searchMemberButton);

		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;
	}

	private void populateBookDropdown() {
		DefaultComboBoxModel<Book> model = new DefaultComboBoxModel<>();
		bookDropdown.setModel(model);

		model.addElement(new Book(-1, "", SELECT_BOOK, "", "", new ArrayList<>()));

		if (bookController != null) {
			List<Book> books = bookController.allBooks();
			if (books != null) {
				for (Book book : books) {
					model.addElement(book);
				}
			}
		}
		bookDropdown.setSelectedIndex(0);
	}

	private void addBook() {
		try {
			int id = Integer.parseInt(bookIdField.getText());
			String serialNumber = bookSerialNumberField.getText();
			String name = bookNameField.getText();
			String author = bookAuthorField.getText();
			String genre = bookGenreField.getText();

			if (id <= 0) {
				bookStatusLabel.setText("ID must be positive");
				bookStatusLabel.setForeground(Color.RED);
				return;
			}

			if (serialNumber.isEmpty() || name.isEmpty() || author.isEmpty() || genre.isEmpty()) {
				bookStatusLabel.setText(ALL_FIELDS_REQUIRED);
				bookStatusLabel.setForeground(Color.RED);
				return;
			}

			Book book = new Book(id, serialNumber, name, author, genre, new ArrayList<>());
			bookController.newBook(book);
			bookStatusLabel.setText("Book added successfully");
			bookStatusLabel.setForeground(Color.GREEN);
			clearBookFields();
			refreshBookDropdown();

		} catch (NumberFormatException e) {
			bookStatusLabel.setText(ID_MUST_BE_NUMBER);
			bookStatusLabel.setForeground(Color.RED);
		}
	}

	private void updateBook() {
		int selectedRow = bookTable.getSelectedRow();
		if (selectedRow < 0) {
			bookStatusLabel.setText("No book selected");
			bookStatusLabel.setForeground(Color.RED);
			return;
		}

		try {
			int id = Integer.parseInt(bookIdField.getText());
			String serialNumber = bookSerialNumberField.getText();
			String name = bookNameField.getText();
			String author = bookAuthorField.getText();
			String genre = bookGenreField.getText();

			if (serialNumber.isEmpty() || name.isEmpty() || author.isEmpty() || genre.isEmpty()) {
				bookStatusLabel.setText(ALL_FIELDS_REQUIRED);
				bookStatusLabel.setForeground(Color.RED);
				return;
			}

			Book book = new Book(id, serialNumber, name, author, genre, null);
			bookController.updateBook(book);
			bookStatusLabel.setText("Book updated successfully");
			bookStatusLabel.setForeground(Color.GREEN);
			clearBookFields();

		} catch (NumberFormatException e) {
			bookStatusLabel.setText(ID_MUST_BE_NUMBER);
			bookStatusLabel.setForeground(Color.RED);
		}
	}

	private void deleteBook() {
		int selectedRow = bookTable.getSelectedRow();
		if (selectedRow < 0) {
			bookStatusLabel.setText("No book selected");
			bookStatusLabel.setForeground(Color.RED);
			return;
		}

		String serialNumber = bookTableModel.getValueAt(selectedRow, 1).toString();
		Book book = new Book(0, serialNumber, null, null, null, null);
		bookController.deleteBook(book);
		bookStatusLabel.setText("Book deleted successfully");
		bookStatusLabel.setForeground(Color.GREEN);
	}

	private void showAllBooks() {
		List<Book> books = bookController.allBooks();
		showAllBooks(books);
	}

	private void searchBook() {
		String serialNumber = JOptionPane.showInputDialog("Enter Book Serial Number to search:");
		if (serialNumber == null || serialNumber.trim().isEmpty()) {
			bookStatusLabel.setText("Please enter a serial number");
			bookStatusLabel.setForeground(Color.RED);
			return;
		}
		bookController.searchBook(serialNumber);
	}

	private void addMember() {
		try {
			int id = Integer.parseInt(memberIdField.getText());
			String name = memberNameField.getText();
			String email = memberEmailField.getText();
			Book selectedBook = (Book) bookDropdown.getSelectedItem();

			if (id <= 0) {
				memberStatusLabel.setText("ID must be positive");
				memberStatusLabel.setForeground(Color.RED);
				return;
			}

			if (name.isEmpty() || email.isEmpty()) {
				memberStatusLabel.setText(ALL_FIELDS_REQUIRED);
				memberStatusLabel.setForeground(Color.RED);
				return;
			}

			Member member = new Member(id, name, email, null);
			memberController.newMember(member);

			Member savedMember = memberController.allMembers().stream().filter(m -> m.getId() == id).findFirst()
					.orElse(null);

			if (savedMember != null) {
				memberController.borrowBook(savedMember, selectedBook);
			}

			memberStatusLabel.setText("Member added successfully");
			memberStatusLabel.setForeground(Color.GREEN);
			clearMemberFields();
			refreshBookDropdown();

		} catch (NumberFormatException e) {
			memberStatusLabel.setText(ID_MUST_BE_NUMBER);
			memberStatusLabel.setForeground(Color.RED);
		}
	}

	private void deleteMember() {
		int selectedRow = memberTable.getSelectedRow();
		if (selectedRow < 0) {
			memberStatusLabel.setText("No member selected");
			memberStatusLabel.setForeground(Color.RED);
			return;
		}

		int id = Integer.parseInt(memberTableModel.getValueAt(selectedRow, 0).toString());
		Member member = new Member(id, null, null, null);
		memberController.deleteMember(member);
		memberStatusLabel.setText("Member deleted successfully");
		memberStatusLabel.setForeground(Color.GREEN);
	}

	private void showAllMembers() {
		List<Member> members = memberController.allMembers();
		showAllMembers(members);
	}

	private void searchMember() {
		String idStr = JOptionPane.showInputDialog("Enter Member ID to search:");
		if (idStr == null || idStr.trim().isEmpty()) {
			memberStatusLabel.setText("Please enter an ID");
			memberStatusLabel.setForeground(Color.RED);
			return;
		}

		try {
			int id = Integer.parseInt(idStr);
			memberController.searchMember(id);
		} catch (NumberFormatException e) {
			memberStatusLabel.setText(ID_MUST_BE_NUMBER);
			memberStatusLabel.setForeground(Color.RED);
		}
	}

	private void clearBookFields() {
		bookIdField.setText("");
		bookSerialNumberField.setText("");
		bookNameField.setText("");
		bookAuthorField.setText("");
		bookGenreField.setText("");
	}

	private void clearMemberFields() {
		memberIdField.setText("");
		memberNameField.setText("");
		memberEmailField.setText("");
	}

	@Override
	public void showAllBooks(List<Book> books) {
		bookTableModel.setRowCount(0);
		for (Book book : books) {
			bookTableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(),
					book.getAuthorName(), book.getGenre() });
		}
	}

	@Override
	public void showAllMembers(List<Member> members) {
		memberTableModel.setRowCount(0);
		for (Member member : members) {
			String borrowedBook = "None";
			if (member.getBook() != null) {
				Book currentBook = bookController.findById(member.getBook().getId());
				borrowedBook = currentBook != null ? currentBook.getName() : "Unknown";
			}

			memberTableModel.addRow(new Object[] { member.getId(), member.getName(), member.getEmail(), borrowedBook });
		}
	}

	@Override
	public void showSearchedBooks(Book book) {
		bookTableModel.setRowCount(0);
		if (book != null) {
			bookTableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(),
					book.getAuthorName(), book.getGenre() });
		}
	}

	@Override
	public void showSearchedMembers(Member member) {
		memberTableModel.setRowCount(0);
		if (member != null) {
			String borrowedBook = "None";
			if (member.getBook() != null) {
				Book currentBook = bookController.findById(member.getBook().getId());
				borrowedBook = currentBook != null ? currentBook.getName() : "Unknown";
			}

			memberTableModel.addRow(new Object[] { member.getId(), member.getName(), member.getEmail(), borrowedBook });
		}
	}

	@Override
	public void bookAdded(Book book) {
		showAllBooks();
		bookStatusLabel.setText("Book added successfully");
		bookStatusLabel.setForeground(Color.GREEN);
	}

	@Override
	public void bookRemoved(Book book) {
		showAllBooks();
		bookStatusLabel.setText("Book removed successfully");
		bookStatusLabel.setForeground(Color.GREEN);
	}

	@Override
	public void memberAdded(Member member) {
		showAllMembers();
		memberStatusLabel.setText("Member added successfully");
		memberStatusLabel.setForeground(Color.GREEN);
	}

	@Override
	public void memberRemoved(Member member) {
		showAllMembers();
		memberStatusLabel.setText("Member removed successfully");
		memberStatusLabel.setForeground(Color.GREEN);
	}

	@Override
	public void showErrorBookNotFound(String message) {
		bookStatusLabel.setText(message);
		bookStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showErrorBookNotFound(String message, Book book) {
		bookStatusLabel.setText(message);
		bookStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showBookError(String message, Book book) {
		bookStatusLabel.setText(message + " (Book ID: " + book.getId() + ")");
		bookStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showErrorMemberNotFound(String message) {
		memberStatusLabel.setText(message);
		memberStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showErrorMemberNotFound(String message, Member member) {
		memberStatusLabel.setText(message);
		memberStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showMemberError(String message, Member member) {
		memberStatusLabel.setText(message + " (Member ID: " + member.getId() + ")");
		memberStatusLabel.setForeground(Color.RED);
	}

	@Override
	public void showMessage(String message) {
		bookStatusLabel.setText(message);
		bookStatusLabel.setForeground(Color.GREEN);
		memberStatusLabel.setText(message);
		memberStatusLabel.setForeground(Color.GREEN);
	}

	@Override
	public void refreshBookDropdown() {
		populateBookDropdown();
	}
}