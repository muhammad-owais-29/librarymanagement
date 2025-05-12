package org.tdd.librarymanagement.repository.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.entity.Book;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class BookMongoRepositoryTest {

	@Mock
	private MongoClient mongoClient;

	@Mock
	private MongoDatabase mongoDatabase;

	@Mock
	private MongoCollection<Document> bookCollection;

	@Mock
	private FindIterable<Document> findIterable;

	private BookMongoRepository bookRepository;

	private Book validBook;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(mongoClient.getDatabase("testDB")).thenReturn(mongoDatabase);
		when(mongoDatabase.getCollection("books")).thenReturn(bookCollection);
		bookRepository = new BookMongoRepository(mongoClient, "testDB", "books");
		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());
	}

	@Test
	public void findAll200() {
		// Given
		Document doc = new Document().append("id", 1).append("serialNumber", "123").append("name", "Book1")
				.append("authorName", "Author1").append("genre", "Genre1");
		when(bookCollection.find()).thenReturn(findIterable);
		when(findIterable.spliterator()).thenReturn(Arrays.asList(doc).spliterator());

		// When
		List<Book> result = bookRepository.findAll();

		// Then
		assertEquals(1, result.size());
		assertEquals(validBook.getId(), result.get(0).getId());
		assertEquals(validBook.getName(), result.get(0).getName());
	}

	@Test
	public void findBySerialNumber200() {
		// Given
		Document doc = new Document().append("id", 1).append("serialNumber", "123").append("name", "Book1")
				.append("authorName", "Author1").append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// When
		Book result = bookRepository.findBySerialNumber("123");

		// Then
		assertNotNull(result);
		assertEquals(validBook.getId(), result.getId());
	}

	@Test
	public void findBySerialNumberReturnNull() {
		// Given
		when(bookCollection.find(Filters.eq("serialNumber", "999"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		Book result = bookRepository.findBySerialNumber("999");

		// Then
		assertNull(result);
	}

	@Test
	public void findById200() {
		// Given
		Document doc = new Document().append("id", 1).append("serialNumber", "123").append("name", "Book1")
				.append("authorName", "Author1").append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// When
		Book result = bookRepository.findById(1);

		// Then
		assertNotNull(result);
		assertEquals(validBook.getId(), result.getId());
	}

	@Test
	public void findByIdReturnNull() {
		// Given
		when(bookCollection.find(Filters.eq("id", 999))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		Book result = bookRepository.findById(999);

		// Then
		assertNull(result);
	}

	@Test
	public void saveInsertNewBook() {
		// Given
		when(bookCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		bookRepository.save(validBook);

		// Then
		verify(bookCollection).insertOne(argThat(doc -> doc.getInteger("id") == 1
				&& doc.getString("serialNumber").equals("123") && doc.getString("name").equals("Book1")));
	}

	@Test
	public void deleteBookBySerialNumber() {
		// When
		bookRepository.delete("123");

		// Then
		verify(bookCollection).deleteOne(Filters.eq("serialNumber", "123"));
	}

	@Test
	public void fromDocumentToBookDocumentIsNull() throws Exception {
		// Given
		java.lang.reflect.Method method = BookMongoRepository.class.getDeclaredMethod("fromDocumentToBook",
				Document.class);
		method.setAccessible(true);

		// When
		Book result = (Book) method.invoke(bookRepository, (Document) null);

		// Then
		assertNull(result);
	}

	@Test
	public void fromDocumentToBookIdIsNull() {
		// Given
		Document doc = new Document().append("serialNumber", "123").append("name", "Book1")
				.append("authorName", "Author1").append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> {
			bookRepository.findBySerialNumber("123");
		});
	}

	@Test
	public void fromDocumentToBookNameIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("serialNumber", "123").append("authorName", "Author1")
				.append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> {
			bookRepository.findBySerialNumber("123");
		});
	}

	@Test
	public void fromDocumentToBookserialNumberIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "Book1").append("authorName", "Author1")
				.append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> {
			bookRepository.findBySerialNumber("123");
		});
	}

	@Test
	public void fromDocumentToBookauthorNameIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "Book1").append("serialNumber", "123")
				.append("genre", "Genre1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> {
			bookRepository.findBySerialNumber("123");
		});
	}

	@Test
	public void fromDocumentToBookgenreIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "Book1").append("serialNumber", "123")
				.append("authorName", "Author1");
		when(bookCollection.find(Filters.eq("serialNumber", "123"))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> {
			bookRepository.findBySerialNumber("123");
		});
	}
}
