package io.github.dougllasfps.quarkussocial.rest;

import io.github.dougllasfps.quarkussocial.domain.model.User;
import io.github.dougllasfps.quarkussocial.domain.repository.UserRepository;
import io.github.dougllasfps.quarkussocial.rest.dto.CreateUserRequest;
import io.github.dougllasfps.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository; //injecao de dependencia do UserRepository
    private Validator validator;

    @Inject //injecao de dependencia do UserRepository
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository; //UserRepository
        this.validator = validator; //+ Validacao dos campos obrigatorios(name+age) em CreateUserRequest
    }

    @POST
    @Transactional //para abrir transacao - sempre que alterar BD
    public Response createUser( CreateUserRequest userRequest){

        //Validar campos campos obrigatorios(CreateUserRequest) em falta e apresentar mensagem se faltar algum
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            //return Response.status(400).entity(responseError).build(); //Cria codigo 400
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS); //Cria codigo 422 em vez de 400
        }

        //Adicionar user
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        repository.persist(user); //save in DB - to persist
        //User. delete("delete from User where age < 18");
        //return Response.ok(user).build(); //Cria codigo 200
        return Response
                .status(Response.Status.CREATED.getStatusCode()) //Cria codigo 201 em vez de 200
                .entity(user)
                .build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}") //No @PathParam tem de ter a mesma string
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);
        if(user != null){
            repository.delete(user);
            //return Response.ok().build();
            return Response.noContent().build(); //codigo 204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){
        User user = repository.findById(id);
        if(user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            //return Response.ok().build();
            return Response.noContent().build(); //codigo 204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
