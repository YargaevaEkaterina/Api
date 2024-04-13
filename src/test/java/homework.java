import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HomeworkTests {

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
}
