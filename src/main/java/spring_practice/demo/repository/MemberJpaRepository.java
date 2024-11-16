package spring_practice.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring_practice.demo.entity.Member;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

}
