package com.davi;

public class Fruit {
  int x;
  int y;

  Fruit(){
    this.x = (int)(Math.random() * (1 - 29)) + 29;;
    this.y = (int)(Math.random() * (1 - 29)) + 29;;
   }
}