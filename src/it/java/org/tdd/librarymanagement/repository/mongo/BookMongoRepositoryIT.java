package org.tdd.librarymanagement.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdd.librarymanagement.entity.Book;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

public class BookMongoRepositoryIT {

	private static final String DB = "testDB";
	private static final String BOOK_COLLECTION = "book";
	private static final int MONGO_PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	private MongoClient mongoClient;
	private BookMongoRepository bookRepository;

	@Before
	public void setUp() {
		mongoClient = new MongoClient(new ServerAddress("localhost", MONGO_PORT));
		bookRepository = new BookMongoRepository(mongoClient, DB, BOOK_COLLECTION);

		MongoCollection<Document> collection = mongoClient.getDatabase(DB).getCollection(BOOK_COLLECTION);
		collection.drop();

	}

	@After
	public void tearDown() {
		mongoClient.getDatabase(DB).getCollection(BOOK_COLLECTION).drop();
		mongoClient.close();
	}

	@Test
	public void testFindById() {
		// Given
		Book book = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());

		// When
		bookRepository.save(book);
		Book found = bookRepository.findById(1);

		// Then
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(1);
		assertThat(found.getSerialNumber()).isEqualTo("123");
		assertThat(found.getName()).isEqualTo("Book1");
		assertThat(found.getAuthorName()).isEqualTo("Author1");
		assertThat(found.getGenre()).isEqualTo("Genre1");
	}

	@Test
	public void testFindAll() {
		// Given
		Book book1 = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());
		Book book2 = new Book(2, "555", "Book2", "Author2", "Genre2", new ArrayList<>());

		// When
		bookRepository.save(book1);
		bookRepository.save(book2);

		List<Book> allBooks = bookRepository.findAll();

		// Then
		assertThat(allBooks).hasSize(2);
		assertThat(allBooks).extracting(Book::getId).contains(1, 2);
	}

	@Test
	public void testFindBySerialNumber() {
		// Given
		Book book = new Book(11, "66", "Book6", "Author6", "Genre6", new ArrayList<>());

		// When
		bookRepository.save(book);
		Book found = bookRepository.findBySerialNumber("66");

		// Then
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(11);
		assertThat(found.getSerialNumber()).isEqualTo("66");
	}

	@Test
	public void testDelete() {
		// Given
		Book book = new Book(5, "55", "BookD5", "Author5", "Genre5", new ArrayList<>());
		bookRepository.save(book);

		// When
		assertThat(bookRepository.findBySerialNumber("55")).isNotNull();

		bookRepository.delete("55");

		// Then
		assertThat(bookRepository.findBySerialNumber("55")).isNull();
	}

}