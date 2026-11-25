package org.motherheart.examples.jooq.shop.customer.api;

import org.motherheart.examples.jooq.shop.customer.dto.CustomerListItem;
import org.motherheart.examples.jooq.shop.customer.dto.CustomerRequest;
import org.motherheart.examples.jooq.shop.customer.dto.CustomerView;
import org.motherheart.examples.jooq.shop.customer.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/customers")
public class CustomerResource {

    private final CustomerService service;

    CustomerResource(CustomerService service) {
        this.service = service;
    }

    @GET
    public List<CustomerListItem> list() {
        return service.list();
    }

    @GET
    @Path("{id}")
    public CustomerView get(@PathParam("id") long id) {
        return service.get(id);
    }

    @POST
    public Response create(@Valid CustomerRequest request) {
        CustomerView created = service.create(request);
        return Response.created(URI.create("/customers/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("{id}")
    public CustomerView update(@PathParam("id") long id, @Valid CustomerRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        service.delete(id);
    }
}
