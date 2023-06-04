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
import requests.OrderClient;
import requests.UserClient;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class GetOrderUserTest {
    private String email;
    private String password;
    private String name;
    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private SettingProperty settingProperty;

    public GetOrderUserTest(String email, String password, String name) {
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
        orderClient = new OrderClient();
        user = new User(email, password, name);
        userClient = new UserClient();
        //Создать пользователя и получить токен авторизации
        Response responseCreate = userClient.createUser(user);
        accessToken = responseCreate.then().log().all().extract().path("accessToken");

    }

    @Test
    @DisplayName("Проверка получения заказов авторизованного пользователя")
    public void getOrderTest(){
        //Получить заказы авторизованного пользователя
        Response responseGetOrder = orderClient.getOrderUser(accessToken);
        responseGetOrder.then().log().all().statusCode(200).body("success", equalTo(true)).body("orders.total", notNullValue());
    }

    @Test
    @DisplayName("Проверка получения заказов НЕ авторизованного пользователя")
    public void getOrderWithoutAuthorization(){
        //Получить заказы НЕ авторизованного пользователя
        Response responseGetOrderWithoutAuthorization = orderClient.getOrderUserWithoutAuthorization();
        responseGetOrderWithoutAuthorization.then().log().all().statusCode(401).body("success", equalTo(false)).body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUser(){
        Response responseDelete = userClient.deleteUser(accessToken);
        responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
    }
}
