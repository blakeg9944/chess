package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;

import java.util.Map;

public class Response {
    static Gson gson = new Gson();

    public static void success(Context ctx, Object response){
        String jsonResponse = gson.toJson(response);
        ctx.result(jsonResponse);
    }

    public static void failure(Context ctx, Exception e) {
        if (e instanceof BadRequestException) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
        else if (e instanceof UnauthorizedException) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
        else if (e instanceof AlreadyTakenException) {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
        else {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
