package com.davi;

import java.util.Timer;
import java.util.UUID;

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
        UUID playerID = client.getSessionId();
        Player player = new Player(playerID);
        
        game.addPlayer(player);
        initGameState(game, client);        
        setFruitRespawn(game);         
        setAutoPlayerMovement(playerID);
      }
    });

    server.addEventListener("playerMove", String.class, new DataListener<String>() {
      
      @Override
      public void onData(SocketIOClient client, String movement, AckRequest ackSender) throws Exception {                                
        UUID playerID = client.getSessionId();  
        game.newMovement(movement, playerID, server); 
      }
    });

    server.addDisconnectListener(new DisconnectListener(){
        @Override
        public void onDisconnect(SocketIOClient client) {
          UUID playerID = client.getSessionId();
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

  private void setAutoPlayerMovement(UUID playerID){
    new Timer().schedule(new java.util.TimerTask(){
      @Override
      public void run() {
          game.checkLastMovement(playerID, server);;
      }
    }, 0, 300);
  }

  public void initGameState(Game game, SocketIOClient client){
    String gameStateJson = new Gson().toJson(game); 
    
    server.getClient(client.getSessionId()).sendEvent("bootstrap", gameStateJson);
    server.getBroadcastOperations().sendEvent("newGameState", gameStateJson);
  }
}
