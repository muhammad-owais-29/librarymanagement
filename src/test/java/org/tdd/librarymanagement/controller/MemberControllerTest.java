package org.tdd.librarymanagement.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.repository.MemberRepository;
import org.tdd.librarymanagement.view.BookView;

public class MemberControllerTest {

	@Mock
	private BookView bookView;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private MemberController memberController;

	private Member validMember;
	private Member anotherMember;
	private Book validBook;

	@Before
	public void setUp() {

		MockitoAnnotations.openMocks(this);

		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", null);
		validMember = new Member(1, "owais", "owais@gmail.com", validBook);
		anotherMember = new Member(2, "test2", "test2@gmail.com", null);

	}

	@Test
	public void allMembersShowAllMembersWithBooks() {
		// Given
		List<Member> members = Arrays.asList(validMember, anotherMember);
		when(memberRepository.findAll()).thenReturn(members);
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		List<Member> result = memberController.allMembers();

		// Then
		assertEquals(2, result.size());
		assertEquals("Book1", result.get(0).getBook().getName());
		verify(bookView).showAllMembers(members);
	}

	@Test
	public void allMembersEmptyList() {
		// Given
		when(memberRepository.findAll()).thenReturn(Collections.emptyList());

		// When
		List<Member> result = memberController.allMembers();

		// Then
		assertEquals(0, result.size());
		verify(bookView).showAllMembers(Collections.emptyList());
	}

	@Test
	public void allMembersNullBook() {
		// Given
		Member memberWithNullBook = new Member(3, "Null Book", "null@gmail.com", null);
		when(memberRepository.findAll()).thenReturn(Collections.singletonList(memberWithNullBook));

		// When
		List<Member> result = memberController.allMembers();

		// Then
		assertEquals(1, result.size());
		verify(bookView).showAllMembers(anyList());
	}

	@Test
	public void allMembersBookIdNotFound() {
		// Given
		Book unknownBook = new Book(99, "999", "unknown Book", "unknown Author2", "Genre2", null);
		Member memberWithNonExistentBook = new Member(4, "test2", "test2@gmail.com", unknownBook);
		when(memberRepository.findAll()).thenReturn(Collections.singletonList(memberWithNonExistentBook));
		when(bookRepository.findById(99)).thenReturn(null);

		// When
		List<Member> result = memberController.allMembers();

		// Then
		assertEquals(1, result.size());
		assertEquals(unknownBook, result.get(0).getBook());
		verify(bookView).showAllMembers(anyList());
	}

	@Test
	public void newMemberInvalidId() {
		// Given
		Member invalidMember = new Member(0, "Invalid", "invalid@gmail.com", null);

		// When
		memberController.newMember(invalidMember);

		// Then
		verify(bookView).showMemberError("ID must be a positive number", invalidMember);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void newMemberDuplicateId() {
		// Given
		when(memberRepository.findById(1)).thenReturn(validMember);

		// When
		memberController.newMember(validMember);

		// Then
		verify(bookView).showMemberError("ID Already exists 1", validMember);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void newMemberAddValidMember() {
		// Given
		when(memberRepository.findById(3)).thenReturn(null);
		Member newMember = new Member(3, "New Member", "new@gmail.com", null);

		// When
		memberController.newMember(newMember);

		// Then
		verify(memberRepository).save(newMember);
		verify(bookView).memberAdded(newMember);
	}

	@Test
	public void borrowBookNullBook() {
		// Given

		// When
		memberController.borrowBook(validMember, null);

		// Then
		verify(bookView).showBookError("Book cannot be null", (Book) null);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void borrowBookNullMember() {
		// Given

		// When
		memberController.borrowBook(null, validBook);

		// Then
		verify(bookView).showMemberError("Member cannot be null", (Member) null);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void borrowBookMemberNotFound() {
		// Given
		when(memberRepository.findById(1)).thenReturn(null);
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		memberController.borrowBook(validMember, validBook);

		// Then
		verify(bookView).showMemberError("Member or Book not found", (Member) null);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void borrowBook_BookNotFound() {
		// Given
		when(memberRepository.findById(1)).thenReturn(validMember);
		when(bookRepository.findById(1)).thenReturn(null);

		// When
		memberController.borrowBook(validMember, validBook);

		// Then
		verify(bookView).showMemberError("Member or Book not found", (Member) null);
		verify(memberRepository, never()).save(any());
	}

	@Test
	public void borrowBookSuccessfullyBorrowBook() {
		// Given
		Member memberWithoutBook = new Member(1, "owais", "owais@gmail.com", null);
		when(memberRepository.findById(1)).thenReturn(memberWithoutBook);
		when(bookRepository.findById(1)).thenReturn(validBook);
		List<Member> expectedMembers = Collections.singletonList(memberWithoutBook);
		when(memberRepository.findAll()).thenReturn(expectedMembers);

		// When
		memberController.borrowBook(memberWithoutBook, validBook);

		// Then
		assertEquals(validBook, memberWithoutBook.getBook());
		verify(memberRepository).save(memberWithoutBook);
		verify(bookRepository).save(validBook);
		verify(bookView).refreshBookDropdown();
		verify(bookView, times(2)).showAllMembers(anyList());
	}

	@Test
	public void deleteMemberNonExisting() {
		when(memberRepository.findById(99)).thenReturn(null);

		Member nonExistingMember = new Member(99, "Non-existent", "none@example.com", null);
		memberController.deleteMember(nonExistingMember);

		verify(bookView).showErrorMemberNotFound("No existing member with id 99", nonExistingMember);
		verify(memberRepository, never()).delete(anyInt());
	}

	@Test
	public void deleteMemberExistingMember() {
		when(memberRepository.findById(1)).thenReturn(validMember);

		memberController.deleteMember(validMember);

		verify(memberRepository).delete(1);
		verify(bookView).memberRemoved(validMember);
	}

	@Test
	public void searchMemberNotFound() {
		when(memberRepository.findById(99)).thenReturn(null);

		memberController.searchMember(99);

		verify(bookView).showErrorMemberNotFound("No existing member with id 99");
	}

//	@Test
//	public void searchMemberFoundMember() {
//		when(memberRepository.findById(1)).thenReturn(validMember);
//
//		memberController.searchMember(1);
//
//		verify(bookView).showSearchedMembers(validMember);
//	}

}