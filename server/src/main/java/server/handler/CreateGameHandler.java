package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.CreateGameResult;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.CreateGameService;
import service.UnauthorizedException;

import java.util.Map;

import static server.handler.Response.failure;
import static server.handler.Response.success;

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
            success(ctx, createGameResult);
        }
        catch(Exception e) {
            failure(ctx, e);
        }
    }
}
