package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.JoinGameService;
import service.UnauthorizedException;

import java.util.Map;

public class JoinGameHandler implements Handler {
    Gson gson = new Gson();
    private final JoinGameService service;

    public JoinGameHandler(JoinGameService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            String jsonBody = ctx.body();
            JoinGameRequest joinGameRequest = gson.fromJson(jsonBody, JoinGameRequest.class);
            String authToken = ctx.header("Authorization");
            service.joinGame(joinGameRequest, authToken);
        }
        catch(BadRequestException badRequestException){
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
        catch(UnauthorizedException unauthorizedException){
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        }
        catch(AlreadyTakenException alreadyTakenException) {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        }
        catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: (description of error)")));
        }
    }
}
