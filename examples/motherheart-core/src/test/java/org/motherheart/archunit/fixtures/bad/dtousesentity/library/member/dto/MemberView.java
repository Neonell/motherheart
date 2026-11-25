package org.motherheart.archunit.fixtures.bad.dtousesentity.library.member.dto;

import org.motherheart.archunit.fixtures.bad.dtousesentity.library.member.persistence.Member;

public record MemberView(Long id) {
    static MemberView from(Member member) {
        return new MemberView(member.id);
    }
}
