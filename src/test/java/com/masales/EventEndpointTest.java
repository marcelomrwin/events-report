package com.masales;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class EventEndpointTest {

    @Test
    public void testGetEvents(){
        given().when().get("/events").then().statusCode(200);
    }

    @Test
    public void testCount(){
        given().when().get("/events/count").then().statusCode(200);//.body(is("33989"));
    }
}
