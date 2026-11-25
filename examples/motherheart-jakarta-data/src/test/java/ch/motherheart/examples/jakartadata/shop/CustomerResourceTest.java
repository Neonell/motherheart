package ch.motherheart.examples.jakartadata.shop;

import static ch.motherheart.examples.jakartadata.shop.Api.json;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CustomerResourceTest {

    @Test
    void crudRoundTrip() {
        long id = json().body(Map.of("name", "Margaret Hamilton", "email", "margaret@example.org"))
                .post("/customers")
                .then().statusCode(201)
                .body("name", equalTo("Margaret Hamilton"))
                .extract().jsonPath().getLong("id");

        json().get("/customers/{id}", id)
                .then().statusCode(200)
                .body("email", equalTo("margaret@example.org"));

        json().body(Map.of("name", "Margaret H. Hamilton", "email", "mhh@example.org"))
                .put("/customers/{id}", id)
                .then().statusCode(200)
                .body("name", equalTo("Margaret H. Hamilton"));

        json().get("/customers")
                .then().statusCode(200)
                .body("name", hasItem("Margaret H. Hamilton"))
                // a list item is a projection: the e-mail is not even loaded
                .body("[0]", not(hasKey("email")));

        json().delete("/customers/{id}", id).then().statusCode(204);
        json().get("/customers/{id}", id).then().statusCode(404);
    }

    @Test
    void invalidRequestIsRejected() {
        json().body(Map.of("name", "", "email", "not-an-email"))
                .post("/customers")
                .then().statusCode(400);
    }

    @Test
    void theHeartRefusesToDeleteACustomerWithOrders() {
        json().delete("/customers/{id}", Api.customerIdOf("Ada Lovelace"))
                .then().statusCode(409);
    }
}
