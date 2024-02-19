package requests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Login;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {
private static final String CREATE_USER_PATH = "api/auth/register";
private static final String LOGIN_USER_PATH = "api/auth/login";
private static final String USER_PATH = "api/auth/user";

@Step("Создание пользователя")
public Response createUser(User user){
    return given()
            .header("Content-type", "application/json")
            .and()
            .body(user)
            .when()
            .post(CREATE_USER_PATH);
}

@Step("Логин пользователя")
public Response loginUser(Login login){
    return given()
            .header("Content-type", "application/json")
            .and()
            .body(login)
            .when()
            .post(LOGIN_USER_PATH);
}

@Step("Получить информацию о пользователе")
public Response getUser(String token){
    return given()
            .header("Authorization", token)
            .get(USER_PATH);
}

@Step("Обновление данных авторизованного пользователя")
public Response updateUser(User user, String token){
    return given()
            .header("Authorization", token)
            .header("Content-type", "application/json")
            .and()
            .body(user)
            .when()
            .patch(USER_PATH);
}

@Step("Обновление данных рользователя без авторизации")
public Response updateUserWithoutAuthorization(User user){
    return given()
            .header("Content-type", "application/json")
            .and()
            .body(user)
            .when()
            .patch(USER_PATH);

}

@Step("Удаление пользователя")
public Response deleteUser(String token){

    return given()
            .header("Authorization", token)
            .delete(USER_PATH);

}


}
