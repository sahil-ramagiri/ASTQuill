package io.github.astquill.error;

public class ParserError extends RuntimeException {

  public ParserError() {
  }

  public ParserError(String message) {
    super(message);
  }

  public ParserError(String message, Throwable cause) {
    super(message, cause);
  }
}
