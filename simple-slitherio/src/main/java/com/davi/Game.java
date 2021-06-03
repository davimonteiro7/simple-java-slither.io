package com.davi;
import java.util.ArrayList;

public class Game {
  public ArrayList<Player> players = new ArrayList<Player>();
  public ArrayList<Fruit> fruits = new ArrayList<Fruit>();

  public Game(){
  }
  
  public void newMovement(Player currentPlayer){     
    System.out.println(currentPlayer);
    int index = players.indexOf(currentPlayer);
    System.out.println(index);
  }
}