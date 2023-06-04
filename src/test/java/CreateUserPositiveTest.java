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
public class CreateUserPositiveTest {
    private String email;
    private String password;
    private String name;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private SettingProperty settingProperty;

    public CreateUserPositiveTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        Faker faker = new Faker();
        return new Object[][]{
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()},
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()},
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()}

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
