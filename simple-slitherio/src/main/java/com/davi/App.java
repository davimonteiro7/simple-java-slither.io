package com.davi;

import java.lang.reflect.Type;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.google.gson.Gson;

public class App 
{
    public static void main( String[] args )
    {   
        final Game game = new Game();
        
        game.fruits.add(new Fruit());
        
        Configuration config = new Configuration();
        config.setPort(3000);

        config.setAuthorizationListener(new AuthorizationListener() {
            public boolean isAuthorized(HandshakeData handshakeData) {
                return true;
            }
        });

        final SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(new ConnectListener(){
            @Override
            public void onConnect(SocketIOClient client) {
                String playerID = client.getSessionId().toString();
                
                Player player = new Player(playerID);
                player.addBody();
                game.players.add(player);;
                
                String gameJson = new Gson().toJson(game); 
                
                server.getClient(client.getSessionId()).sendEvent("bootstrap", gameJson);
                server.getBroadcastOperations().sendEvent("newGameState", gameJson);
            }
        });

        server.addEventListener("playerMove", String.class, new DataListener<String>() {

            @Override
            public void onData(SocketIOClient client, String currentPlayerJson, AckRequest ackSender) throws Exception {
                
                Player currentPlayer = new Gson().fromJson(currentPlayerJson, (Player.class));
                System.out.println(currentPlayer);
                game.newMovement(currentPlayer);
                String gameJson = new Gson().toJson(game); 

                server.getClient(client.getSessionId()).sendEvent("newGameState", gameJson);
                server.getBroadcastOperations().sendEvent("newGameState", gameJson);
            }
        });

        server.start();
    }
}
