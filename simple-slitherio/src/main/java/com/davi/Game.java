package com.davi;

import java.util.ArrayList;

public class Game {
  public ArrayList<Player> players = new ArrayList<Player>() ;
  public ArrayList<Fruit> fruits = new ArrayList<Fruit>();

  public Game(){
  }

  public void addPlayers(Player player){
    this.players.add(player);
  }
  public void addFruits(Fruit fruit){
    this.fruits.add(fruit);
  }
}