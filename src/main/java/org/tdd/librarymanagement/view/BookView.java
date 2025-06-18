package org.tdd.librarymanagement.view;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;

public interface BookView {

	void bookAdded(Book newBook);

	public void showAllBooks(List<Book> books);

	public void showErrorBookNotFound(String message);

	public void showSearchedBooks(Book books);

	public void showBookError(String message, Book book);

	public void bookRemoved(Book book);

	public void showErrorBookNotFound(String message, Book book);

	public void memberAdded(Member member);

	public void showMemberError(String message, Member member);

	public void showAllMembers(List<Member> members);

	public void memberRemoved(Member member);

	public void showSearchedMembers(Member member);

	public void showErrorMemberNotFound(String message, Member member);

	public void showErrorMemberNotFound(String message);

	void showMessage(String message);

	void refreshBookDropdown();

}