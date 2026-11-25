package org.motherheart.archunit.fixtures.bad.dtousesentity.library.member.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    public Long id;
}
