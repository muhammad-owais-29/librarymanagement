package org.tdd.librarymanagement.controller;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.view.BookView;

public class BookController {

	private BookRepository bookRepository;
	private BookView bookView;

	public BookController(BookView bookView, BookRepository bookRepository) {
		this.bookView = bookView;
		this.bookRepository = bookRepository;
	}

	public void newBook(Book book) {
		if (book.getId() <= 0) {
			bookView.showBookError("ID must be a positive number", book);
			return;
		}

		Book existingBook = bookRepository.findBySerialNumber(book.getSerialNumber());
		if (existingBook != null) {
			bookView.showBookError("Already existing book with serial number " + book.getSerialNumber(), existingBook);
			return;
		}

		Book existingBookById = bookRepository.findById(book.getId());
		if (existingBookById != null) {
			bookView.showBookError("ID Already exists " + book.getId(), existingBookById);
			return;
		}

		bookRepository.save(book);
		bookView.bookAdded(book);
	}

	public Book findById(int id) {
		return bookRepository.findById(id);
	}

	public List<Book> allBooks() {
		List<Book> books = bookRepository.findAll();
		bookView.showAllBooks(books);
		return books;
	}

	public void searchBook(String serialNumber) {
		Book book = bookRepository.findBySerialNumber(serialNumber);
		if (book == null) {
			bookView.showErrorBookNotFound("No existing book with serial number " + serialNumber);
			return;
		}

		bookView.showSearchedBooks(book);
	}

	public void deleteBook(Book book) {
		Book existingBook = bookRepository.findBySerialNumber(book.getSerialNumber());
		if (existingBook == null) {
			bookView.showErrorBookNotFound("No existing book with serial number " + book.getSerialNumber(), book);
			return;
		}

		bookRepository.delete(book.getSerialNumber());
		bookView.bookRemoved(book);
	}

	public void updateBook(Book book) {
		Book existingBook = bookRepository.findById(book.getId());
		if (existingBook == null) {
			bookView.showErrorBookNotFound("No existing book with ID " + book.getId(), book);
			return;
		}

		// Check if serial number is being changed to one that already exists
		if (!existingBook.getSerialNumber().equals(book.getSerialNumber())) {
			Book bookWithSameSerial = bookRepository.findBySerialNumber(book.getSerialNumber());
			if (bookWithSameSerial != null) {
				bookView.showBookError("Serial number already exists", book);
				return;
			}
		}
		bookRepository.save(book);
		bookView.bookAdded(book);
	}

}