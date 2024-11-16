package spring_practice.demo.repository;

import spring_practice.demo.entity.Member;

import java.util.Optional;

public interface MemberRepository {

    boolean checkEmail(String email);

    void save(Member member);

    Optional<Member> findByEmail(String email);
}
