import Setting.SettingProperty;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import requests.OrderClient;

import java.io.IOException;
import java.util.List;

@RunWith(Parameterized.class)
public class CreateOrderNotValidHashTest {
    private List<String> ingredients;
    private OrderClient orderClient;
    private Order order;
    private SettingProperty settingProperty;

    public CreateOrderNotValidHashTest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        Faker faker = new Faker();
        return new Object[][]{
                {List.of(faker.number().digits(5))},
                {List.of(faker.number().digits(23))},
                {List.of(faker.number().digits(25))}

        };
    }

    @Before
    public void setUp() throws IOException {
        settingProperty = new SettingProperty();
        RestAssured.baseURI = settingProperty.getPropertyUrl();
        orderClient = new OrderClient();
        order = new Order(ingredients);
    }

    @Test
    @DisplayName("Отправить не валидный хэш при создании заказа")
    public void createOrderNotValidHash(){
        Response responseCreate = orderClient.createOrder(order);
        responseCreate.then().log().all().statusCode(500);
    }
}
