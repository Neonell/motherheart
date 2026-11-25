package org.motherheart.examples.jooq.shop.order.api;

import org.motherheart.examples.jooq.shop.order.dto.OrderSummary;
import org.motherheart.examples.jooq.shop.order.dto.PlaceOrderRequest;
import org.motherheart.examples.jooq.shop.order.dto.PlacedOrder;
import org.motherheart.examples.jooq.shop.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/orders")
public class OrderResource {

    private final OrderService service;

    OrderResource(OrderService service) {
        this.service = service;
    }

    @POST
    public Response place(@Valid @NotNull PlaceOrderRequest request) {
        PlacedOrder placed = service.place(request);
        return Response.created(URI.create("/orders/" + placed.id())).entity(placed).build();
    }

    @GET
    public List<OrderSummary> summaries(@QueryParam("customerId") @NotNull Long customerId) {
        return service.summariesFor(customerId);
    }
}
