import Setting.SettingProperty;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private List<String>ingredients;
    private int fromIndex;
    private int toIndex;
    private int statusCode;
    private OrderClient orderClient;
    private Order order;
    private SettingProperty settingProperty;

    public CreateOrderTest(int fromIndex, int toIndex, int statusCode) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.statusCode = statusCode;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                //Добавить один ингредиент
                {0, 1, 200},
                //Добавить несколько ингредиентов
                {0, 7, 200},
                //Добавить несколько ингредиентов
                {10, 15, 200},
                //Не добавлять ингредиенты
                {0, 0, 400},
        };
    }

    @Before
    public void setUp() throws IOException {
        settingProperty = new SettingProperty();
        RestAssured.baseURI = settingProperty.getPropertyUrl();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Проверка создания заказа")
    public void createOrder(){
        Response responseGetIngredient = orderClient.getIngredient();
        ingredients = new ArrayList<>(responseGetIngredient.then().log().all().statusCode(200).extract().path("data._id"));
        order = new Order(ingredients.subList(fromIndex, toIndex));
        Response responseCreate = orderClient.createOrder(order);
        if(statusCode == 200){
            responseCreate.then().log().all().assertThat().body("order.number", notNullValue()).body("success", equalTo(true));
        } else if (statusCode == 400) {
            responseCreate.then().log().all().assertThat().body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
        }
    }
}
