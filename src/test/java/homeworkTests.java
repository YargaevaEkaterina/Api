import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;


public class homeworkTests {

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

    @Test
    public void testGetToken1()
    {
        JsonPath responseForStatus = RestAssured
                .given()
                .queryParam("token", "AMzozMxoDOxAyMx0CNw0CNyAjM")
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        responseForStatus.prettyPrint();
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
}
