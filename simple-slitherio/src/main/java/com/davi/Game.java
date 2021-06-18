package com.davi;
import java.util.ArrayList;
import java.util.UUID;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;

public class Game {
  private ArrayList<Player> players = new ArrayList<Player>();
  public ArrayList<Fruit> fruits = new ArrayList<Fruit>();

  public Game(){
  }
  
  public void addPlayer(Player player){
    this.players.add(player);
  }

  public void removePlayer(Player player){
    this.players.remove(player);
  }
  public Player getPlayerById(String PlayerID){
    Player player = players.get(getIndexByID(PlayerID));
    return player;
  }
  public int getIndexByID(String id) {
    int index = -1;
    for (int i = 0; i < players.size(); i++){
      if (players.get(i).id.equals(id)){
        index = i;
        break;
      }
    }
    return index;
  }
  public void addFruit(SocketIOServer server){
    if (fruits.size() < 7){
      fruits.add(new Fruit());

      for(int i = 0; i < fruits.size(); i++){
        Fruit fruit = fruits.get(i);
        for(int j = 0; j < players.size(); j++){
          Player player = players.get(j);
          if(fruit.y == player.body.get(0).y && fruit.x == player.body.get(0).x){
            fruits.remove(i);
            addFruit(server);
          }
        }
        server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this));  
      }
    }
  }
  public void checkLastMovement(String currentPlayerId, SocketIOServer server){
    if(!players.get(getIndexByID(currentPlayerId)).lastMovement.equals("")){
      newMovement(players.get(getIndexByID(currentPlayerId)).lastMovement, currentPlayerId, server);
    }
  }
  public void newMovement(String movement, String currentPlayerId, SocketIOServer server){     
    Player currentPlayer = players.get(getIndexByID(currentPlayerId));
    
    switch (movement) {
      case "ArrowUp":
        currentPlayer.lastMovement = "ArrowUp";
        currentPlayer.body.get(0).y--;
        if (currentPlayer.body.get(0).y == -1){
          currentPlayer.body.get(0).y += 30;
        }
        break;
      
      case "ArrowDown":
        currentPlayer.lastMovement = "ArrowDown";
        currentPlayer.body.get(0).y++;
        if (currentPlayer.body.get(0).y == 30){
          currentPlayer.body.get(0).y -= 30;
        }
        break;
      
      case "ArrowLeft":
        currentPlayer.lastMovement = "ArrowLeft";
        currentPlayer.body.get(0).x--;
        if (currentPlayer.body.get(0).x == -1){
          currentPlayer.body.get(0).x += 30;
        }
        break;
      
      case "ArrowRight":
        currentPlayer.lastMovement = "ArrowRight";
        currentPlayer.body.get(0).x++;
        if (currentPlayer.body.get(0).x == 30){
          currentPlayer.body.get(0).x -= 30;
        }
        break;
      
      default:
        return;
    }

    for(int i = currentPlayer.body.size() - 1; i > 0; i--){
      currentPlayer.body.get(i).x = currentPlayer.body.get(i - 1).x;
      currentPlayer.body.get(i).y = currentPlayer.body.get(i - 1).y;
    }

    server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this));

    handleFruitRespawn(currentPlayer, server);
    handleColision(currentPlayer, currentPlayerId, server);
  }
  private void handleFruitRespawn(Player currentPlayer, SocketIOServer server){
    for (int i = 0; i < fruits.size(); i++) {
      Fruit fruit = fruits.get(i);
      if(currentPlayer.body.get(0).x == fruit.x && currentPlayer.body.get(0).y == fruit.y) {
        fruits.remove(i);
        currentPlayer.points++;

        currentPlayer.body.add(new Coordinate(
          currentPlayer.body.get(currentPlayer.body.size() - 1).x, 
          currentPlayer.body.get(currentPlayer.body.size() - 1).y));

        if (players.size() > 6){
          this.addFruit(server);
        }
        
        server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this)); 
      }
    }
  }
  private void handleColision(Player currentPlayer, String currentPlayerId, SocketIOServer server){
    
    for (int i = 0; i < players.size(); i++){
      Player player = players.get(i);
      if (!player.id.equals(currentPlayerId)){
        for(int j = 0; j < player.body.size(); j++){
          if (players.get(getIndexByID(currentPlayerId)).body.get(0).x == player.body.get(j).x 
              && players.get(getIndexByID(currentPlayerId)).body.get(0).y == player.body.get(j).y) {
            
            player.points += players.get(getIndexByID(currentPlayerId)).points;
            
            for(int k = 1; k < currentPlayer.body.size() - 2; k++){
              System.out.println(currentPlayer.body.size());
              player.body.add(players.get(getIndexByID(currentPlayerId)).body.get(k));
            }
            
            players.get(getIndexByID(currentPlayerId)).points = 0;
            
            Coordinate pieceOfBody = currentPlayer.body.get(0);
            currentPlayer.body.clear();
            currentPlayer.body.add(pieceOfBody);
            
            server.getClient(UUID.fromString(currentPlayerId)).sendEvent("newGameState", new Gson().toJson(this));;
            server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this));
            break;
          }
        }
      }
    }
  }

}