package org.tdd.librarymanagement.repository.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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
import org.tdd.librarymanagement.entity.Member;
import org.tdd.librarymanagement.repository.BookRepository;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MemberMongoRepositoryTest {
	@Mock
	private MongoClient mongoClient;

	@Mock
	private MongoDatabase mongoDatabase;

	@Mock
	private MongoCollection<Document> memberCollection;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private FindIterable<Document> findIterable;

	private MemberMongoRepository memberRepository;

	private Member validMember;
	private Book validBook;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(mongoClient.getDatabase("testDB")).thenReturn(mongoDatabase);
		when(mongoDatabase.getCollection("members")).thenReturn(memberCollection);

		memberRepository = new MemberMongoRepository(mongoClient, "testDB", "members", bookRepository);

		validBook = new Book(1, "123", "Book1", "Author1", "Genre1", new ArrayList<>());
		validMember = new Member(1, "owais", "owais@gmail.com", validBook);
	}

	@Test
	public void findById200() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "owais").append("email", "owais@gmail.com")
				.append("bookId", 1);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		Member result = memberRepository.findById(1);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("owais", result.getName());
		assertEquals(validBook, result.getBook());
	}

	@Test
	public void findByIdReturnNull() {
		// Given
		when(memberCollection.find(Filters.eq("id", 999))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		Member result = memberRepository.findById(999);

		// Then
		assertNull(result);
	}

	@Test
	public void findAll200() {
		// Given
		Document doc1 = new Document().append("id", 1).append("name", "owais").append("email", "owais@gmail.com")
				.append("bookId", 1);
		Document doc2 = new Document().append("id", 2).append("name", "test2").append("email", "test2@gmail.com")
				.append("bookId", null);

		when(memberCollection.find()).thenReturn(findIterable);
		when(findIterable.spliterator()).thenReturn(Arrays.asList(doc1, doc2).spliterator());
		when(bookRepository.findById(1)).thenReturn(validBook);

		// When
		List<Member> result = memberRepository.findAll();

		// Then
		assertEquals(2, result.size());
		assertEquals("owais", result.get(0).getName());
		assertEquals("test2", result.get(1).getName());
		assertEquals(validBook, result.get(0).getBook());
		assertNull(result.get(1).getBook());
	}

	@Test
	public void saveInsertNewMember() {
		// Given
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		memberRepository.save(validMember);

		// Then
		verify(memberCollection).insertOne(argThat(doc -> doc.getInteger("id") == 1
				&& doc.getString("name").equals("owais") && doc.getInteger("bookId") == 1));
	}

	@Test
	public void saveExistingMember() {
		// Given
		Document existingDoc = new Document().append("id", 1).append("name", "Old Name")
				.append("email", "old@example.com").append("bookId", null);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(existingDoc);

		// When
		memberRepository.save(validMember);

		// Then
		verify(memberCollection).replaceOne(eq(Filters.eq("id", 1)),
				argThat(doc -> doc.getString("name").equals("owais") && doc.getInteger("bookId") == 1));
	}

	@Test
	public void saveMemberWithoutBook() {
		// Given
		Member memberWithoutBook = new Member(2, "test2", "test2@gmail.com", null);
		when(memberCollection.find(Filters.eq("id", 2))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(null);

		// When
		memberRepository.save(memberWithoutBook);

		// Then
		verify(memberCollection).insertOne(argThat(doc -> doc.getInteger("id") == 2 && doc.get("bookId") == null));
	}

	@Test
	public void fromDocumentToMemberNonExistentBook() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "owais").append("email", "owais@gmail.com")
				.append("bookId", 999);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);
		when(bookRepository.findById(999)).thenReturn(null);

		// When
		Member result = memberRepository.findById(1);

		// Then
		assertNotNull(result);
		assertNull(result.getBook());
	}

	@Test
	public void fromDocumentToMemberWithEmptyBorrowersList() {
		// Given
		Book repoBook = new Book(1, "123", "Book1", "Author1", "Genre1",
				Arrays.asList(new Member(99, "Dummy", "dummy@gmail.com", null)));
		Document doc = new Document().append("id", 1).append("name", "owais").append("email", "owais@gmail.com")
				.append("bookId", 1);

		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);
		when(bookRepository.findById(1)).thenReturn(repoBook);

		// When
		Member member = memberRepository.findById(1);

		// Then
		assertNotNull(member.getBook());
		assertEquals(1, member.getBook().getId());
		assertTrue(member.getBook().getBorrowers().isEmpty());
		assertNotSame(repoBook, member.getBook());
	}

	@Test
	public void fromDocumentToMemberDocumentIsNull() throws Exception {
		// Given
		java.lang.reflect.Method method = MemberMongoRepository.class.getDeclaredMethod("fromDocumentToMember",
				Document.class);
		method.setAccessible(true);

		// When
		Member result = (Member) method.invoke(memberRepository, (Document) null);

		// Then
		assertNull(result);
	}

	@Test
	public void fromDocumentToMemberNameIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("email", "owais@gmail.com").append("bookId", 1);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> memberRepository.findById(1));
	}

	@Test
	public void fromDocumentToMemberEmailIsNull() {
		// Given
		Document doc = new Document().append("id", 1).append("name", "owais").append("bookId", 1);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> memberRepository.findById(1));
	}

	@Test
	public void fromDocumentToMemberIdAndEmailIsNull() {
		// Given
		Document doc = new Document().append("name", "owais").append("bookId", 1);
		when(memberCollection.find(Filters.eq("id", 1))).thenReturn(findIterable);
		when(findIterable.first()).thenReturn(doc);

		// Then
		assertThrows(IllegalArgumentException.class, () -> memberRepository.findById(1));
	}

	@Test
	public void delete_shouldRemoveMemberById() {
		// When
		memberRepository.delete(1);
		// Then
		verify(memberCollection).deleteOne(Filters.eq("id", 1));
	}

}