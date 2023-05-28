import io.qameta.allure.junit4.DisplayName;
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
public class CreateUserPositiveTest {
    private String email;
    private String password;
    private String name;
    private User user;
    private UserClient userClient;
    private String accessToken;

    public CreateUserPositiveTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                {"testemail034232@example.com", "12309", "Sergey"},
                {"wert3245234@example.com", "sdfgdf", "Сергей"},
                {"348904dfdgfd@example.com", "!#@$+=-", "12423"}

        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User(email, password, name);
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUser(){
        Response responseCreate = userClient.createUser(user);
        responseCreate.then().statusCode(200).assertThat().body("success", equalTo(true));
        accessToken = responseCreate.then().log().all().extract().path("accessToken");
        System.out.println(accessToken);
    }
    @After
    public void deleteUser(){
        Response responseDelete = userClient.deleteUser(accessToken);
        responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
    }
}
