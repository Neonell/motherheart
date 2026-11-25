package org.motherheart.archunit.fixtures.bad.crossdomainrepository.library.member.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    Long id;
}
