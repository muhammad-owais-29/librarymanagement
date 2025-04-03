package org.tdd.librarymanagement.repository.mongo;

import org.bson.Document;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.repository.BookRepository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

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

		Document document = new Document()
				.append(ID_FIELD, book.getId())
				.append(SERIAL_NUMBER_FIELD, book.getSerialNumber())
				.append(NAME_FIELD, book.getName())
				.append(AUTHOR_NAME_FIELD, book.getAuthorName())
				.append(GENRE_FIELD, book.getGenre());
		bookCollection.insertOne(document);

	}

}