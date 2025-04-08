package org.tdd.librarymanagement.view;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;

public interface BookView {

	void bookAdded(Book newBook);

	public void showAllBooks(List<Book> books);

	public void showErrorBookNotFound(String message);

	public void showSearchedBooks(Book books);

	public void showError(String message, Book book);

}