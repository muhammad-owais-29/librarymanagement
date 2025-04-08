package org.tdd.librarymanagement.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.view.BookView;

public class BookControllerTest {

	@Mock
	private BookView bookView;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookController bookController;

	private List<Member> borrowers;

	private Book validBook;

	private Book anotherBook;

	@Before
	public void setUp() {

		MockitoAnnotations.openMocks(this);

		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", borrowers);
		anotherBook = new Book(2, "456", "Book2", "Author2", "Genre2", borrowers);

	}

	@Test
	public void addNewBook200() {

		// Given
		Book newBook = new Book(3, "999", "New Title", "New Author", "Genre", borrowers);

		// When
		bookController.newBook(newBook);

		verify(bookRepository).save(newBook);
		verify(bookView).bookAdded(newBook);

	}

	@Test
	public void newBookDuplicateSerialNumber() {
		// Given
		when(bookRepository.findBySerialNumber("123")).thenReturn(validBook);

		// When
		bookController.newBook(new Book(1, "123", "New Book", "New Author", "Genre", borrowers));

		// Then
		verify(bookView).showError("Already existing book with serial number 123", validBook);
		verify(bookRepository, never()).save(any());
	}

	@Test
	public void newBookInvalidId() {
		// Given
		Book invalidBook = new Book(0, "789", "New Book", "New Author", "Genre", borrowers);

		// When
		bookController.newBook(invalidBook);

		// Then
		verify(bookView).showError("ID must be a positive number", invalidBook);
		verify(bookRepository, never()).save(any());
	}

	@Test
	public void newBookDuplicateId() {
		// Given
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		bookController.newBook(new Book(1, "789", "New Book", "New Author", "Genre", borrowers));

		// Then
		verify(bookView).showError("ID Already exists 1", validBook);
		verify(bookRepository, never()).save(any());
	}

	@Test
	public void allBooks() {
		// Given
		List<Book> books = Arrays.asList(validBook, anotherBook);
		when(bookRepository.findAll()).thenReturn(books);

		// When
		List<Book> result = bookController.allBooks();

		// Then
		assertEquals(books, result);
		verify(bookView).showAllBooks(books);
	}

	@Test
	public void findById() {
		// Given
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		Book result = bookController.findById(1);

		// Then
		assertEquals(validBook, result);
		verify(bookRepository).findById(1);
	}

	@Test
	public void searchBook200() {
		// Given
		when(bookRepository.findBySerialNumber("123")).thenReturn(validBook);

		// When
		bookController.searchBook("123");

		// Then
		verify(bookView).showSearchedBooks(validBook);
	}

	@Test
	public void searchBookNotFound() {
		// Given
		when(bookRepository.findBySerialNumber("999")).thenReturn(null);

		// When
		bookController.searchBook("999");

		// Then
		verify(bookView).showErrorBookNotFound("No existing book with serial number 999");
	}

}
