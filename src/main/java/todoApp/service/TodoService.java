
package todoApp.service;

import todoApp.entity.Todo;
import todoApp.entity.User;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Stateless
public class TodoService {

    @Inject
    private EntityManager entityManager;

    @Inject
    private QueryService queryService;

    @Inject
    private SecurityUtil securityUtil;

    @Context
    private SecurityContext securityContext;



    public User saveUser(User user) {
        // Check if user exists in database
        Long count = (Long) queryService.countUserByEmail(user.getEmail()).get(0);
        // If user is not existing in database, proceed to add it
        if (user.getId() == null && count == 0) {
            // Hash password passed by user
            Map<String, String> credMap = securityUtil.hashPassword(user.getPassword());
            // Add hashed password to database
            user.setPassword(credMap.get(SecurityUtil.HASHED_PASSWORD_KEY));
            // Add salt to database
            user.setSalt(credMap.get(SecurityUtil.SALT_KEY));
            // Add new user to database
            entityManager.persist(user);
            // Clear map which was storing hashed password and salt
            credMap.clear();
        }
        return user;
    }


    public Todo createTodo(Todo todo) {
        // Get current logged user
        User userByEmail = queryService.findUserByEmail(securityContext.getUserPrincipal().getName());
        // If user is logged in, mark him as owner of new todo
        if (userByEmail != null) {
            todo.setTodoOwner(userByEmail);
            entityManager.persist(todo);
        }
        return todo;
    }


    public Todo updateTodo(Todo todo) {
        entityManager.merge(todo);
        return todo;
    }


    public Todo findToDoById(Long id) {
        return queryService.findTodoById(id);
    }


    public List<Todo> getTodos() {
        return queryService.getAllTodos();
    }
}
