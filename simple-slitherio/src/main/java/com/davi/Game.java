package com.davi;
import java.util.ArrayList;

public class Game {
  public ArrayList<Player> players = new ArrayList<Player>();
  public ArrayList<Fruit> fruits = new ArrayList<Fruit>();

  public Game(){
  }
  
  public void newMovement(String moveData, String currentPlayerId){     
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

    for (int i = 0; i < fruits.size(); i++) {
      Fruit fruit = fruits.get(i);
      if(currentPlayer.body.get(0).x == fruit.x && currentPlayer.body.get(0).y == fruit.y) {
        fruits.remove(i);
        currentPlayer.points++;

        currentPlayer.body.add(new Coordinate(
          currentPlayer.body.get(currentPlayer.body.size() - 1).x, 
          currentPlayer.body.get(currentPlayer.body.size() - 1).y));

        if (players.size() > 6) fruits.add(new Fruit());
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