package org.tdd.librarymanagement.view.swing;

import java.awt.BorderLayout;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.controller.MemberController;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.view.BookView;

public class CombinedSwingView extends JFrame implements BookView {

	private static final long serialVersionUID = 1L;
	private BookController bookController;
	private MemberController memberController;

	public void setBookController(BookController bookController) {
		this.bookController = bookController;
	}

	public void setMemberController(MemberController memberController) {
		this.memberController = memberController;
	}

	// Book Components
	public JTextField bookIdField, bookSerialNumberField, bookNameField, bookAuthorField, bookGenreField;
	private JTable bookTable;
	private DefaultTableModel bookTableModel;

	// Member Components
	private JTextField memberIdField, memberNameField, memberEmailField;
	private JTable memberTable;
	private DefaultTableModel memberTableModel;
	private JComboBox<Book> bookDropdown;

	public CombinedSwingView() {
		setTitle("Library Management System");
		setSize(1200, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// split pane to divide the window into two parts
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(600);

		JPanel bookPanel = createBookPanel();
		splitPane.setLeftComponent(bookPanel);

		JPanel memberPanel = createMemberPanel();
		splitPane.setRightComponent(memberPanel);

		add(splitPane, BorderLayout.CENTER);
	}

	// Create the Book Panel
	private JPanel createBookPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Book Form
		JPanel bookFormPanel = new JPanel(new GridLayout(6, 2, 5, 5));
		bookFormPanel.setBorder(BorderFactory.createTitledBorder("Book Form"));

		bookFormPanel.add(new JLabel("ID:"));
		bookIdField = new JTextField();
		bookFormPanel.add(bookIdField);

		bookFormPanel.add(new JLabel("Serial Number:"));
		bookSerialNumberField = new JTextField();
		bookFormPanel.add(bookSerialNumberField);

		bookFormPanel.add(new JLabel("Name:"));
		bookNameField = new JTextField();
		bookFormPanel.add(bookNameField);

		bookFormPanel.add(new JLabel("Author:"));
		bookAuthorField = new JTextField();
		bookFormPanel.add(bookAuthorField);

		bookFormPanel.add(new JLabel("Genre:"));
		bookGenreField = new JTextField();
		bookFormPanel.add(bookGenreField);

		JButton addBookButton = new JButton("Add Book");
		addBookButton.addActionListener(e -> addBook());
		bookFormPanel.add(addBookButton);

		JButton updateBookButton = new JButton("Update Book");
		bookFormPanel.add(updateBookButton);

		panel.add(bookFormPanel, BorderLayout.NORTH);

		// Book Table
		String[] bookColumns = { "ID", "Serial Number", "Name", "Author", "Genre" };
		bookTableModel = new DefaultTableModel(bookColumns, 0);
		bookTable = new JTable(bookTableModel);
		bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bookTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
				int selectedRow = bookTable.getSelectedRow();
				if (selectedRow >= 0) {
					bookIdField.setText(bookTableModel.getValueAt(selectedRow, 0).toString());
					bookSerialNumberField.setText(bookTableModel.getValueAt(selectedRow, 1).toString());
					bookNameField.setText(bookTableModel.getValueAt(selectedRow, 2).toString());
					bookAuthorField.setText(bookTableModel.getValueAt(selectedRow, 3).toString());
					bookGenreField.setText(bookTableModel.getValueAt(selectedRow, 4).toString());
				}
			}
		});

		JScrollPane bookScrollPane = new JScrollPane(bookTable);
		panel.add(bookScrollPane, BorderLayout.CENTER);

		// Book Buttons
		JPanel bookButtonPanel = new JPanel();
		JButton deleteBookButton = new JButton("Delete Book");
		deleteBookButton.addActionListener(e -> deleteBook());
		bookButtonPanel.add(deleteBookButton);

		JButton showAllBooksButton = new JButton("Show All Books");
		showAllBooksButton.addActionListener(e -> showAllBooks());
		bookButtonPanel.add(showAllBooksButton);

		JButton searchBookButton = new JButton("Search Book");
		searchBookButton.addActionListener(e -> searchBook());
		bookButtonPanel.add(searchBookButton);

		panel.add(bookButtonPanel, BorderLayout.SOUTH);

		return panel;
	}

	// Create the Member Panel
	private JPanel createMemberPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Member Form
		JPanel memberFormPanel = new JPanel(new GridLayout(5, 2, 5, 5));
		memberFormPanel.setBorder(BorderFactory.createTitledBorder("Member Form"));

		memberFormPanel.add(new JLabel("ID:"));
		memberIdField = new JTextField();
		memberFormPanel.add(memberIdField);

		memberFormPanel.add(new JLabel("Name:"));
		memberNameField = new JTextField();
		memberFormPanel.add(memberNameField);

		memberFormPanel.add(new JLabel("Email:"));
		memberEmailField = new JTextField();
		memberFormPanel.add(memberEmailField);

		memberFormPanel.add(new JLabel("Borrow Book:"));
		bookDropdown = new JComboBox<>();
		bookDropdown.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof Book) {
					Book book = (Book) value;
					if (book.getId() == -1) {
						setText("Please select a book");
						setEnabled(false);
					} else {
						setText(book.getName());
						setEnabled(true);
					}
				}
				return this;
			}
		});
		populateBookDropdown();
		memberFormPanel.add(bookDropdown);

		JButton addMemberButton = new JButton("Add Member");
		addMemberButton.addActionListener(e -> addMember());
		memberFormPanel.add(addMemberButton);

		panel.add(memberFormPanel, BorderLayout.NORTH);

		// Member Table
		String[] memberColumns = { "ID", "Name", "Email", "Borrowed Books" };
		memberTableModel = new DefaultTableModel(memberColumns, 0);
		memberTable = new JTable(memberTableModel);
		memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane memberScrollPane = new JScrollPane(memberTable);
		panel.add(memberScrollPane, BorderLayout.CENTER);

		// Member Buttons
		JPanel memberButtonPanel = new JPanel();
		memberButtonPanel.add(new JButton("Delete Member"));
		memberButtonPanel.add(new JButton("Show All Members"));
		memberButtonPanel.add(new JButton("Search Member"));

		panel.add(memberButtonPanel, BorderLayout.SOUTH);

		return panel;
	}

	private void populateBookDropdown() {
		DefaultComboBoxModel<Book> model = new DefaultComboBoxModel<>();
		bookDropdown.setModel(model);

		model.addElement(new Book(-1, "", "Please select a book", "", "", new ArrayList<>()));

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
		int id = Integer.parseInt(bookIdField.getText());
		String serialNumber = bookSerialNumberField.getText();
		String name = bookNameField.getText();
		String author = bookAuthorField.getText();
		String genre = bookGenreField.getText();

		Book book = new Book(id, serialNumber, name, author, genre, new ArrayList<>());
		bookController.newBook(book);
		clearBookFields();
	}

	private void showAllBooks() {
		List<Book> books = bookController.allBooks();
		showAllBooks(books);
	}

	private void searchBook() {
		String serialNumber = JOptionPane.showInputDialog("Enter Book Serial Number to search:");
		if (serialNumber != null) {
			bookController.searchBook(serialNumber);
		}
	}

	private void deleteBook() {
		int selectedRow = bookTable.getSelectedRow();
		if (selectedRow >= 0) {
			String serialNumber = bookTableModel.getValueAt(selectedRow, 1).toString();
			Book book = new Book(0, serialNumber, null, null, null, null);
			bookController.deleteBook(book);
		}
	}

	private void clearBookFields() {
		bookIdField.setText("");
		bookSerialNumberField.setText("");
		bookNameField.setText("");
		bookAuthorField.setText("");
		bookGenreField.setText("");
	}

	private void addMember() {
		int id = Integer.parseInt(memberIdField.getText());
		String name = memberNameField.getText();
		String email = memberEmailField.getText();
		Book selectedBook = (Book) bookDropdown.getSelectedItem();

		Member member = new Member(id, name, email, null);
		memberController.newMember(member);

		Member savedMember = memberController.allMembers().stream().filter(m -> m.getId() == id).findFirst()
				.orElse(null);

		if (selectedBook != null && savedMember != null) {
			memberController.borrowBook(savedMember, selectedBook);
		}

		clearMemberFields();
		refreshBookDropdown();
	}

	private void showAllMembers() {
		List<Member> members = memberController.allMembers();
		showAllMembers(members);
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
	public void bookAdded(Book book) {
		showAllBooks();
	}

	@Override
	public void bookRemoved(Book book) {
		showAllBooks();
	}

	@Override
	public void showErrorBookNotFound(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showSearchedBooks(Book book) {
		bookTableModel.setRowCount(0);
		bookTableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(), book.getAuthorName(),
				book.getGenre() });
	}

	@Override
	public void showError(String message, Book book) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showErrorBookNotFound(String message, Book book) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void memberAdded(Member member) {
		showAllMembers();
	}

	@Override
	public void showError(String message, Member member) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void refreshBookDropdown() {
		populateBookDropdown();
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
}