package org.tdd.librarymanagement.controller;

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
		bookRepository.save(book);
		bookView.bookAdded(book);

	}

}