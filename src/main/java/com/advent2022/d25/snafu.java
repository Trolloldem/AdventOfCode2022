package com.advent2022.d25;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class snafu {

  static HashMap<Character, Long> converter = null;

  /**
   * Init the support map to quickly convert bettwen decimal and SNAFU
   **/
  static void init_converter() {
    converter = new HashMap<>();
    converter.put('0', 0l);
    converter.put('1', 1l);
    converter.put('2', 2l);
    converter.put('-', -1l);
    converter.put('=', -2l);
  }

  /**
   * Convert from decimal to SNAFU (base 5)
   **/
  static long from_snafu(String dec) {
    long power = (long)Math.pow(5, dec.length()-1);
    if(converter == null) {
      init_converter();
    }
    long n = 0;
    for(char c:dec.toCharArray()) {
      n += converter.get(c) * power;
      power = power / 5;
    }
    return n;
  }

  /**
   * Convert from SNAFU(base 5) to decimal. Treat 3 and 4 as -2 and -1
   **/
  static String to_snafu(long dec) {
    StringBuilder sb = new StringBuilder();
    while(dec != 0) {
      long n = dec % 5;
      if (n > 2) {
        long tmp = dec / 5;
        tmp += 1; 
        long insert = dec - tmp * 5;
        if(insert == -2) {
          sb.append('=');
        } else {
          sb.append('-');
        }
        dec = tmp;
      } else {
        long tmp = dec / 5;
        sb.append(dec - tmp * 5);
        dec = tmp;
      }
    }
    return sb.reverse().toString();
  }

  /**
   * Parse and convert using a numbering system with base 5. 
   * When converting back to SNAFU, pay attention 3 and 4 are treated as -2, -1
   **/
  static String sol(String filename) {

    long result = 0;
    try {
      InputStream iStream = snafu.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        result += from_snafu(line);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
    return to_snafu(result);
  }

  public static void execute(){
    String example = "inputs/d25/example.txt";
    System.out.println("(example) Solution: " + sol(example));

    String input = "inputs/d25/input.txt";
    System.out.println("Solution: " + sol(input));
  }

}
