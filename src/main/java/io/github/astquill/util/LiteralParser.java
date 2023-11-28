package io.github.astquill.util;

import org.apache.commons.text.StringEscapeUtils;

public class LiteralParser {

  public static String parseString(String raw) {
    return StringEscapeUtils.unescapeEcmaScript(raw);
  }
}