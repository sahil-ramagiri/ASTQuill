package io.github.astquill;

import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.Lexer;
import io.github.astquill.util.Lexer.Token;
import io.github.astquill.util.Parser;
import io.github.astquill.util.ToString;

import java.util.List;
import java.util.function.Function;

public class ASTQuill {

  public static ASTree read(String input) {
    Lexer lexer = new Lexer();
    LexerResult<List<Token>> lexerResult = lexer.tokenize(input,
        LexerOptions.builder().build());

    Parser parser = new Parser();
    var parserResult = parser.parse(lexerResult.getT());

    return new ASTree(parserResult.orElseThrow(Function.identity()));
  }

  public static String write(ASTree asTree) {
    return ToString.run(asTree.getValue());
  }

}