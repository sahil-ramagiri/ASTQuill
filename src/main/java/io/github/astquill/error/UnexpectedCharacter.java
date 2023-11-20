package io.github.astquill.error;

public final class UnexpectedCharacter extends LexerError {

  private final String message;


  public UnexpectedCharacter(String message) {
    this.message = message;
  }
}
