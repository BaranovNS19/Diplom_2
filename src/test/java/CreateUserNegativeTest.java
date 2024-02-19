import Setting.SettingProperty;
import com.github.javafaker.Faker;
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

import java.io.IOException;

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
    private SettingProperty settingProperty;

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
        Faker faker = new Faker();
        return new Object[][]{
                //Пользователь не заполнил пароль
                {faker.internet().emailAddress(), null , faker.name().firstName(), 403, false, "Email, password and name are required fields"},
                //Пользователь не заполнил email
                {null, faker.internet().password(), faker.name().firstName(), 403, false, "Email, password and name are required fields"},
                //Пользователь не заполнил имя
                {faker.internet().emailAddress(), faker.internet().password(), null, 403, false, "Email, password and name are required fields"},
                //Создание существующего пользователя
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName(), 200, true, null}

        };
    }

    @Before
    public void setUp() throws IOException {
        settingProperty = new SettingProperty();
        RestAssured.baseURI = settingProperty.getPropertyUrl();
        user = new User(email, password, name);
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Проверка создания пользователя с НЕ валидными данными и попытка создания существующего пользователя")
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
