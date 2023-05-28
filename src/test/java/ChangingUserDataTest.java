import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Login;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import requests.UserClient;

import static org.hamcrest.Matchers.equalTo;
@RunWith(Parameterized.class)
public class ChangingUserDataTest {
    private String email;
    private String updateEmail;
    private String password;
    private String updatePassword;
    private String name;
    private String updateName;
    private User user;
    private UserClient userClient;
    private String accessToken;
    private Login login;

    public ChangingUserDataTest(String email, String updateEmail, String password, String updatePassword, String name, String updateName) {
        this.email = email;
        this.updateEmail = updateEmail;
        this.password = password;
        this.updatePassword = updatePassword;
        this.name = name;
        this.updateName = updateName;
    }

    @Parameterized.Parameters
    public static Object[][]getData(){
        return new Object[][]{
                //Обновляются все данные
                {"old@example.com", "new.email@example.com", "43534", "NewPassword123", "Nikolay", "Sergey"},
                //Обновляется только пароль
                {"old@example.com", "old@example.com", "43534", "New678", "Nikolay", "Nikolay"},
                //Обновляется только email
                {"old@example.com", "new340@example.com", "43534", "43534", "Nikolay", "Nikolay"},
                //Обновляется только имя
                {"old@example.com", "new340@example.com", "43534", "43534", "Nikolay", "Sergey"}

        };
    }

    @Before
   public void setUp(){
       RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
       user = new User(email, password, name);
       userClient = new UserClient();
       login = new Login(updateEmail, updatePassword);
       //Создание пользователя и получение токена
        Response responseCreate = userClient.createUser(user);
        accessToken = responseCreate.then().log().all().extract().path("accessToken");
   }

    @Test
    @DisplayName("Проверка обновления данных авторизованного пользователя")
    public void updateUserTest(){
        //Получение данных пользователя (email, Имя)
        Response responseGetUser = userClient.getUser(accessToken);
        responseGetUser.then().log().all().statusCode(200).body("success", equalTo(true)).body("user.email", equalTo(email)).body("user.name", equalTo(name));
        //Обновление данных пользователя
        Response responseUpdate = userClient.updateUser(new User(updateEmail, updatePassword, updateName), accessToken);
        responseUpdate.then().log().all().statusCode(200).body("success", equalTo(true)).body("user.email", equalTo(updateEmail)).body("user.name", equalTo(updateName));
        //Получение обновленных данных пользователя
        Response responseGetUserAfterUpdate = userClient.getUser(accessToken);
        responseGetUserAfterUpdate.then().log().all().statusCode(200).body("success", equalTo(true)).body("user.email", equalTo(updateEmail)).body("user.name", equalTo(updateName));
        //Логин пользователя (для проверки обновленного пароля)
        Response responseLogin = userClient.loginUser(login);
        responseLogin.then().log().all().statusCode(200);

    }

    @Test
    @DisplayName("Проверка обновления данных НЕ авторизованного пользователя")
    public void updateUserWithoutAuthorizationTest(){
        //Обновление данных не авторизованного пользователя
        Response responseUpdateWithoutAuthorization = userClient.updateUserWithoutAuthorization(new User(updateEmail, updatePassword, updateName));
        responseUpdateWithoutAuthorization.then().log().all().statusCode(401).body("success", equalTo(false)).body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUser(){
        Response responseDelete = userClient.deleteUser(accessToken);
        responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
    }
}


