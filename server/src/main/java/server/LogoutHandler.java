package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.LogoutService;
import service.UnauthorizedException;

import java.util.Map;

public class LogoutHandler implements Handler {
    private final LogoutService service;
    Gson gson = new Gson();

    public LogoutHandler(LogoutService service) {
        this.service = service;
    }


    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            Gson gson = new Gson();
            String jsonBody = ctx.body();
            LogoutRequest logoutRequest = gson.fromJson(jsonBody, LogoutRequest.class);
            service.logout(logoutRequest);
            ctx.status(200);
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
