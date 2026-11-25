package org.motherheart.archunit.fixtures.bad.resourceusesrepository.library.member.api;

import org.motherheart.archunit.fixtures.bad.resourceusesrepository.library.member.persistence.MemberRepository;
import jakarta.ws.rs.Path;

@Path("/members")
public class MemberResource {
    MemberRepository repository;

    public boolean exists(Long id) {
        return repository.findById(id).isPresent();
    }
}
