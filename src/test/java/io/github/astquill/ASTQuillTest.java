package io.github.astquill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.astquill.error.ParserError;
import io.github.astquill.model.JValue;
import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.Lexer;
import io.github.astquill.util.Lexer.Token;
import no.gorandalum.fluentresult.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ASTQuillTest {
  @Test
  void read() throws JsonProcessingException {
    String json = """
        {
            "key1": [true, false, null],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    ASTree result = ASTQuill.read(json);

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writeValueAsString(result));
  }

  @Test
  void tostring() throws JsonProcessingException {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    ASTree result = ASTQuill.read(json);
    System.out.println(ASTQuill.write(result));
  }
}