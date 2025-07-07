package org.tdd.librarymanagement.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class MemberMongoRepositoryIT {

	private static final String DB = "testDB";
	private static final String MEMBER_COLLECTION = "member";
	private static final String BOOK_COLLECTION = "book";
	private static final int MONGO_PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	private MongoClient mongoClient;
	private BookMongoRepository bookRepository;
	private MemberMongoRepository memberRepository;

	@Before
	public void setUp() {
		mongoClient = new MongoClient(new ServerAddress("localhost", MONGO_PORT));
		MongoDatabase db = mongoClient.getDatabase(DB);
		if (db.listCollectionNames().into(new ArrayList<>()).contains(MEMBER_COLLECTION)) {
			db.getCollection(MEMBER_COLLECTION).drop();
		}
		if (db.listCollectionNames().into(new ArrayList<>()).contains(BOOK_COLLECTION)) {
			db.getCollection(BOOK_COLLECTION).drop();
		}
		bookRepository = new BookMongoRepository(mongoClient, DB, BOOK_COLLECTION);
		memberRepository = new MemberMongoRepository(mongoClient, DB, MEMBER_COLLECTION, bookRepository);
	}

	@After
	public void tearDown() {
		MongoDatabase db = mongoClient.getDatabase(DB);
		if (db.listCollectionNames().into(new ArrayList<>()).contains(BOOK_COLLECTION)) {
			db.getCollection(BOOK_COLLECTION).drop();
		}
		if (db.listCollectionNames().into(new ArrayList<>()).contains(MEMBER_COLLECTION)) {
			db.getCollection(MEMBER_COLLECTION).drop();
		}
		mongoClient.close();
		mongoClient = null;
	}

	@Test
	public void testFindById() {
		// Given
		Book book = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());
		bookRepository.save(book);

		Member member = new Member(1, "owais", "owais@gmail.com", book);

		// When
		memberRepository.save(member);
		Member found = memberRepository.findById(1);

		// Then
		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(1);
		assertThat(found.getName()).isEqualTo("owais");
		assertThat(found.getEmail()).isEqualTo("owais@gmail.com");
		assertThat(found.getBook()).isNotNull();
		assertThat(found.getBook().getId()).isEqualTo(1);
	}

	@Test
	public void testFindAll() {
		// Given
		Book book = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());
		bookRepository.save(book);

		Member m1 = new Member(1, "owais", "owais@gmail.com", book);
		Member m2 = new Member(6, "test6", "test5@gmail.com", null);

		// When
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> all = memberRepository.findAll();

		// Then
		assertThat(all).extracting(Member::getId).contains(1, 6);
		assertThat(all.stream().filter(m -> m.getId() == 1).findFirst().get().getBook()).isNotNull();
		assertThat(all.stream().filter(m -> m.getId() == 6).findFirst().get().getBook()).isNull();
	}

	@Test
	public void testDelete() {
		// Given
		Member member = new Member(7, "test7", "test7@gmail.com", null);
		memberRepository.save(member);

		// When
		assertThat(memberRepository.findById(7)).isNotNull();
		memberRepository.delete(7);

		// Then
		assertThat(memberRepository.findById(7)).isNull();
	}

}