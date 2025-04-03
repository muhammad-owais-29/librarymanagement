package org.tdd.librarymanagement.controller;

import static org.mockito.Mockito.verify;

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

	@Before
	public void setUp() {

		MockitoAnnotations.openMocks(this);

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

}
