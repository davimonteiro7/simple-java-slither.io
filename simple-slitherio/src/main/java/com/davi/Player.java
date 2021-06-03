package com.davi;

import java.util.ArrayList;

public class Player {
  
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
    double x = Math.floor(Math.random() * (1 - 29)) + 29;
    double y = Math.floor(Math.random() * (1 - 29)) + 29;
    Coordinate coordinate = new Coordinate(x, y);
    body.add(coordinate);
  }

}