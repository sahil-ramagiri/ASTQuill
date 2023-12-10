package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import io.github.astquill.util.XpathLexer.XpathToken;
import no.gorandalum.fluentresult.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.function.Function;

class XpathLexerTest {

  @ParameterizedTest
  @ValueSource(strings = {"key1", "key1.key2", "key1[0]", "[1]", "key1[1].key2", "key1[0][12]",
      "key1[0][12].key2[1][13]"})
  void tokenize(String input) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(input);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());
    System.out.println(xpathTokens);
  }

  @ParameterizedTest
  @ValueSource(strings = {".", ".key2", "[0]key1[0]", "[1]key1"})
  void tokenize_throwsError(String input) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(input);
    Assertions.assertThrows(InvalidXpathException.class,
        () -> result.orElseThrow(Function.identity()));
  }
}