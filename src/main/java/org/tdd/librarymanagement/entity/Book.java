package org.tdd.librarymanagement.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Book")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "serialNumber", unique = true, nullable = false)
	private String serialNumber;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "authorName", nullable = false)
	private String authorName;

	@Column(name = "genre", nullable = false)
	private String genre;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Member> borrowers = new ArrayList<>();

	public Book() {
	}

	public Book(int id, String serialNumber, String name, String authorName, String genre, List<Member> borrowers) {
		this.id = id;
		this.serialNumber = serialNumber;
		this.name = name;
		this.authorName = authorName;
		this.genre = genre;
		this.borrowers = borrowers != null ? borrowers : new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public List<Member> getBorrowers() {
		return borrowers;
	}

	public void setBorrowers(List<Member> borrowers) {
		this.borrowers = borrowers;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Book book = (Book) o;
		return id == book.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return name;
	}
}