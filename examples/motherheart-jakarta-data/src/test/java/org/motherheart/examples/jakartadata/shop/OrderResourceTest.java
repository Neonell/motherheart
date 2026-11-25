package ch.motherheart.examples.jakartadata.shop;

import static ch.motherheart.examples.jakartadata.shop.Api.json;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OrderResourceTest {

    @Test
    void placeAnOrderAndReadItsSummary() {
        long customer = Api.createCustomer("Barbara Liskov");
        long pen = Api.createProduct("Pen", "1.50");
        long notebook = Api.createProduct("Notebook", "4.20");

        json().body(Map.of("customerId", customer, "lines", List.of(
                        Map.of("productId", pen, "quantity", 4),
                        Map.of("productId", notebook, "quantity", 2))))
                .post("/orders")
                .then().statusCode(201)
                .body("customerId", equalTo((int) customer))
                .body("total", closeTo(14.40, 0.001));

        json().queryParam("customerId", customer)
                .get("/orders")
                .then().statusCode(200)
                .body("", hasSize(1))
                .body("[0].lineCount", equalTo(2))
                .body("[0].total", closeTo(14.40, 0.001));
    }

    @Test
    void unknownCustomerIsRejected() {
        long pen = Api.createProduct("Pencil", "0.80");
        json().body(Map.of("customerId", 999_999, "lines", List.of(Map.of("productId", pen, "quantity", 1))))
                .post("/orders")
                .then().statusCode(400)
                .body("error", containsString("unknown customer"));
    }

    @Test
    void unknownProductIsRejectedAndNothingIsStored() {
        long customer = Api.createCustomer("Edsger Dijkstra");
        json().body(Map.of("customerId", customer, "lines", List.of(Map.of("productId", 999_999, "quantity", 1))))
                .post("/orders")
                .then().statusCode(400)
                .body("error", containsString("unknown product"));

        json().queryParam("customerId", customer)
                .get("/orders")
                .then().statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    void anOrderNeedsLines() {
        json().body(Map.of("customerId", Api.createCustomer("Alan Kay"), "lines", List.of()))
                .post("/orders")
                .then().statusCode(400);
    }
}
