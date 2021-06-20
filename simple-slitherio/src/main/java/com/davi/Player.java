package com.davi;

import java.util.ArrayList;
import java.util.UUID;

public class Player extends Object {
  
  UUID ID;
  int points;
  String lastMovement;
  ArrayList<Coordinate> body = new ArrayList<Coordinate>();

  Player(UUID playerID){
    this.ID = playerID;
    this.points = 0;
    this.lastMovement = "";
    this.addBody();
  }

  public void addBody() {
    int x = (int)(Math.random() * (1 - 29)) + 29;
    int y = (int)(Math.random() * (1 - 29)) + 29;
    Coordinate coordinate = new Coordinate(x, y);
    this.body.add(coordinate);
  }
 
  @Override
  public boolean equals(Object obj) {
    Player player = (Player)obj;
    if (player.ID == this.ID) return true;
    else return false; 
  }
}