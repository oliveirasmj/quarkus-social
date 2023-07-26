package io.github.dougllasfps.quarkussocial.rest;

import io.github.dougllasfps.quarkussocial.domain.model.User;
import io.github.dougllasfps.quarkussocial.domain.repository.FollowerRepository;
import io.github.dougllasfps.quarkussocial.domain.repository.UserRepository;
import io.github.dougllasfps.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;
    Long followerId;


    @BeforeEach //antes de tudo faz isto
    @Transactional
    public void setUp(){
        //user padrao dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //o seguidor
        var follower = new User();
        follower.setAge(30);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(JsonbBuilder.create().toJson(body))
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 when userId doen't exist")
    public void userNotFoundTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var inexistentUserID = 999;

        given()
            .contentType(ContentType.JSON)
            .body(JsonbBuilder.create().toJson(body))
            .pathParam("userId", inexistentUserID)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(JsonbBuilder.create().toJson(body))
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}