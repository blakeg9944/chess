package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.RegisterRequest;
import model.RegisterResult;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.RegisterService;
import java.util.Map;

public class RegisterHandler implements Handler {
    private final RegisterService service;

    public RegisterHandler(RegisterService service){
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String jsonBody = ctx.body();
            Gson gson = new Gson();
            RegisterRequest registerRequest = gson.fromJson(jsonBody ,RegisterRequest.class);
            RegisterResult registerResult = service.register(registerRequest);
            ctx.status(200);
            String jsonResponse = gson.toJson(registerResult);
            ctx.json(jsonResponse);
        }
        catch (BadRequestException badRequestException){
            ctx.status(400);
            ctx.result(Map.of("message", badRequestException.getMessage()).toString());
        }
        catch(AlreadyTakenException alreadyTakenException){
            ctx.status(403);
            ctx.result(Map.of("message", alreadyTakenException.getMessage()).toString());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(Map.of("message", e.getMessage()).toString());
        }

    }
}
