package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private NotificationHandler notificationHandler;

    public WebSocketFacade (String url, NotificationHandler notificationHandler) throws Exception{
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        case ERROR -> serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                        case NOTIFICATION -> serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                    }
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (URISyntaxException | IllegalStateException | DeploymentException | IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void sendCommand(UserGameCommand userGameCommand) throws Exception{
        try{
            String jsonCommand = new Gson().toJson(userGameCommand);
            this.session.getBasicRemote().sendText(jsonCommand);
        } catch (IOException e) {
            throw new IOException("Error sending message to server: " + e.getMessage());
        }
    }

    public void connectWebSocket(String authToken, int gameID) throws Exception{
        try{
            ConnectCommand connectCommand = new ConnectCommand(authToken, gameID);
            sendCommand(connectCommand);
        } catch (Exception e) {
            throw new Exception("Error: could not connect to server");
        }

    }

    public void leaveGameWebSocket(String authToken, int gameID) throws Exception{
        try{
            LeaveCommand leaveCommand = new LeaveCommand(authToken, gameID);
            sendCommand(leaveCommand);
        } catch (Exception e) {
            throw new Exception("Error: could not connect to server");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
