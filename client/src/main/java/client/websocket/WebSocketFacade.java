package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
