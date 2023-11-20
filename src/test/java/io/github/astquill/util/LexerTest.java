package io.github.astquill.util;

import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.Lexer.Offset;
import io.github.astquill.util.Lexer.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

class LexerTest {

  @Test
  void parseWhitespace() {
    Offset offset1 = new Lexer().parseWhitespace("   a", 0, 1, 1);
    Assertions.assertThat(offset1).isEqualTo(new Offset(3, 1, 4));

    Offset offset2 = new Lexer().parseWhitespace("a   a", 0, 1, 1);
    Assertions.assertThat(offset2).isEqualTo(new Offset(0, 1, 1));

    Offset offset3 = new Lexer().parseWhitespace("a   a", 1, 1, 2);
    Assertions.assertThat(offset3).isEqualTo(new Offset(4, 1, 5));

    Offset offset4 = new Lexer().parseWhitespace("   \na", 0, 1, 1);
    Assertions.assertThat(offset4).isEqualTo(new Offset(4, 2, 1));

    Offset offset5 = new Lexer().parseWhitespace("   \r\na", 0, 1, 1);
    Assertions.assertThat(offset5).isEqualTo(new Offset(5, 2, 1));

    Offset offset6 = new Lexer().parseWhitespace("   ", 0, 1, 1);
    Assertions.assertThat(offset6).isEqualTo(new Offset(3, 1, 4));
  }

  @Test
  void parseChar() {

  }

  @Test
  void tokenize() {
    String json = """
        {
            "key1": [true, false, null],
            "key2": {
                "key3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    LexerResult<List<Token>> lexerResult = new Lexer().tokenize(json, LexerOptions.builder().build());

    System.out.println(lexerResult);
  }
}