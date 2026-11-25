package ch.motherheart.examples.jooq.shop;

import static ch.motherheart.examples.jooq.shop.Api.json;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductResourceTest {

    @Test
    void crudRoundTrip() {
        long id = Api.createProduct("Webcam", "59.00");

        json().body(Map.of("name", "Webcam HD", "price", "64.50"))
                .put("/products/{id}", id)
                .then().statusCode(200)
                .body("price", closeTo(64.50, 0.001));

        json().delete("/products/{id}", id).then().statusCode(204);
        json().get("/products/{id}", id).then().statusCode(404);
    }

    @Test
    void listIsFilteredByPrice() {
        json().queryParam("maxPrice", "20")
                .get("/products")
                .then().statusCode(200)
                .body("name", hasItem("Mouse"))
                .body("name", not(hasItem("Monitor")))
                .body("price", everyItem(lessThanOrEqualTo(20.0)));
    }
}
