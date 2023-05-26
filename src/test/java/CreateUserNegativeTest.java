import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import requests.UserClient;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserNegativeTest {
    private String email;
    private String password;
    private String name;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private int statusCode;
    private boolean success;
    private String message;

    public CreateUserNegativeTest(String email, String password, String name, int statusCode, boolean success, String message) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.statusCode = statusCode;
        this.success = success;
        this.message = message;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                {"vasya123@example.com", null , "Василий", 403, false, "Email, password and name are required fields"},
                {null, "1234", "Nikolay", 403, false, "Email, password and name are required fields"},
                {"34534@example.com", "34634", null, 403, false, "Email, password and name are required fields"},
                {"alexey22@example.com", "6574", "Алексей", 200, true, null}

        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User(email, password, name);
        userClient = new UserClient();
    }

    @Test
    public void createUser(){
        Response responseCreate = userClient.createUser(user);
        responseCreate.then().log().all().statusCode(statusCode).body("success", equalTo(success)).body("message", equalTo(message));
        if(responseCreate.statusCode()==200) {
            accessToken = responseCreate.then().extract().path("accessToken");
            Response responseCreate2 = userClient.createUser(user);
            responseCreate2.then().log().all().statusCode(403).body("success", equalTo(false)).body("message", equalTo("User already exists"));

        }

    }

    @After
    public void deleteUser(){
        if(statusCode==200){
            userClient.deleteUser(accessToken);
        }
    }
}
