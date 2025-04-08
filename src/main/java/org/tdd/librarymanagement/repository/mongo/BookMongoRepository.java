package org.tdd.librarymanagement.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.repository.BookRepository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class BookMongoRepository implements BookRepository {

	private static final String ID_FIELD = "id";
	private static final String SERIAL_NUMBER_FIELD = "serialNumber";
	private static final String NAME_FIELD = "name";
	private static final String AUTHOR_NAME_FIELD = "authorName";
	private static final String GENRE_FIELD = "genre";

	private MongoCollection<Document> bookCollection;

	public BookMongoRepository(MongoClient client, String databaseName, String collectionName) {
		bookCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public void save(Book book) {

		Document document = new Document().append(ID_FIELD, book.getId())
				.append(SERIAL_NUMBER_FIELD, book.getSerialNumber()).append(NAME_FIELD, book.getName())
				.append(AUTHOR_NAME_FIELD, book.getAuthorName()).append(GENRE_FIELD, book.getGenre());
		bookCollection.insertOne(document);

	}

	@Override
	public List<Book> findAll() {
		return StreamSupport.stream(bookCollection.find().spliterator(), false).map(this::fromDocumentToBook)
				.collect(Collectors.toList());
	}

	@Override
	public Book findById(int id) {
		Document document = bookCollection.find(Filters.eq(ID_FIELD, id)).first();
		return document != null ? fromDocumentToBook(document) : null;
	}

	@Override
	public Book findBySerialNumber(String serialNumber) {
		Document document = bookCollection.find(Filters.eq(SERIAL_NUMBER_FIELD, serialNumber)).first();
		return document != null ? fromDocumentToBook(document) : null;
	}

	private Book fromDocumentToBook(Document d) {
		if (d == null) {
			return null;
		}

		Integer id = d.getInteger(ID_FIELD);
		String serialNumber = d.getString(SERIAL_NUMBER_FIELD);
		String name = d.getString(NAME_FIELD);
		String authorName = d.getString(AUTHOR_NAME_FIELD);
		String genre = d.getString(GENRE_FIELD);

		if (id == null || serialNumber == null || name == null || authorName == null || genre == null) {
			throw new IllegalArgumentException("Book document is missing required fields");
		}

		return new Book(id, serialNumber, name, authorName, genre, new ArrayList<>());
	}

}