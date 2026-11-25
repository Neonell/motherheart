package org.motherheart.archunit.fixtures.good.library.member.api;

import org.motherheart.archunit.fixtures.good.library.member.dto.MemberListItem;
import org.motherheart.archunit.fixtures.good.library.member.dto.MemberView;
import org.motherheart.archunit.fixtures.good.library.member.service.MemberService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

@Path("/members")
public class MemberResource {
    private final MemberService service;

    MemberResource(MemberService service) {
        this.service = service;
    }

    @GET
    public List<MemberListItem> list() {
        return service.list();
    }

    @GET
    @Path("{id}")
    public MemberView get(@PathParam("id") Long id) {
        return service.get(id);
    }
}
