package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.RegisterRequest;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

public class RegisterHandler implements Handler {

    private HttpResponse<Object> ctx;

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String jsonBody = ctx.body().toString();

        var serializer = new Gson();

        RegisterRequest request = serializer.fromJson(jsonBody, RegisterRequest.class);

        RegisterResult result = registerService(request);

        ctx.status(200);
        ctx.result(serializer.toJson(result));
    }
}
