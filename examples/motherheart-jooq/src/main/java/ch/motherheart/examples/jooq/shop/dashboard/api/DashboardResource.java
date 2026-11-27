package ch.motherheart.examples.jooq.shop.dashboard.api;

import ch.motherheart.examples.jooq.shop.dashboard.dto.MonthlyRevenue;
import ch.motherheart.examples.jooq.shop.dashboard.dto.TopProduct;
import ch.motherheart.examples.jooq.shop.dashboard.service.DashboardService;
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

    /** Offset pagination: pages start at 1. */
    @GET
    @Path("revenue-by-month-paginated")
    public List<MonthlyRevenue> revenueByMonthPaginated(@QueryParam("year") @Min(1970) @Max(9999) int year,
        @QueryParam("pageNumber") @DefaultValue("1") @Min(1) int pageNumber,
        @QueryParam("pageSize") @DefaultValue("10") @Min(1) @Max(100) int pageSize) {
        return service.revenueByMonthPaginated(year, pageNumber, pageSize);
    }

    /**
     * Seek pagination: the first page omits {@code afterMonth}, each next page sends back the
     * {@code month} of the last row received. An empty list means there is nothing left.
     */
    @GET
    @Path("revenue-by-month-seek")
    public List<MonthlyRevenue> revenueByMonthSeeked(@QueryParam("year") @Min(1970) @Max(9999) int year,
        @QueryParam("afterMonth") @DefaultValue("0") @Min(0) @Max(12) int afterMonth,
        @QueryParam("pageSize") @DefaultValue("10") @Min(1) @Max(100) int pageSize) {
        return service.revenueByMonthSeeked(year, afterMonth, pageSize);
    }

    @GET
    @Path("top-products")
    public List<TopProduct> topProducts(@QueryParam("year") @Min(1970) @Max(9999) int year,
                                        @QueryParam("limit") @DefaultValue("5") @Min(1) @Max(100) int limit) {
        return service.topProducts(year, limit);
    }

}
