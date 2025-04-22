package org.tdd.librarymanagement.repository;

import java.util.List;

import org.tdd.librarymanagement.entity.Book;

public interface BookRepository {

	void save(Book book);

	Book findById(int id);

	List<Book> findAll();

	Book findBySerialNumber(String serialNumber);

	void delete(String serialNumber);

}