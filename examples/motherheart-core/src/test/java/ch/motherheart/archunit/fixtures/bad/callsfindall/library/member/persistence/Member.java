package ch.motherheart.archunit.fixtures.bad.callsfindall.library.member.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    Long id;
}
