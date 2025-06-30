package org.tdd.librarymanagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.mongo.BookMongoRepository;
import org.tdd.librarymanagement.view.BookView;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class BookControllerIT {

	private MongoClient mongoClient;
	private BookMongoRepository bookMongoRepository;
	@Mock
	private BookView bookView;
	private BookController bookController;
	private List<Member> borrowers;
	private Book validBook;
	private Book anotherBook;

	private static final String DB = "library";
	private static final String BOOK_COLLECTION = "book";
	private static final int MONGO_PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		borrowers = new ArrayList<>();
		mongoClient = new MongoClient(new ServerAddress("localhost", MONGO_PORT));
		bookMongoRepository = new BookMongoRepository(mongoClient, DB, BOOK_COLLECTION);
		bookController = new BookController(bookView, bookMongoRepository);

		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", borrowers);
		anotherBook = new Book(2, "456", "Book2", "Author2", "Genre2", borrowers);
	}

	@After
	public void tearDown() {
		mongoClient.getDatabase(DB).drop();
		mongoClient.close();
	}

	@Test
	public void testAddNewBook() {
		// Given
		Book book = new Book(3, "999", "New Title", "New Author", "Genre", borrowers);

		// When
		bookController.newBook(book);

		// Then
		Book found = bookMongoRepository.findById(3);
		assertThat(found).isNotNull();
		assertThat(found.getSerialNumber()).isEqualTo("999");

		// Then
		verify(bookView).bookAdded(book);
	}

	@Test
	public void testFindBookById() {
		// Given
		bookMongoRepository.save(validBook);

		// When
		Book found = bookController.findById(1);

		// Then
		assertThat(found).isNotNull();
		assertThat(found.getSerialNumber()).isEqualTo("123");
	}

	@Test
	public void testShowAllBooks() {
		// Given
		bookMongoRepository.save(validBook);
		bookMongoRepository.save(anotherBook);

		// When
		List<Book> books = bookController.allBooks();

		// Then
		assertThat(books).hasSize(2);
		verify(bookView).showAllBooks(books);
	}

	@Test
	public void testSearchBookBySerialNumber() {
		// Given
		bookMongoRepository.save(validBook);

		// When
		bookController.searchBook("123");

		// Then
		verify(bookView).showSearchedBooks(validBook);
	}

	@Test
	public void testDeleteBook() {
		// Given
		bookMongoRepository.save(anotherBook);

		// When
		bookController.deleteBook(anotherBook);

		// Then
		assertThat(bookMongoRepository.findById(2)).isNull();
		verify(bookView).bookRemoved(anotherBook);
	}

	@Test
	public void deleteNonExistingBook() {
		// Given
		Book bookToDelete = new Book(99, "999", "Title", "Author", "Genre", borrowers);

		// When
		bookController.deleteBook(bookToDelete);

		// Then
		verify(bookView).showErrorBookNotFound("No existing book with serial number 999", bookToDelete);
	}

}