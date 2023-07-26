package io.github.dougllasfps.quarkussocial.rest;

import io.github.dougllasfps.quarkussocial.domain.model.Follower;
import io.github.dougllasfps.quarkussocial.domain.model.Post;
import io.github.dougllasfps.quarkussocial.domain.model.User;
import io.github.dougllasfps.quarkussocial.domain.repository.FollowerRepository;
import io.github.dougllasfps.quarkussocial.domain.repository.PostRepository;
import io.github.dougllasfps.quarkussocial.domain.repository.UserRepository;
import io.github.dougllasfps.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) //outra alterantiva em vez de por a string do endereco
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach //antes de tudo faz isto
    @Transactional
    public void setUp(){
        //user padrao dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //criar o post para o user
        Post post = new Post();
        post.setText("Hellow");
        post.setUser(user);
        postRepository.persist(post);

        //user que não segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //user seguidor
        var userFollower = new User();
        userFollower.setAge(33);
        userFollower.setName("Beltrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();
        //seguir user
        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
            .contentType(ContentType.JSON)
            .body(JsonbBuilder.create().toJson(postRequest))
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserID = 999;

        given()
            .contentType(ContentType.JSON)
            .body(JsonbBuilder.create().toJson(postRequest))
            .pathParam("userId", inexistentUserID)
        .when()
            .post()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserID = 999;

        given()
            .pathParam("userId", inexistentUserID)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    //Caso de teste 1 baseados no ListPost() do PostResource
    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given()
            .pathParam("userId", userId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("You forgot the header followerId"));
    }

    //Caso de teste 2 baseados no ListPost() do PostResource
    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){
        var inexistentFollowerID = 999;

        given()
            .pathParam("userId", userId)
            .header("followerId", inexistentFollowerID)
        .when()
            .get()
        .then()
            .statusCode(400)
        .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest(){
        given()
            .pathParam("userId", userId)
            .header("followerId", userNotFollowerId)
        .when()
            .get()
        .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts"));
    }

    @Test
    @DisplayName("should return posts")
    public void listPostsTest(){
        given()
            .pathParam("userId", userId)
            .header("followerId", userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }
}