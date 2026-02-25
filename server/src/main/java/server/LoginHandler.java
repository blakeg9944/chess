package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import org.jetbrains.annotations.NotNull;
import service.BadRequestException;
import service.LoginService;
import service.UnauthorizedException;

import java.util.Map;

public class LoginHandler implements Handler {
    private final LoginService service;

    public LoginHandler(LoginService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try{
            String jsonBody = ctx.body();
            Gson gson = new Gson();
            LoginRequest loginRequest = gson.fromJson(jsonBody, LoginRequest.class);
            LoginResult loginResult = service.login(loginRequest);
            ctx.status(200);
            String jsonResponse = gson.toJson(loginResult);
            ctx.result(jsonResponse);
        }
        catch(BadRequestException badRequestException){
            ctx.status(400);
            ctx.result(Map.of("message", badRequestException.getMessage()).toString());
        }
        catch(UnauthorizedException unauthorizedException){
            ctx.status(401);
            ctx.result(Map.of("message", unauthorizedException.getMessage()).toString());
        }
        catch (Exception e) {
            ctx.status(500);
            ctx.result(Map.of("message", e.getMessage()).toString());
        }
    }
}
