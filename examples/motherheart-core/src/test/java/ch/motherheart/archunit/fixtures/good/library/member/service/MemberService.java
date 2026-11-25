package ch.motherheart.archunit.fixtures.good.library.member.service;

import ch.motherheart.archunit.fixtures.good.library.common.DomainNotFoundException;
import ch.motherheart.archunit.fixtures.good.library.member.dto.MemberListItem;
import ch.motherheart.archunit.fixtures.good.library.member.dto.MemberView;
import ch.motherheart.archunit.fixtures.good.library.member.persistence.Member;
import ch.motherheart.archunit.fixtures.good.library.member.persistence.MemberRepository;
import java.util.List;

public class MemberService {
    private final MemberRepository repository;

    MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public MemberView get(Long id) {
        Member member = repository.findById(id).orElseThrow(() -> new DomainNotFoundException("member " + id));
        return new MemberView(member.getId(), member.getName());
    }

    public List<MemberListItem> list() {
        return repository.list();
    }

    public boolean exists(Long id) {
        return repository.countById(id) > 0;
    }
}
