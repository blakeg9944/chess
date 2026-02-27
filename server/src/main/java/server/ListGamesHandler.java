package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.ListGamesRequest;
import model.ListGamesResult;
import org.jetbrains.annotations.NotNull;
import service.ListGamesService;
import service.UnauthorizedException;

import java.util.Map;

public class ListGamesHandler implements Handler {
    private final ListGamesService service;
    private final Gson gson = new Gson();

    public ListGamesHandler(ListGamesService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            String authToken = ctx.header("Authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = service.listGames(listGamesRequest);
            String jsonResponse = gson.toJson(listGamesResult);
            ctx.result(jsonResponse);
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
