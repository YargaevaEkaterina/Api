import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.restassured.http.Headers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Unit3Tests {

    String cookie;
    String header;
    int userIdOnAuth;
    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = response.getCookie("auth_sid");
        this.header = response.getHeader("x-csrf-token");
        this.userIdOnAuth = response.jsonPath().getInt("user_id");
    }

    @Test
    public void authTest()
    {
        JsonPath responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
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
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")){
            spec.cookie("auth_sid", this.cookie);
        }
        else if(condition.equals("headers")){
            spec.header("x-csrf-token", this.header);
        }
        else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }

        JsonPath responseForCheck = spec.get().jsonPath();
        assertEquals(0, responseForCheck.getInt("user_id"), "User id should be 0 or unauth request");
    }


}

