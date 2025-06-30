package org.tdd.librarymanagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tdd.librarymanagement.entity.Book;
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;
import org.tdd.librarymanagement.repository.MemberRepository;
import org.tdd.librarymanagement.repository.mongo.BookMongoRepository;
import org.tdd.librarymanagement.repository.mongo.MemberMongoRepository;
import org.tdd.librarymanagement.view.BookView;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class MemberControllerIT {

	private MongoClient mongoClient;
	private MemberMongoRepository memberMongoRepository;
	private BookMongoRepository bookMongoRepository;
	@Mock
	private BookView bookView;
	private MemberController memberController;
	private BookRepository bookRepository;
	private MemberRepository memberRepository;

	private Book validBook;
	private Member validMember;
	private Member anotherMember;

	private static final String DB = "library";
	private static final String BOOK_COLLECTION = "book";
	private static final String MEMBER_COLLECTION = "member";
	private static final int MONGO_PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		mongoClient = new MongoClient(new ServerAddress("localhost", MONGO_PORT));
		bookMongoRepository = new BookMongoRepository(mongoClient, DB, BOOK_COLLECTION);
		memberMongoRepository = new MemberMongoRepository(mongoClient, DB, MEMBER_COLLECTION, bookMongoRepository);

		bookRepository = bookMongoRepository;
		memberRepository = memberMongoRepository;

		memberController = new MemberController(bookView, memberRepository, bookRepository);

		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", null);
		validMember = new Member(1, "owais", "owais@gmail.com", validBook);
		anotherMember = new Member(2, "test2", "test2@gmail.com", null);

		bookRepository.save(validBook);
	}

	@After
	public void tearDown() {
		mongoClient.getDatabase(DB).getCollection(BOOK_COLLECTION).drop();
		mongoClient.getDatabase(DB).getCollection(MEMBER_COLLECTION).drop();
		mongoClient.close();
	}

	@Test
	public void testAddNewMember() {
		// Given
		Member newMember = new Member(3, "New Member", "new@gmail.com", null);

		// When
		memberController.newMember(newMember);

		// Then
		Member found = memberRepository.findById(3);
		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("New Member");
		verify(bookView).memberAdded(newMember);
	}

	@Test
	public void testAllMembers() {
		// Given
		memberRepository.save(validMember);
		memberRepository.save(anotherMember);

		// When
		List<Member> members = memberController.allMembers();

		// Then
		assertThat(members).hasSizeGreaterThanOrEqualTo(2);
		verify(bookView).showAllMembers(members);
	}

	@Test
	public void testBorrowBook() {
		// Given
		memberRepository.save(anotherMember);
		Book bookToBorrow = validBook;

		// When
		memberController.borrowBook(anotherMember, bookToBorrow);

		// Then
		Member updated = memberRepository.findById(2);
		assertThat(updated.getBook()).isNotNull();
		assertThat(updated.getBook().getId()).isEqualTo(bookToBorrow.getId());
		verify(bookView).refreshBookDropdown();
		verify(bookView, times(2)).showAllMembers(memberRepository.findAll());
	}

	@Test
	public void testDeleteMember() {
		// Given
		memberRepository.save(validMember);

		// When
		memberController.deleteMember(validMember);

		// Then
		assertThat(memberRepository.findById(validMember.getId())).isNull();
		verify(bookView).memberRemoved(validMember);
	}

	@Test
	public void testSearchMember() {
		// Given
		memberRepository.save(validMember);

		// When
		memberController.searchMember(validMember.getId());

		// Then
		verify(bookView).showSearchedMembers(validMember);
	}
}