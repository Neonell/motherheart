package ch.motherheart.examples.jakartadata.shop.dashboard.api;

import ch.motherheart.examples.jakartadata.shop.dashboard.dto.MonthlyRevenue;
import ch.motherheart.examples.jakartadata.shop.dashboard.dto.TopProduct;
import ch.motherheart.examples.jakartadata.shop.dashboard.service.DashboardService;
import jakarta.data.page.PageRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@Path("/dashboard")
public class DashboardResource {

    private final DashboardService service;

    DashboardResource(DashboardService service) {
        this.service = service;
    }

    @GET
    @Path("revenue-by-month")
    public List<MonthlyRevenue> revenueByMonth(@QueryParam("year") @Min(1970) @Max(9999) int year) {
        return service.revenueByMonth(year);
    }

    @GET
    @Path("revenue-by-month-paginated")
    public List<MonthlyRevenue> revenueByMonthWithPagination(@QueryParam("year") @Min(1970) @Max(9999) int year,
        @QueryParam("pageNumber") @DefaultValue("0") @Min(1) int pageNumber,
        @QueryParam("pageSize") @DefaultValue("10") @Min(10) @Max(100) int pageSize) {
        return service.revenueByMonthWithPagination(year, pageNumber, pageSize);
    }

    @GET
    @Path("top-products")
    public List<TopProduct> topProducts(@QueryParam("year") @Min(1970) @Max(9999) int year,
                                        @QueryParam("limit") @DefaultValue("5") @Min(1) @Max(100) int limit) {
        return service.topProducts(year, limit);
    }
}
