package com.advent2022;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.advent2022.d1.calories;
import com.advent2022.d2.rcs;
import com.advent2022.d3.sack;
import com.advent2022.d4.overlap;
import com.advent2022.d5.cargo;
import com.advent2022.d6.code;
import com.advent2022.d7.sh;
import com.advent2022.d8.visible;
import com.advent2022.d9.rope;
import com.advent2022.d10.cycle;
import com.advent2022.d11.monkey;
import com.advent2022.d12.hill;
import com.advent2022.d13.comp;
import com.advent2022.d14.sand;
import com.advent2022.d15.beacon;
import com.advent2022.d16.valves;
import com.advent2022.d17.rocks;
import com.advent2022.d18.cubes;
import com.advent2022.d19.blue;
import com.advent2022.d20.mix;
import com.advent2022.d21.yell;
import com.advent2022.d22.warp;
import com.advent2022.d23.plant;
import com.advent2022.d24.blizzard;
import com.advent2022.d25.snafu;

class Main {

  static HashMap<Integer, Method> get_methods() {
    String m_name = "execute";
    HashMap<Integer, Method> methods = new HashMap<>();
    try {
      methods.put(1, calories.class.getMethod(m_name));
      methods.put(2, rcs.class.getMethod(m_name));
      methods.put(3, sack.class.getMethod(m_name));
      methods.put(4, overlap.class.getMethod(m_name));
      methods.put(5, cargo.class.getMethod(m_name));
      methods.put(6, code.class.getMethod(m_name));
      methods.put(7, sh.class.getMethod(m_name));
      methods.put(8, visible.class.getMethod(m_name));
      methods.put(9, rope.class.getMethod(m_name));
      methods.put(10, cycle.class.getMethod(m_name));
      methods.put(11, monkey.class.getMethod(m_name));
      methods.put(12, hill.class.getMethod(m_name));
      methods.put(13, comp.class.getMethod(m_name));
      methods.put(14, sand.class.getMethod(m_name));
      methods.put(15, beacon.class.getMethod(m_name));
      methods.put(16, valves.class.getMethod(m_name));
      methods.put(17, rocks.class.getMethod(m_name));
      methods.put(18, cubes.class.getMethod(m_name));
      methods.put(19, blue.class.getMethod(m_name));
      methods.put(20, mix.class.getMethod(m_name));
      methods.put(21, yell.class.getMethod(m_name));
      methods.put(22, warp.class.getMethod(m_name));
      methods.put(23, plant.class.getMethod(m_name));
      methods.put(24, blizzard.class.getMethod(m_name));
      methods.put(25, snafu.class.getMethod(m_name));
    } catch (NoSuchMethodException e) {
      System.err.println(e.getMessage());
    }
    return methods;
  }

  public static void main(String[] args) {
    System.out.println("Select a day to solve (1-25): ");
    Scanner scanner = new Scanner(System.in);
    int day = 0;
    try {
      day = scanner.nextInt();
    } catch (InputMismatchException e) {
      System.err.println("Only numbers between 1 and 25 can be requested");
      scanner.close();
      return;
    }
    Method selected = get_methods().get(day);
    try {
      selected.invoke(null);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.getCause().printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    scanner.close();

  }
} 
