package io.github.dougllasfps.quarkussocial.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data //anotacao para simular todos getters, setters, construtores e hashcode
public class CreateUserRequest { //Equivalente a UserDTO

    @NotBlank(message = "Name is Required") //vê se não é nula nem vazia
    private String name;

    @NotNull(message = "Age is Required") //vê se não é nulo
    private Integer age;
}
