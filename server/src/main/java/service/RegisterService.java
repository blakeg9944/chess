package service;

import io.javalin.http.BadRequestResponse;
import model.RegisterRequest;
import model.RegisterResult;

public class RegisterService{

    public RegisterResult register(RegisterRequest registerRequest) throws Exception{
        validateRequest(registerRequest);
        return new RegisterResult(RegisterResult.username())
    }

    private void validateRequest(RegisterRequest registerRequest){
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new BadRequestResponse("Error: bad request");
    }
}
