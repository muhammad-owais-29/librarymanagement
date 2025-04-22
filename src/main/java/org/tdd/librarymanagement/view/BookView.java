package org.tdd.librarymanagement.view;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;

public interface BookView {

	void bookAdded(Book newBook);

	public void showAllBooks(List<Book> books);

	public void showErrorBookNotFound(String message);

	public void showSearchedBooks(Book books);

	public void showError(String message, Book book);

	public void bookRemoved(Book book);

	public void showErrorBookNotFound(String message, Book book);

	public void memberAdded(Member member);

	public void showError(String message, Member member);

	void refreshBookDropdown();

	public void showAllMembers(List<Member> members);

}