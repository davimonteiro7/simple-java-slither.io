package com.davi;
import java.util.ArrayList;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;

public class Game {
  public ArrayList<Player> players = new ArrayList<Player>();
  public ArrayList<Fruit> fruits = new ArrayList<Fruit>();

  public Game(){
  }
  
  public void addFruit(SocketIOServer server){
    fruits.add(new Fruit());
    for (int i = 0; i < fruits.size(); i++){
      if (fruits.size() < 7){
        Fruit fruit = fruits.get(i);
        for(int j = 0; j < players.size(); j++){
          Player player = players.get(j);
          if(fruit.y == player.body.get(j).y && fruit.x == player.body.get(j).x){
            fruits.remove(i);
            addFruit(server);
          }
        }
        server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this));
      }
    }
  }

  public void newMovement(String moveData, String currentPlayerId, SocketIOServer server){     
    Player currentPlayer = players.get(getIndexByID(currentPlayerId));
    
    switch (moveData) {
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

    for (int i = 0; i < players.size(); i++){
      Player player = players.get(i);
      if (!player.id.equals(currentPlayer.id)){
        for(int j = 0; j < player.body.size(); j++){
          if (currentPlayer.body.get(0).x == player.body.get(j).x && currentPlayer.body.get(0).y == player.body.get(j).y) {
            player.points += currentPlayer.points;
            for(int k = 1; k < currentPlayer.body.size() - 2; k++){
              player.body.add(currentPlayer.body.get(k));
            }
            currentPlayer.points = 0;
            currentPlayer.body.remove(currentPlayer.body.size() - 1);
            server.getBroadcastOperations().sendEvent("newGameState", new Gson().toJson(this));
          }
        }
      }
    }


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
}