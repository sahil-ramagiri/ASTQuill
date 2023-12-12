package io.github.astquill.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LiteralParserTest {

  @Test
  void parseString() {
    String input = "\"abc bcd\"";
    String output = LiteralParser.parseString(input);

    System.out.println("input: " + input);
    System.out.println("output: " + output);
    Assertions.assertEquals("abc bcd", output);
  }

  @Test
  void unparseString() {
    String input = "abc bcd";
    String output = LiteralParser.unparseString(input);

    System.out.println("input: " + input);
    System.out.println("output: " + output);
    Assertions.assertEquals("\"abc bcd\"", output);
  }
}