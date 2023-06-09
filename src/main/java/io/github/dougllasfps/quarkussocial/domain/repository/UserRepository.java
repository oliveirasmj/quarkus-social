package io.github.dougllasfps.quarkussocial.domain.repository;

import io.github.dougllasfps.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped //criar uma instancia desta classe no local desejável - injecao de dependencia - é como se fosse um singletoon - única classe para todos
public class UserRepository implements PanacheRepository<User> {
}
