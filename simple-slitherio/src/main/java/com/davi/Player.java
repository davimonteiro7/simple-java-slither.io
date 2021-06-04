package com.davi;

import java.util.ArrayList;

public class Player extends Object {
  
  String id;
  int points;
  String lastMovement;
  ArrayList<Coordinate> body = new ArrayList<Coordinate>();

  Player(String PlayerID){
    this.id = PlayerID;
    this.points = 0;
    this.lastMovement = "";
  }

  public void addBody() {
    int x = (int)(Math.random() * (1 - 29)) + 29;
    int y = (int)(Math.random() * (1 - 29)) + 29;
    Coordinate coordinate = new Coordinate(x, y);
    body.add(coordinate);
  }
 
  @Override
  public boolean equals(Object obj) {
    Player p = (Player)obj;
    if (p.id == this.id) return true;
    else return false; 
  }
}