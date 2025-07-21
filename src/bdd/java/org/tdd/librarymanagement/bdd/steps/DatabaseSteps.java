
package org.tdd.librarymanagement.bdd.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.tdd.librarymanagement.bdd.LibraryManagementRunnerBDD;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {

	public static final String DB_NAME = "testDB";
	public static final String BOOK_COLLECTION = "book";
	public static final String MEMBER_COLLECTION = "member";

	private MongoClient mongoClient;

	@Before
	public void setUp() {
		mongoClient = MongoClients.create("mongodb://localhost:" + LibraryManagementRunnerBDD.mongoPort);
		MongoDatabase db = mongoClient.getDatabase(DB_NAME);
		db.drop();
	}

	@After
	public void tearDown() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Given("The database is clean")
	public void theDatabaseIsClean() {
		MongoDatabase db = mongoClient.getDatabase(DB_NAME);
		db.drop();
		System.out.println("Database cleaned.");
	}

	@Given("The database contains the following books:")
	public void theDatabaseContainsBooks(DataTable dataTable) {
		insertBooks(dataTable);
	}

	private void insertBooks(DataTable dataTable) {
		MongoCollection<Document> bookCollection = mongoClient.getDatabase(DB_NAME).getCollection(BOOK_COLLECTION);
		List<Map<String, String>> books = dataTable.asMaps(String.class, String.class);

		for (Map<String, String> book : books) {
			Document doc = new Document().append("id", Integer.parseInt(book.get("ID")))
					.append("serialNumber", book.get("Serial Number")).append("name", book.get("Name"))
					.append("authorName", book.get("Author")).append("genre", book.get("Genre"))
					.append("borrowers", new ArrayList<>());
			bookCollection.insertOne(doc);
		}

		System.out.println("===== Inserted Books =====");
		printCollection(bookCollection);
	}

	@Given("The database contains the following members:")
	public void theDatabaseContainsMembers(DataTable dataTable) {
		MongoCollection<Document> memberCollection = mongoClient.getDatabase(DB_NAME).getCollection(MEMBER_COLLECTION);
		List<Map<String, String>> members = dataTable.asMaps(String.class, String.class);

		for (Map<String, String> member : members) {
			Document doc = new Document().append("id", Integer.parseInt(member.get("ID")))
					.append("name", member.get("Name")).append("email", member.get("Email"));
			memberCollection.insertOne(doc);
		}

		System.out.println("===== Inserted Members =====");
		printCollection(memberCollection);
	}

	private void printCollection(MongoCollection<Document> collection) {
		try (MongoCursor<Document> cursor = collection.find().iterator()) {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		}
	}
}
