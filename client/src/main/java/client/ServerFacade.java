package client;

import com.google.gson.Gson;
import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public LoginResult login(LoginRequest request) throws Exception {
        // 1. Create URL and Connection
        URL url = new URI(serverUrl + "/session").toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        // 2. Set Method to "POST"
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        // 3. Write JSON request body
        try(var outputStream = http.getOutputStream()){
            var jsonBody = new Gson().toJson(request);
            outputStream.write(jsonBody.getBytes());
        }
        http.connect();
        // 4. Read JSON response body
        if (http.getResponseCode() == 200){
            try (InputStream responseBody = http.getInputStream()){
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                // 5. Return the Result object
                return new Gson().fromJson(inputStreamReader, LoginResult.class);
            }
        }
        else{
            throw new Exception("Error: " + http.getResponseCode());
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception{
        URL url = new URI(serverUrl + "/user").toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        // 2. Set Method to "POST"
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        try(var outputStream = http.getOutputStream()){
            var jsonBody = new Gson().toJson(registerRequest);
            outputStream.write(jsonBody.getBytes());
        }
        http.connect();
        if (http.getResponseCode() == 200){
            try (InputStream responseBody = http.getInputStream()){
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                // 5. Return the Result object
                return new Gson().fromJson(inputStreamReader, RegisterResult.class);
            }
        }
        else{
            throw new Exception("Error: " + http.getResponseCode());
        }
    }
}
