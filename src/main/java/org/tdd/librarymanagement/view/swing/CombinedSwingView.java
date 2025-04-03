package org.tdd.librarymanagement.view.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.tdd.librarymanagement.controller.BookController;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.view.BookView;

public class CombinedSwingView extends JFrame implements BookView {

	private static final long serialVersionUID = 1L;
	private BookController bookController;

	public void setBookController(BookController bookController) {
		this.bookController = bookController;
	}

	// Book Components
	private JTextField bookIdField, bookSerialNumberField, bookNameField, bookAuthorField, bookGenreField;
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

		JScrollPane bookScrollPane = new JScrollPane(bookTable);
		panel.add(bookScrollPane, BorderLayout.CENTER);

		// Book Buttons
		JPanel bookButtonPanel = new JPanel();
		bookButtonPanel.add(new JButton("Delete Book"));
		bookButtonPanel.add(new JButton("Show All Books"));
		bookButtonPanel.add(new JButton("Search Book"));

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
		memberFormPanel.add(bookDropdown);

		memberFormPanel.add(new JButton("Add Member"));

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

	private void addBook() {

		int id = Integer.parseInt(bookIdField.getText());
		String serialNumber = bookSerialNumberField.getText();
		String name = bookNameField.getText();
		String author = bookAuthorField.getText();
		String genre = bookGenreField.getText();

		Book book = new Book(id, serialNumber, name, author, genre, new ArrayList<>());
		bookController.newBook(book);

	}

	@Override
	public void bookAdded(Book newBook) {
		// TODO Auto-generated method stub

	}

}