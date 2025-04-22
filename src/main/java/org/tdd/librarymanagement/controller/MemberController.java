package org.tdd.librarymanagement.controller;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.repository.MemberRepository;
import org.tdd.librarymanagement.view.BookView;

public class MemberController {

	private BookView bookView;
	private MemberRepository memberRepository;
	private BookRepository bookRepository;

	public MemberController(BookView bookView, MemberRepository memberRepository, BookRepository bookRepository) {
		this.bookView = bookView;
		this.memberRepository = memberRepository;
		this.bookRepository = bookRepository;
	}

	public void newMember(Member member) {
		if (member.getId() <= 0) {
			bookView.showError("ID must be a positive number", member);
			return;
		}

		Member existingMember = memberRepository.findById(member.getId());
		if (existingMember != null) {
			bookView.showError("ID Already exists " + member.getId(), existingMember);
			return;
		}

		memberRepository.save(member);
		bookView.memberAdded(member);
	}

	public List<Member> allMembers() {
		List<Member> members = memberRepository.findAll();
		for (Member member : members) {
			if (member.getBook() != null) {
				Book book = bookRepository.findById(member.getBook().getId());
				if (book != null) {
					member.setBook(book);
				}
			}
		}
		bookView.showAllMembers(members);
		return members;
	}

	public void borrowBook(Member member, Book book) {
		if (book == null) {
			bookView.showError("Book cannot be null", (Book) null);
			return;
		}
		if (member == null) {
			bookView.showError("Member cannot be null", (Member) null);
			return;
		}

		Member existingMember = memberRepository.findById(member.getId());
		Book existingBook = bookRepository.findById(book.getId());

		if (existingMember == null || existingBook == null) {
			bookView.showError("Member or Book not found", (Member) null);
			return;
		}

		existingMember.setBook(existingBook);

		memberRepository.save(existingMember);
		bookRepository.save(existingBook);

		bookView.refreshBookDropdown();
		bookView.showAllMembers(allMembers());
	}

}