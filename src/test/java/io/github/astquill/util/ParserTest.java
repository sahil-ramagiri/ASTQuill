package io.github.astquill.util;

import io.github.astquill.error.ParserError;
import io.github.astquill.model.JValue;
import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.Lexer.Token;
import no.gorandalum.fluentresult.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

  @Test
  void parse() {
    String json = """
        {
            "key1": [true, false, null],
            "key2": {
                "key3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    LexerResult<List<Token>> lexerResult = new Lexer().tokenize(json, LexerOptions.builder().build());

    System.out.println(lexerResult.getT());
    System.out.println("Tokens Size :" + lexerResult.getT().size());

    Parser parser = new Parser();
    Result<JValue, ParserError> result = parser.parse(lexerResult.getT());

    System.out.println(result.orElseThrow(Function.identity()));
  }
}