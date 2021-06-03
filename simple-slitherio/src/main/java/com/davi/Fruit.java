package com.davi;

public class Fruit {
  double x;
  double y;

  Fruit(){
    this.x = Math.floor(Math.random() * (1 - 29)) + 29;
    this.y = Math.floor(Math.random() * (1 - 29)) + 29;
   }
}