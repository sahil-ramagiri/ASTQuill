package io.github.astquill.result;

import io.github.astquill.error.LexerError;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LexerResult<T> {

  T t;

  LexerError lexerError;

  public static <G> LexerResult<G> ok(G tokenStream) {
    LexerResult<G> lexerResult = new LexerResult<G>();
    lexerResult.t = tokenStream;

    return lexerResult;
  }

  public static <G> LexerResult<G> error(LexerError error) {
    LexerResult<G> lexerResult = new LexerResult<G>();
    lexerResult.lexerError = error;

    return lexerResult;
  }

  public boolean isOk() {
    return lexerError == null;
  }

}
