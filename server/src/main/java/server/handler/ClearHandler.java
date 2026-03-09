package server.handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.ClearService;

import static server.handler.Response.failure;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            failure(ctx, e);
        }
    }
}
