package ch.motherheart.examples.jooq.shop;

import static ch.motherheart.examples.jooq.shop.Api.json;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/** Runs against the 2025 demo data (db/testdata), which no other test touches. */
@QuarkusTest
class DashboardResourceTest {

    @Test
    void revenueByMonth() {
        json().queryParam("year", 2025)
                .get("/dashboard/revenue-by-month")
                .then().statusCode(200)
                .body("month", contains(1, 2, 3))
                .body("orders", contains(2, 1, 1))
                .body("revenue", contains(closeTo(288.70, 0.001), closeTo(69.90, 0.001), closeTo(398.00, 0.001)));
    }

    @Test
    void topProducts() {
        json().queryParam("year", 2025).queryParam("limit", 3)
                .get("/dashboard/top-products")
                .then().statusCode(200)
                .body("", hasSize(3))
                .body("name", contains("Monitor", "Mouse", "Cable"))
                .body("quantity", contains(3, 3, 10))
                .body("revenue", contains(closeTo(597.00, 0.001), closeTo(59.70, 0.001), closeTo(50.00, 0.001)));
    }

    @Test
    void anEmptyYearIsAnEmptyList() {
        json().queryParam("year", 1999)
                .get("/dashboard/revenue-by-month")
                .then().statusCode(200)
                .body("", hasSize(0));
    }
}
