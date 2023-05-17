package com.advent2022.utils;

public class Coord {
  public int x;
  public int y;
  
  public Coord(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public boolean equals(Object obj) {
    if(!(obj instanceof Coord)) {
      return false;
    }
    return ((Coord)obj).x == this.x && ((Coord)obj).y == this.y;
  }

  public int hashCode(){
    String to_hash = ((Integer)this.x).toString() + "|" + ((Integer)this.y).toString();
    return to_hash.hashCode();
  }
}

