package io.github.astquill.util;

import io.github.astquill.model.JLiteral;
import io.github.astquill.result.ParserResult;
import io.github.astquill.util.Lexer.Token;
import java.util.List;

public class Parser {

  public ParserResult parse(List<Token> tokenStream) {
    return ParserResult.from(new JLiteral());
  }
}
