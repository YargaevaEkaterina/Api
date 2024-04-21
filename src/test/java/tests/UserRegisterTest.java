package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertStatusCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully(){
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertStatusCodeEquals(response, 200);
        Assertions.assertJsonHasField(response,"id");
    }

    //Ex15: Тесты на метод user
    @Test
    @DisplayName("Создание пользователя с некорректным email - без символа @")
    public void incorrectEmailTest(){
        String email = DataGenerator.getRandomIncorrectEmail();

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }


    @DisplayName("Создание пользователя без указания одного из полей")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void createUserWithoutField(String fieldName){

        Map<String, String> userData = new HashMap<>();
        userData.put(fieldName, null);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(response,"The following required params are missed: " + fieldName);
    }

    @Test
    @DisplayName("Создание пользователя с очень коротким именем в один символ")
    public void createUserWithShortName(){
        String name = "1";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(response,"The value of 'username' field is too short");
    }

    @Test
    @DisplayName("Создание пользователя с очень длинным именем - длиннее 250 символов")
    public void createUserWithLongName(){
        String name = "namenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamename";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", name);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseTextEquals(response, "The value of 'username' field is too long");
    }
}
