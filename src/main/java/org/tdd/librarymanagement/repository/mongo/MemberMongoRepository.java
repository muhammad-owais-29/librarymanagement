package org.tdd.librarymanagement.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.repository.MemberRepository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class MemberMongoRepository implements MemberRepository {

	private static final String ID_FIELD = "id";
	private static final String NAME_FIELD = "name";
	private static final String EMAIL_FIELD = "email";
	private static final String BOOK_ID_FIELD = "bookId";

	private MongoCollection<Document> memberCollection;
	private final BookRepository bookRepository;

	public MemberMongoRepository(MongoClient client, String databaseName, String collectionName,
			BookRepository bookRepository) {
		this.memberCollection = client.getDatabase(databaseName).getCollection(collectionName);
		this.bookRepository = bookRepository;
	}

	@Override
	public Member findById(int id) {
		Document document = memberCollection.find(Filters.eq(ID_FIELD, id)).first();
		return document != null ? fromDocumentToMember(document) : null;
	}

	@Override
	public List<Member> findAll() {
		return StreamSupport.stream(memberCollection.find().spliterator(), false).map(this::fromDocumentToMember)
				.collect(Collectors.toList());
	}

	@Override
	public void save(Member member) {
		Document document = new Document().append(ID_FIELD, member.getId()).append(NAME_FIELD, member.getName())
				.append(EMAIL_FIELD, member.getEmail())
				.append(BOOK_ID_FIELD, member.getBook() != null ? member.getBook().getId() : null);

		Document existing = memberCollection.find(Filters.eq(ID_FIELD, member.getId())).first();
		if (existing != null) {
			memberCollection.replaceOne(Filters.eq(ID_FIELD, member.getId()), document);
		} else {
			memberCollection.insertOne(document);
		}
	}

	@Override
	public void delete(int id) {
		memberCollection.deleteOne(Filters.eq(ID_FIELD, id));
	}

	private Member fromDocumentToMember(Document d) {
		if (d == null) {
			return null;
		}

		Integer id = d.getInteger(ID_FIELD);
		String name = d.getString(NAME_FIELD);
		String email = d.getString(EMAIL_FIELD);
		Integer bookId = d.getInteger(BOOK_ID_FIELD);

		if (id == null || name == null || email == null) {
			throw new IllegalArgumentException("Member document is missing required fields");
		}

		Book book = null;
		if (bookId != null) {
			book = bookRepository.findById(bookId);

			if (book != null) {
				book = new Book(book.getId(), book.getSerialNumber(), book.getName(), book.getAuthorName(),
						book.getGenre(), new ArrayList<>());
			}
		}

		return new Member(id, name, email, book);
	}

}