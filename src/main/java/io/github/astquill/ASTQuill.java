package io.github.astquill;

import io.github.astquill.model.JArray;
import io.github.astquill.model.JLiteral;
import io.github.astquill.model.JObject;
import io.github.astquill.result.LexerResult;
import io.github.astquill.result.ParserResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.Lexer;
import io.github.astquill.util.Lexer.Token;
import io.github.astquill.util.Lexer.TokenType;
import io.github.astquill.util.Parser;

import java.util.List;

public class ASTQuill {

  public static ASTree read(String input) {
    Lexer lexer = new Lexer();
    LexerResult<List<Token>> lexerResult = lexer.tokenize("123",
        LexerOptions.builder().build());

    Parser parser = new Parser();
    ParserResult parserResult = parser.parse(lexerResult.getT());

    return new ASTree(parserResult.getJNode());
  }

  public static String write(ASTree asTree) {
    return asTree.value.toString();
  }

  public static ASTree newObject() {
    return new ASTree(new JObject());
  }

  public static ASTree newArray() {
    return new ASTree(new JArray());
  }

  public static ASTree newLiteral() {
    return new ASTree(new JLiteral());
  }
}
