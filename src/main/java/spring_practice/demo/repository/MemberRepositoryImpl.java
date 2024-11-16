package spring_practice.demo.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import spring_practice.demo.entity.Member;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public boolean checkEmail(String email){
        return memberJpaRepository.findByEmail(email).isPresent();
    }

    @Override
    public void save(Member member){
        memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email);
    }

}
