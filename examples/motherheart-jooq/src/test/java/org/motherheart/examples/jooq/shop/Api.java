package org.motherheart.examples.jooq.shop;

import static io.restassured.RestAssured.given;

import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.UUID;

/** Small helpers shared by the HTTP tests. Identical in both example modules. */
final class Api {

    /** Decimals come back as doubles (compared with closeTo), integers stay integers. */
    private static final RestAssuredConfig CONFIG = RestAssuredConfig.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(NumberReturnType.DOUBLE));

    private Api() {
    }

    static RequestSpecification json() {
        return given().config(CONFIG).contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    static long createCustomer(String name) {
        return json().body(Map.of("name", name, "email", UUID.randomUUID() + "@example.org"))
                .post("/customers").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    static long createProduct(String name, String price) {
        return json().body(Map.of("name", name, "price", price))
                .post("/products").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    static long customerIdOf(String name) {
        return json().get("/customers").then().statusCode(200)
                .extract().jsonPath().getLong("find { it.name == '" + name + "' }.id");
    }
}
