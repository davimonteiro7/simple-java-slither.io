package com.davi;

import java.util.Timer;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.Gson;

public class App 
{
    public static void main( String[] args )
    {   
        
        final Game game = new Game();
        
        Configuration config = new Configuration();
        config.setHostname("0.0.0.0");
        config.setOrigin("Access-Control-Allow-Origin");
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
                game.players.add(player);
                
                int index = game.getIndexByID(playerID);
                game.players.get(index).addBody();
                
                
                String gameJson = new Gson().toJson(game); 
                
                server.getClient(client.getSessionId()).sendEvent("bootstrap", gameJson);
                server.getBroadcastOperations().sendEvent("newGameState", gameJson);

                new Timer().schedule(new java.util.TimerTask(){
                    @Override
                    public void run() {
                        game.addFruit(server);
                    }
                }, 0, 6000);
                
                new Timer().schedule(new java.util.TimerTask(){
                    @Override
                    public void run() {
                        game.checkLastMovement(playerID, server);;
                    }
                }, 0, 300);
            }
        });

        server.addEventListener("playerMove", String.class, new DataListener<String>() {

            @Override
            public void onData(SocketIOClient client, String moveData, AckRequest ackSender) throws Exception {                                
                game.newMovement(moveData, client.getSessionId().toString(), server); 
                
            }
        });

        server.addDisconnectListener(new DisconnectListener(){
            @Override
            public void onDisconnect(SocketIOClient client) {
              game.players.remove(game.players.get(game.getIndexByID(client.getSessionId().toString())));
              server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(game));
            }            
        });

        server.start();
    }
}
