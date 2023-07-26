package io.github.dougllasfps.quarkussocial.rest;

import io.github.dougllasfps.quarkussocial.rest.dto.CreateUserRequest;
import io.github.dougllasfps.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.json.bind.JsonbBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //para ordenar os metodos de teste desta class
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("should create an user successfully")
    @Order(1) //definir ordem de execucao de cada metodo
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        //GIVEN - dado este cenario
        //WHEN - quando executo
        //THEN - isto deve acontecer
        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(JsonbBuilder.create().toJson(user))
                .when()
                    .post(apiURL)
                .then()
                    .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("should return error when json is not valid")
    @Order(2) //definir ordem de execucao de cada metodo
    public void createUserValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        //GIVEN - dado este cenario
        //WHEN - quando executo
        //THEN - isto deve acontecer
        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(JsonbBuilder.create().toJson(user))
                .when()
                        .post(apiURL)
                .then()
                        .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode()); //devolve erro
        assertEquals("Validation Error", response.jsonPath().getString("message")); //mensagem do erro

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        //Tem de haver 2 mensagens de erro
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
        //assertEquals("Age is Required", errors.get(0).get("message"));
        //assertEquals("Name is Required", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("should list all users")
    @Order(3) //definir ordem de execucao de cada metodo
    public void listAllUsersTest(){
        given()
            .contentType(ContentType.JSON)
        .when()
            .post(apiURL)
        .then()
            .statusCode(500)
            .body("size()", Matchers.is(2));
    }
}