package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue){
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Json value is not equal to expected " + name);
    }

    public static void assertJsonByName(Response response, String name, String expectedValue){
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "Json value is not equal to expected " + name);
    }

    public static void assertResponseTextEquals(Response response, String expectedValue){
        assertEquals(expectedValue, response.asString(), "Response text is not expected");
    }

    public static void assertStatusCodeEquals(Response response, int expectedStatusCode){
        assertEquals(expectedStatusCode, response.statusCode(), "Response status code is not expected");
    }

    public static void assertJsonHasKey(Response response, String expectedFieldName){
        response.then().assertThat().body("$", hasKey(expectedFieldName));
    }
}
