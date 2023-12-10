package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import no.gorandalum.fluentresult.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XpathLexer {

  public Result<List<XpathToken>, InvalidXpathException> tokenize(String input) {
    if (!input.matches(
        "^([\\s\\w\\d]+|\\[\\d+\\])(\\[\\d+\\])*(\\.[\\s\\w\\d]+(\\[\\d+\\])*)*$")) {
      return Result.error(new InvalidXpathException());
    }

    ArrayList<XpathToken> tokens = new ArrayList<>();
    String[] segments = input.split("\\.");

    for (String segment : segments) {

      String[] stringPart = segment.split("\\[\\d+]");
      if (stringPart.length == 1) {
        tokens.add(new XpathToken(XpathTokenType.STRING, stringPart[0]));
      }

      Pattern pattern = Pattern.compile("\\[(\\d+)]");
      Matcher matcher = pattern.matcher(segment);
      while (matcher.find()) {
        tokens.add(new XpathToken(XpathTokenType.INDEX, matcher.group(1)));
      }
    }

    return Result.success(tokens);
  }

  public enum XpathTokenType {
    STRING,      // key1
    INDEX,       // [2]
  }

  public record XpathToken(
      XpathTokenType tokenType,
      String raw
  ) {

    public String getText() {
      return raw;
    }

    public int getIndex() {
      return Integer.parseInt(raw);
    }
  }
}
