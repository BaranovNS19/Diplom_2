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

    public GetOrderUserTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                {"clientOrder@example.com", "03493", "Victor"},
                {"ilyamodnic@example.com", "028349823", "Ilya"},
                {"darya1222@example.com", "Doc740", "Daria"}
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
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
