package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class homeworkTests extends BaseTestCase {

    //Ex4: GET-запрос
    @Test
    public void testGet()
    {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    //Ex5: Парсинг JSON
    @Test
    public void testGetHeaders303RestAssured()
    {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        response.prettyPrint();

        String message = response.get("messages.message[1]");
        System.out.println(message);
    }

    //Ex6: Редирект
    @Test
    public void redirectTest()
    {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        response.print();

        String url = response.getHeader("Location");
        System.out.println(url);
    }

    //Ex7: Долгий редирект
    @Test
    public void longRedirectTest()
    {
        int countRedirects = 0;
        int statusCode = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";

        while(statusCode != 200) {
            Response responseRedirect = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            countRedirects++;
            statusCode = responseRedirect.getStatusCode();
            url = responseRedirect.getHeader("Location");
            System.out.println("URL: " + url);
        }
        System.out.println("Status code: " + statusCode);
        System.out.println("Redirects before status code 200: " + countRedirects);
    }

    //Ex8: Токены
    @Test
    public void testGetToken()
    {
        //Создаем задачу
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        response.prettyPrint();

        String token = response.get("token");
        int seconds = response.get("seconds");

        //Делаем один запрос с token ДО того, как задача готова, убеждаемся в правильности поля status
        response = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        response.prettyPrint();
        String status = response.get("status");
        statusCorrect(status, "Job is NOT ready");

        //Ждем указанное время
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Делаем один запрос c token ПОСЛЕ того, как задача готова, убеждаемся в правильности поля status и наличии поля result
        response = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        response.prettyPrint();
        status = response.get("status");
        statusCorrect(status, "Job is ready");
        String result = response.get("result");
        if (result != null)
        {
            System.out.println("Result is not null and equals: " + result);
        }
        else
        {
            System.out.println("Result is null or not found");
        }
    }

    //Ex9: Подбор пароля
    @Test
    public void testPostMapBody()
    {
        String[] password =
                {"password",
                "123456",
                "123456789",
                "12345678",
                "12345",
                "qwerty",
                "abc123",
                "football",
                "1234567",
                "monkey",
                "111111",
                "letmein",
                "1234",
                "1234567890",
                "dragon",
                "baseball",
                "sunshine",
                "iloveyou",
                "trustno1",
                "princess",
                "adobe123",
                "123123",
                "welcome",
                "login",
                "admin",
                "qwerty123",
                "solo",
                "1q2w3e4r",
                "master",
                "666666",
                "photoshop",
                "1qaz2wsx",
                "qwertyuiop",
                "ashley",
                "mustang",
                "121212",
                "starwars",
                "654321",
                "bailey",
                "access",
                "flower",
                "555555",
                "passw0rd",
                "shadow",
                "lovely",
                "7777777",
                "michael",
                "!@#$%^&*",
                "jesus",
                "password1",
                "superman",
                "hello",
                "charlie",
                "888888",
                "696969",
                "hottie",
                "freedom",
                "aa123456",
                "qazwsx",
                "ninja",
                "azerty",
                "loveme",
                "whatever",
                "donald",
                "batman",
                "zaq1zaq1",
                "Football",
                "0",
                "123qwe"};

        String response = "";
        int i = 0;
        while (!Objects.equals(response, "You are authorized")){
            Response responseForCookie = RestAssured
                    .given()
                    .body("{\"login\":\"super_admin\",\"password\":\"" + password[i] + "\"}")
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookieAuth = responseForCookie.getCookie("auth_cookie");

            Response responseCheckCookie = RestAssured
                    .given()
                    .cookies("auth_cookie",responseCookieAuth)
                    .get("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();
            response = responseCheckCookie.print();
            i++;
        }
        System.out.println("Correct password is: " + password[i-1]);
    }

    public void statusCorrect(String status, String expectedStatus)
    {
        if(Objects.equals(status, expectedStatus)){
            System.out.println("Status is CORRECT and equals: " + status);
        }
        else
        {
            System.out.println("Status is INCORRECT and equals: " + status);
        }
    }

    //Ex10: Тест на короткую фразу
    @Test
    public void checkStringLengthTest()
    {
        String text = "1234567890123456";
        int textLength = text.length();

        assertTrue(textLength > 15, "The text length should be more than 15 symbols. Actual text length is " + textLength);
    }


    //Ex11: Тест запроса на метод cookie
    @Test
    public void cookieTest(){
        String name = "HomeWork";
        String value = "hw_value";

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        assertEquals(value, this.getCookie(response, name), "Actual cookie doesn't equals expected cookie");
    }

    //Ex12: Тест запроса на метод header
    @Test
    public void headerTest(){
        String name = "x-secret-homework-header";
        String value = "Some secret value";

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        assertEquals(value, this.getHeader(response, name), "Actual header doesn't equals expected header");
    }

    //Ex13: UserAgent
    @ParameterizedTest
    @MethodSource("userAgentValues")
    public void userAgentTest(String userAgentValue, String platform, String browser, String device){

        Response response = RestAssured
                .given()
                .header("User-Agent", userAgentValue)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        response.prettyPrint();

        Assertions.assertJsonByName(response, "platform", platform);
        Assertions.assertJsonByName(response, "browser", browser);
        Assertions.assertJsonByName(response, "device", device);
    }

    static Stream<Arguments> userAgentValues() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile",
                        "No",
                        "Android"),
                Arguments.of(
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "Chrome",
                        "iOS"),
                Arguments.of(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot",
                        "Unknown",
                        "Unknown"),
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web",
                        "Chrome",
                        "No"),
                Arguments.of(
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "No",
                        "iPhone")
        );
    }
}
