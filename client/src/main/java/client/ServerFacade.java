package client;

import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var http = createURLandConnection("/session", "POST", null);
        // 3. Write JSON request body
        writeBody(request, http);
        http.connect();
        // 4. Read JSON response body
        if (http.getResponseCode() == 200){
            return readBody(http, LoginResult.class);
        }
        else{
            var error = readBody(http, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Error: " + http.getResponseCode());
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception{
        var http = createURLandConnection("/user", "POST", null);
        writeBody(registerRequest, http);
        http.connect();
        if (http.getResponseCode() == 200){
            return readBody(http, RegisterResult.class);
        }
        else{
            var error = readBody(http, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Error: " + http.getResponseCode());
        }
    }

    public void logout(LogoutRequest logoutRequest) throws Exception{
        var httpURLConnection = createURLandConnection("/session", "DELETE", logoutRequest.authToken());
        httpURLConnection.connect();
        if(httpURLConnection.getResponseCode() != 200) {
            var error = readBody(httpURLConnection, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Logout failed");
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws Exception{
        var http = createURLandConnection("/game", "GET", listGamesRequest.authToken());
        http.connect();
        if (http.getResponseCode() == 200){
            return readBody(http, ListGamesResult.class);
        }
        else{
            var error = readBody(http, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Error: " + http.getResponseCode());
        }
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws Exception{
        HttpURLConnection http = createURLandConnection("/game", "POST", authToken);
        writeBody(request, http);
        http.connect();
        if(http.getResponseCode() == 200){
            return readBody(http, CreateGameResult.class);
        }
        else{
            var error = readBody(http, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Error: " + http.getResponseCode());
        }
    }

    public void joinGame(JoinGameRequest request, String authToken) throws Exception{
        HttpURLConnection http = createURLandConnection("/game", "PUT", authToken);
        writeBody(request, http);
        http.connect();
        if(http.getResponseCode() == 200){
            readBody(http, JoinGameResult.class);
        }
        else{
            var error = readBody(http, ErrorResponse.class);
            throw new Exception(error != null ? error.message() : "Error: " + http.getResponseCode());
        }
    }

    private HttpURLConnection createURLandConnection(String path, String method, String authToken) throws Exception{
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        if (authToken != null && !authToken.isEmpty()){
            http.setRequestProperty("authorization", authToken);
        }
        if (method.equals("POST") || method.equals("PUT")) {
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
        }

        return http;

    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                var gson = new com.google.gson.GsonBuilder().serializeNulls().create();
                var jsonBody = gson.toJson(request);
                outputStream.write(jsonBody.getBytes());
            }
        }
    }
    private <T> T readBody(HttpURLConnection http, Class<T> tClass) throws Exception {
        InputStream is = (http.getResponseCode() == 200) ? http.getInputStream() : http.getErrorStream();
        if (is == null){
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(is)) {
            return new Gson().fromJson(reader, tClass);
        } catch (Exception e) {
            throw new Exception("Failed to parse server response: " + e.getMessage());
        }
    }


    public void clear() throws Exception {
        var http = createURLandConnection("/db", "DELETE", null);
        http.connect();
        if (http.getResponseCode() != 200) {
            throw new Exception("Clear failed");
        }
    }
}
