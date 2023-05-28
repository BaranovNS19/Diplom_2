import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import requests.OrderClient;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private List<String>ingredients;
    private int statusCode;
    private OrderClient orderClient;
    private Order order;

    public CreateOrderTest(List<String> ingredients, int statusCode) {
        this.ingredients = ingredients;
        this.statusCode = statusCode;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                //Добавить один ингредиент
                {List.of("61c0c5a71d1f82001bdaaa7a"), 200},
                //Добавить несколько ингредиентов
                {List.of("61c0c5a71d1f82001bdaaa74", "61c0c5a71d1f82001bdaaa78", "61c0c5a71d1f82001bdaaa76"), 200},
                //Не добавлять ингредиенты
                {null, 400},
                //Добавить не валидный хэш ингредиента
                {List.of("95869"), 500}

        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderClient = new OrderClient();
        order = new Order(ingredients);
    }

    @Test
    @DisplayName("Проверка создания заказа")
    public void createOrder(){
        Response responseCreate = orderClient.createOrder(order);
        if(statusCode == 200){
            responseCreate.then().log().all().assertThat().body("order.number", notNullValue()).body("success", equalTo(true));
        } else if (statusCode == 400) {
            responseCreate.then().log().all().assertThat().body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
        } else if (statusCode == 500) {
            responseCreate.then().log().all().statusCode(statusCode);
        }
    }
}
