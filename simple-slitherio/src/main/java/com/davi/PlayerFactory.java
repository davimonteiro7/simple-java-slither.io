package com.davi;

public class PlayerFactory {
  
  public static Player createPlayer(String playerID){
    Player player = new Player(playerID);
    player.addBody();
    return player;
  }
}
