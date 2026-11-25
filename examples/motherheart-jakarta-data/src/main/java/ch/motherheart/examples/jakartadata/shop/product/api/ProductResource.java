package ch.motherheart.examples.jakartadata.shop.product.api;

import ch.motherheart.examples.jakartadata.shop.product.dto.ProductListItem;
import ch.motherheart.examples.jakartadata.shop.product.dto.ProductRequest;
import ch.motherheart.examples.jakartadata.shop.product.dto.ProductView;
import ch.motherheart.examples.jakartadata.shop.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Path("/products")
public class ProductResource {

    private final ProductService service;

    ProductResource(ProductService service) {
        this.service = service;
    }

    @GET
    public List<ProductListItem> list(@QueryParam("maxPrice") BigDecimal maxPrice) {
        return service.list(maxPrice);
    }

    @GET
    @Path("{id}")
    public ProductView get(@PathParam("id") long id) {
        return service.get(id);
    }

    @POST
    public Response create(@Valid ProductRequest request) {
        ProductView created = service.create(request);
        return Response.created(URI.create("/products/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("{id}")
    public ProductView update(@PathParam("id") long id, @Valid ProductRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        service.delete(id);
    }
}
