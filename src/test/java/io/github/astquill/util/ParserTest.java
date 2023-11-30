package io.github.astquill.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  void parse() throws JsonProcessingException {
    String json = """
        {
            "key1": [true, false, null],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    LexerResult<List<Token>> lexerResult = new Lexer().tokenize(json, LexerOptions.builder().build());

    System.out.println(lexerResult.getT());
    System.out.println("Tokens Size :" + lexerResult.getT().size());

    Parser parser = new Parser();
    Result<JValue, ParserError> result = parser.parse(lexerResult.getT());

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writeValueAsString(result.orElseThrow(Function.identity())));
    System.out.println(json);
  }
}