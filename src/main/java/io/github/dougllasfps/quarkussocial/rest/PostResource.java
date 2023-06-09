package io.github.dougllasfps.quarkussocial.rest;

import io.github.dougllasfps.quarkussocial.domain.model.Post;
import io.github.dougllasfps.quarkussocial.domain.model.User;
import io.github.dougllasfps.quarkussocial.domain.repository.FollowerRepository;
import io.github.dougllasfps.quarkussocial.domain.repository.PostRepository;
import io.github.dougllasfps.quarkussocial.domain.repository.UserRepository;
import io.github.dougllasfps.quarkussocial.rest.dto.CreatePostRequest;
import io.github.dougllasfps.quarkussocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

//@Path("/posts") - Opcao 1
@Path("/users/{userId}/posts") //Opcao 2 - aqui é utilizado o subResource /posts
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    //@Path("/{userId}")  - Opcao 1
    @POST
    @Transactional
    public Response savePost( @PathParam("userId") Long userId, CreatePostRequest request ){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        //post.setDateTime(LocalDateTime.now()); //não é preciso porque em Post estou a usar o Pre Persist

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    //Ver todos os posts sem ser seguidor
    /*
    @GET
    public Response ListPost( @PathParam("userId") Long userId ){

        //ver se user existe
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //postRepository.find("select post from Post where user = :user");
        //PanacheQuery<Post> query = postRepository.find("user", user);
        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user); //ordenado por data
        var list = query.list(); //guarda numa lista todos os resultados

        var postResponseList = list.stream() //quero que liste apenas o text e o dateTime
//              .map(post -> PostResponse.fromEntity(post)) //v1
                .map(PostResponse::fromEntity) //v2
                .collect(Collectors.toList());

        //return Response.ok(list).build(); //desta forma retorna todos os campos
        return Response.ok(postResponseList).build(); //desta forma retorna apenas o text e o dateTime
    }
     */

    //Ver todos os posts apenas se eu for seguidor
    @GET
    public Response ListPost(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId ){

        //ver se user existe
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //ver se followerId foi passado como header no pedido
        if(followerId == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        //ver se é seguidor para ver os posts
        User follower = userRepository.findById(followerId);
        if(follower == null){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Inexistent followerId")
                    .build();
        }
        boolean follows = followerRepository.follows(follower, user);
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }


        //postRepository.find("select post from Post where user = :user");
        //PanacheQuery<Post> query = postRepository.find("user", user);
        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user); //ordenado por data
        var list = query.list(); //guarda numa lista todos os resultados

        var postResponseList = list.stream() //quero que liste apenas o text e o dateTime
//              .map(post -> PostResponse.fromEntity(post)) //v1
                .map(PostResponse::fromEntity) //v2
                .collect(Collectors.toList());

        //return Response.ok(list).build(); //desta forma retorna todos os campos
        return Response.ok(postResponseList).build(); //desta forma retorna apenas o text e o dateTime
    }
}
