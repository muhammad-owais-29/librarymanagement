package org.tdd.librarymanagement.repository;

import java.util.List;

import org.tdd.librarymanagement.entity.Member;

public interface MemberRepository {

	void save(Member member);

	Member findById(int id);

	List<Member> findAll();

}