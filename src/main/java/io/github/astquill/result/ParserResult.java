package io.github.astquill.result;

import io.github.astquill.error.ParserError;
import io.github.astquill.model.JNode;
import lombok.Getter;

@Getter
public class ParserResult {

  JNode jNode;

  ParserError parserError;

  public static ParserResult from(JNode jNode) {
    ParserResult parserResult = new ParserResult();
    parserResult.jNode = jNode;

    return parserResult;
  }

  public static ParserResult from(ParserError error) {
    ParserResult parserResult = new ParserResult();
    parserResult.parserError = error;

    return parserResult;
  }
}
