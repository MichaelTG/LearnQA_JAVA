import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloWorldTest {

    //EX5 сделать
    @Test
    public void parseJson(){
        JsonPath response = RestAssured

                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        List<String> messages = response.getList("messages.message");
        System.out.println(messages.get(1));

    }
    //EX6
    @Test
    public void redirectLink(){
        Map<String, String> headers = new HashMap<>();
        headers.put("firstHeader1", "firstValue");

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        response.print();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }
    //EX7 сделать
    @Test
    public void longRedirectLink(){
        Map<String, String> headers = new HashMap<>();
        headers.put("firstHeader1", "firstValue");
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);

        while (locationHeader != null){
            Response redirectResponse = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(locationHeader)
                    .andReturn();
            locationHeader = redirectResponse.getHeader("Location");
            System.out.println(locationHeader);
        }
    }
    //EX8
    @Test
    public void token() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        JsonPath response = RestAssured
                .get(url)
                .jsonPath();
        String tokenAnswer = response.get("token");
        int executionTime = response.get("seconds");
        Response checkResponse = RestAssured
                .given()
                .queryParam("token",tokenAnswer)
                .get(url)
                .andReturn();
        checkResponse.print();
        Thread.sleep(executionTime * 1000);
        Response successfulResponse = RestAssured
                .given()
                .queryParam("token",tokenAnswer)
                .get(url)
                .andReturn();
        successfulResponse.print();
    }


    //EX9
    @Test
    public void password(){
        String loginUrl="https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
        String checkCookieUrl="https://playground.learnqa.ru/ajax/api/check_auth_cookie";
        String correctAnswer = "You are authorized";
        String[] commonPss  = {"123456", "123456789", "qwerty", "password", "1234567", "12345678", "12345", "iloveyou", "111111", "123123", "abc123", "qwerty123", "1q2w3e4r", "admin", "qwertyuiop", "654321", "555555", "lovely", "7777777","welcome","888888","princess","dragon","password1","123qwe"};

        for (int i = 0; i < commonPss.length; i++) {
            Map<String, String> data = new HashMap<>();
            data.put("login","super_admin");
            data.put("password", commonPss[i]);
            Response response = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post(loginUrl)
                    .andReturn();

            String responseCookie= response.getCookie("auth_cookie");

            Map<String, String> checkCookieData = new HashMap<>();
            checkCookieData.put("auth_cookie",responseCookie);

            Response  checkLoginCookies = RestAssured
                    .given()
                    .cookies(checkCookieData)
                    .when()
                    .get(checkCookieUrl)
                    .andReturn();
            String stringResponce = checkLoginCookies.asString();

            if (stringResponce.equals(correctAnswer)){
                System.out.println("correct password is: "+ commonPss[i]);
                break;
            }
            }
        }
    }

