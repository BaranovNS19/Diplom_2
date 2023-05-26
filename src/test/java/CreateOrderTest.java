import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import requests.UserClient;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    private User user;
    private UserClient userClient;
    private String accessToken;


    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    public void test(){
        user = new User("order@example.com", "045tijre", "получаем список");
        userClient = new UserClient();
        Response responseCreate = userClient.createUser(user);
        accessToken = responseCreate.then().extract().path("accessToken");
        Response response = given()
                .header("Authorization", accessToken)
                .get("api/ingredients");
        String product = response.then().statusCode(200).extract().path("data._id" );
        System.out.println(product);

    }

    @After
    public void deleteUser(){
        Response responseDelete = userClient.deleteUser(accessToken);
        responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
    }
}
