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


public class GameWebSocket {
 
  final Game game;
  Configuration config;
  final SocketIOServer server;
  
  public GameWebSocket(){
    this.game = new Game();
    this.config = getConfiguration();
    this.server = new SocketIOServer(config);
  }

  private Configuration getConfiguration(){
    Configuration config = new Configuration();
    config.setHostname("0.0.0.0");
    config.setOrigin("*");
    config.setPort(3000);

    config.setAuthorizationListener(new AuthorizationListener() {
      public boolean isAuthorized(HandshakeData handshakeData) {
        return true;
      }
    });

    return config;
  }

  public void initServer(){        
    
    server.addConnectListener(new ConnectListener(){
        @Override
        public void onConnect(SocketIOClient client) {
            System.out.println("CONNECTED ON.");
            String playerID = client.getSessionId().toString();
            Player player = PlayerFactory.createPlayer(playerID);
            
            game.addPlayer(player);
            String gameStateJson = new Gson().toJson(game); 
            
            server.getClient(client.getSessionId()).sendEvent("bootstrap", gameStateJson);
            server.getBroadcastOperations().sendEvent("newGameState", gameStateJson);

            setFruitRespawn(game);         
            setAutoPlayerMovement(playerID);
        }
    });

    server.addEventListener("playerMove", String.class, new DataListener<String>() {
      
      @Override
      public void onData(SocketIOClient client, String movement, AckRequest ackSender) throws Exception {                                
        String playerID = client.getSessionId().toString();  
        game.newMovement(movement, playerID, server); 
      }
    });

    server.addDisconnectListener(new DisconnectListener(){
        @Override
        public void onDisconnect(SocketIOClient client) {
          String playerID = client.getSessionId().toString();
          Player player = game.getPlayerById(playerID);
          game.removePlayer(player);
          server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(game));
        }            
    });

    server.start();    
  }

  private void setFruitRespawn(Game game){
    new Timer().schedule(new java.util.TimerTask(){
      @Override
      public void run() {
          game.addFruit(server);
      }
    }, 0, 6000);    
  }

  private void setAutoPlayerMovement(String playerID){
    new Timer().schedule(new java.util.TimerTask(){
      @Override
      public void run() {
          game.checkLastMovement(playerID, server);;
      }
    }, 0, 300);
  }
}
