package io.github.astquill.util;

import org.apache.commons.text.StringEscapeUtils;

public class LiteralParser {

  public static String parseString(String raw) {
    String trimmed = raw.substring(1, raw.length() - 1);
    return StringEscapeUtils.unescapeEcmaScript(trimmed);
  }
}