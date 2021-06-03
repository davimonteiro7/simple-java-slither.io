package com.davi;

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
        game.addFruits(new Fruit());
        
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
                game.addPlayers(player);
                
                String gameJson = new Gson().toJson(game); 
                System.out.println(gameJson);    
                
                server.getClient(client.getSessionId()).sendEvent("bootstrap", gameJson);
                server.getBroadcastOperations().sendEvent("newGameState", gameJson);
            }
        });

        server.addEventListener("message", String.class, new DataListener<String>() {

            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                System.out.println("hey");
                
            }
        });

        server.start();
    }
}
