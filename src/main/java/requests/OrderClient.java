package requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDER_PATH = "api/orders";
    private static final String INGREDIENT_PATH = "api/ingredients";

    @Step("Получить данные об ингредиентах")
    public Response getIngredient(){
        return given()
                .get(INGREDIENT_PATH);
    }

    @Step("Создать заказ")
    public Response createOrder(Order order){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получить заказы конкретного авторизованного пользователя")
    public Response getOrderUser(String token){
        return given()
                .header("Authorization", token)
                .get(ORDER_PATH);
    }

    @Step("Получить заказы не авторизованного пользователя")
    public Response getOrderUserWithoutAuthorization(){
        return given()
                .get(ORDER_PATH);
    }


}
