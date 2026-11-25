package org.motherheart.archunit.fixtures.good.library.member.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
