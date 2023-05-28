import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Login;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import requests.UserClient;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class LoginUserTest {
    private String email;
    private String password;
    private int statusCode;
    private boolean success;
    private User user;
    private UserClient userClient;
    private Login login;
    private String accessToken;
    private int statusCodeCreate;
    private String message;

    public LoginUserTest(String email, String password, int statusCode, boolean success, int statusCodeCreate, String message) {
        this.email = email;
        this.password = password;
        this.statusCode = statusCode;
        this.success = success;
        this.statusCodeCreate = statusCodeCreate;
        this.message = message;

    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                {"testemailclient123@example.com", "ABcd12345!", 200, true, 200, null},
                {"testemailclient123@example.com", "12345", 401, false, 200, "email or password are incorrect"},
                {null, "ABcd12345!", 401, false, 200, "email or password are incorrect"},
                {"testemailclient123@example.com", null, 401, false, 200, "email or password are incorrect"},
                {"netTakogoClienta", "346345345", 401, false, 200, "email or password are incorrect"}

        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User("testemailclient123@example.com", "ABcd12345!", "Test Client");
        userClient = new UserClient();
        login = new Login(email, password);

    }

    @Test
    @DisplayName("Проверка логина пользователя")
    public void loginUser(){
        Response responseCreate = userClient.createUser(user);
        accessToken = responseCreate.then().log().all().statusCode(statusCodeCreate).extract().path("accessToken");
        Response responseLogin = userClient.loginUser(login);
        responseLogin.then().log().all().statusCode(statusCode).body("success", equalTo(success)).body("message", equalTo(message));
    }

   @After
    public void deleteUser() {
        if (statusCodeCreate == 200) {
            Response responseDelete = userClient.deleteUser(accessToken);
            responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
        }
    }
}
