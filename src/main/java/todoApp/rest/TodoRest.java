package todoApp.rest;

import todoApp.entity.Todo;
import todoApp.service.QueryService;
import todoApp.service.TodoService;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authz
public class TodoRest {

    @Inject
    TodoService todoService;
    @Inject
    QueryService queryService;

    // Create new Todo and persist
    @Path("new")
    @POST
    public Response createTodo(Todo todo) {
        todoService.createTodo(todo);

        return Response.ok(todo).build();
    }

    // Update existing todo
    @Path("update")
    @PUT
    public Response updateTodo(Todo todo) {
        todoService.updateTodo(todo);
        return Response.ok(todo).build();
    }

    // Get todo by Id
    @Path("{id}")
    @GET
    public Todo getTodo(@PathParam("id") Long id) {
        return queryService.findTodoById(id);
    }


    // List all todos owned by current user
    @Path("list")
    @GET
    public List<Todo> getTodos() {
        return queryService.getAllTodos();
    }

    // Complete existing todo
    @Path("status")
    @POST
    public Response markAsComplete(@QueryParam("id") Long id) {
        Todo todo = todoService.findToDoById(id);
        todo.setIsCompleted(true);
        todoService.updateTodo(todo);

        return Response.ok(todo).build();

    }
}
