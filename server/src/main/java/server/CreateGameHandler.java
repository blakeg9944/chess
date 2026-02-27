package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.CreateGameService;
import service.LoginService;
import service.UnauthorizedException;

import java.util.Map;

public class CreateGameHandler implements Handler {
    private final CreateGameService service;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            Gson gson = new Gson();
            String jsonBody = ctx.body();
            CreateGameRequest createGameRequest = gson.fromJson(jsonBody, CreateGameRequest.class);
            String authToken = ctx.header("Authorization");
            CreateGameResult createGameResult = service.createGame(createGameRequest, authToken);
            String jsonResponse = gson.toJson(createGameResult);
            ctx.result(jsonResponse);
        }
        catch(BadRequestException badRequestException){
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        catch(UnauthorizedException unauthorizedException){
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        }
        catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: (description of error)")));
        }
    }
}
