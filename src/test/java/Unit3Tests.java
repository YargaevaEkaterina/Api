import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.restassured.http.Headers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Unit3Tests {

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    public void testAssert(String name)
    {
        Map<String, String> queryParams = new HashMap<>();

        if(name.length() > 0){
            queryParams.put("name", name);
        }
        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0) ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
    }

    @Test
    public void authTest()
    {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        Map<String, String> cookies = response.getCookies();
        Headers headers = response.getHeaders();
        int userIdOnAuth = response.jsonPath().getInt("user_id");

        assertEquals(200, response.statusCode(), "Unexpected status code");
        assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have 'auth_sid' cookie");
        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't have 'x-csrf-token' headers");
        assertTrue(response.jsonPath().getInt("user_id") > 0, "User id should be greater then 0");

        JsonPath responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", response.getHeader("x-csrf-token"))
                .cookie("auth_sid", response.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();

        int userIdCheck = responseCheckAuth.getInt("user_id");
        assertTrue(userIdCheck > 0, "Unexpected user id" + userIdCheck);
        assertEquals(userIdOnAuth, userIdCheck, "User id from auth is not equals User id on check");
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void negativeAuthTest(String condition)
    {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Map<String, String> cookies = response.getCookies();
        Headers headers = response.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")){
            spec.cookie("auth_sid", cookies.get("auth_sid"));
        }
        else if(condition.equals("headers")){
            spec.header("x-csrf-token", headers.get("x-csrf-token"));
        }
        else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }

        JsonPath responseForCheck = spec.get().jsonPath();
        assertEquals(0, responseForCheck.getInt("user_id"), "User id should be 0 or unauth request");
    }
}

