package io.github.astquill.util;

import io.github.astquill.error.UnexpectedCharacter;
import io.github.astquill.model.Loc;
import io.github.astquill.model.SourceLoc;
import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lexer {

  private final Map<Character, TokenType> punctuatorTokensMap = Map.of(
      '{', TokenType.LEFT_BRACE,
      '}', TokenType.RIGHT_BRACE,
      '[', TokenType.LEFT_BRACKET,
      ']', TokenType.RIGHT_BRACKET,
      ':', TokenType.COLON,
      ',', TokenType.COMMA
  );

  private final Map<String, TokenType> keywordTokensMap = Map.of(
      "true", TokenType.TRUE,
      "false", TokenType.FALSE,
      "null", TokenType.NULL
  );

  private final Set<Character> escapes = Set.of(
      '"',    // Quotation mask
      '\\',   // Reverse solidus
      '/',    // Solidus
      'b',    // Backspace
      'f',    // Form feed
      'n',    // New line
      'r',    // Carriage return
      't',    // Horizontal tab
      'u'     // 4 hexadecimal digits
  );

  protected Offset parseWhitespace(String input, int index, int line, int column) {
    boolean shouldExit = false;
    while (!shouldExit && index < input.length()) {
      char c = input.charAt(index);
      if (c == '\r') { // CR (Unix)
        index += 1;
        line += 1;
        column = 1;
        if (input.charAt(index) == '\n') { // CRLF (Windows)
          index += 1;
        }
      } else if (c == '\n') { // LF (MacOs)
        index += 1;
        line += 1;
        column = 1;
      } else if (c == '\t' || c == ' ') {
        index++;
        column++;
      } else {
        shouldExit = true;
      }
    }

    return new Offset(index, line, column);
  }

  protected Optional<Token> parseChar(String input, int index, int line, int column) {
    char c = input.charAt(index);

    if (punctuatorTokensMap.containsKey(c)) {
      Loc loc = new Loc();
      loc.setStart(new SourceLoc(line, column, index));
      loc.setEnd(new SourceLoc(line, column + 1, index + 1));
      return Optional.of(new Token(punctuatorTokensMap.get(c), String.valueOf(c), loc));
    } else {
      return Optional.empty();
    }
  }

  protected Optional<Token> parseKeyword(String input, int index, int line, int column) {
    for (Entry<String, TokenType> entry : keywordTokensMap.entrySet()) {
      String name = entry.getKey();
      TokenType type = entry.getValue();
      if ((index + name.length() <= input.length()) && (input
          .startsWith(name, index))) {
        Loc loc = new Loc();
        loc.setStart(new SourceLoc(line, column, index));
        loc.setEnd(new SourceLoc(line, column + name.length(), index + name.length()));
        return Optional.of(new Token(type, name, loc));
      }
    }

    return Optional.empty();
  }

  protected Optional<Token> parseString(String input, int index, int line, int column) {
    int startIndex = index;
    StringBuilder buffer = new StringBuilder();
    StringState state = StringState._START_;

    while (index < input.length()) {
      char c = input.charAt(index);

      switch (state) {
        case _START_ -> {
          if (c == '"') {
            index++;
            state = StringState.START_QUOTE_OR_CHAR;
          } else {
            return Optional.empty();
          }
        }
        case START_QUOTE_OR_CHAR -> {
          if (c == '\\') {
            buffer.append(c);
            index++;
            state = StringState.ESCAPE;
          } else if (c == '"') {
            index++;
            String raw = input.substring(startIndex, index);
            Loc loc = new Loc();
            loc.setStart(new SourceLoc(line, column, startIndex));
            loc.setEnd(new SourceLoc(line, column + raw.length(), index));
            return Optional.of(
                new Token(TokenType.STRING, raw, loc));
          } else {
            buffer.append(c);
            index++;
          }
        }
        case ESCAPE -> {
          if (escapes.contains(c)) {
            buffer.append(c);
            index++;
            if (c == 'u') {
              for (int i = 0; i < 4; i++) {
                if (index + i < input.length() && isHex(input.charAt(index))) {
                  buffer.append(input.charAt(index));
                  index++;
                } else {
                  return Optional.empty();
                }
              }
            }
            state = StringState.START_QUOTE_OR_CHAR;
          } else {
            return Optional.empty();
          }
        }
      }
      ;
    }

    return Optional.empty();
  }

  protected Optional<Token> parseNumber(String input, int index, int line, int column) {
    int startIndex = index;
    int passedValueIndex = index;
    NumberState state = NumberState._START_;

    iterator:
    while (index < input.length()) {
      char c = input.charAt(index);

      switch (state) {
        case _START_ -> {
          if (c == '-') {
            state = NumberState.MINUS;
          } else if (c == '0') {
            passedValueIndex = index + 1;
            state = NumberState.ZERO;
          } else if (isDigit1to9(c)) {
            passedValueIndex = index + 1;
            state = NumberState.DIGIT;
          } else {
            return Optional.empty();
          }
        }

        case MINUS -> {
          if (c == '0') {
            passedValueIndex = index + 1;
            state = NumberState.ZERO;
          } else if (isDigit1to9(c)) {
            passedValueIndex = index + 1;
            state = NumberState.DIGIT;
          } else {
            return Optional.empty();
          }
        }

        case ZERO -> {
          if (c == '.') {
            state = NumberState.POINT;
          } else if (isExp(c)) {
            state = NumberState.EXP;
          } else {
            break iterator;
          }
        }

        case DIGIT -> {
          if (isDigit(c)) {
            passedValueIndex = index + 1;
          } else if (c == '.') {
            state = NumberState.POINT;
          } else if (isExp(c)) {
            state = NumberState.EXP;
          } else {
            break iterator;
          }
        }

        case POINT -> {
          if (isDigit(c)) {
            passedValueIndex = index + 1;
            state = NumberState.DIGIT_FRACTION;
          } else {
            break iterator;
          }
        }

        case DIGIT_FRACTION -> {
          if (isDigit(c)) {
            passedValueIndex = index + 1;
          } else if (isExp(c)) {
            state = NumberState.EXP;
          } else {
            break iterator;
          }
        }

        case EXP -> {
          if (c == '+' || c == '-') {
            state = NumberState.EXP_DIGIT_OR_SIGN;
          } else if (isDigit(c)) {
            passedValueIndex = index + 1;
            state = NumberState.EXP_DIGIT_OR_SIGN;
          } else {
            break iterator;
          }
        }

        case EXP_DIGIT_OR_SIGN -> {
          if (isDigit(c)) {
            passedValueIndex = index + 1;
          } else {
            break iterator;
          }
        }
      }

      index++;
    }

    if (passedValueIndex > 0) {
      String raw = input.substring(startIndex, passedValueIndex);
      Loc loc = new Loc();
      loc.setStart(new SourceLoc(line, column, startIndex));
      loc.setEnd(new SourceLoc(line, column + raw.length(), index));

      return Optional.of(new Token(TokenType.NUMBER, raw, loc));
    }

    return Optional.empty();
  }

  // HELPERS

  public LexerResult<List<Token>> tokenize(String input, LexerOptions options) {
    int line = 1;
    int column = 1;
    int index = 0;
    ArrayList<Token> tokens = new ArrayList<>();

    while (index < input.length()) {

      // eat whitespace
      log.debug("Start Consuming whitespace index={}", index);
      Offset whitespace = parseWhitespace(input, index, line, column);
      line = whitespace.line;
      column = whitespace.column;
      index = whitespace.index;
      log.debug("End Consuming whitespace index={}", index);

      // EOF check
      if (index >= input.length()) {
        break;
      }

      // try parse character
      log.debug("Start Parsing character index={}", index);
      Optional<Token> charToken = parseChar(input, index, line, column);
      if (charToken.isPresent()) {
        Token token = charToken.get();
        tokens.add(token);
        column = token.loc.getEnd().getColumn();
        index = token.loc.getEnd().getOffset();
        continue;
      }

      // try parse keyword
      log.debug("Start Parsing keyword index={}", index);
      Optional<Token> keywordToken = parseKeyword(input, index, line, column);
      if (keywordToken.isPresent()) {
        Token token = keywordToken.get();
        tokens.add(token);
        column = token.loc.getEnd().getColumn();
        index = token.loc.getEnd().getOffset();
        continue;
      }

      // try parse string
      log.debug("Start Parsing string index={}", index);
      Optional<Token> stringToken = parseString(input, index, line, column);
      if (stringToken.isPresent()) {
        Token token = stringToken.get();
        tokens.add(token);
        column = token.loc.getEnd().getColumn();
        index = token.loc.getEnd().getOffset();
        continue;
      }

      // try parse number
      log.debug("Start Parsing number index={}", index);
      Optional<Token> numberToken = parseNumber(input, index, line, column);
      if (numberToken.isPresent()) {
        Token token = numberToken.get();
        tokens.add(token);
        column = token.loc.getEnd().getColumn();
        index = token.loc.getEnd().getOffset();
        continue;
      }

      // invalid character found
      log.error("Invalid Character found. index={}, inputLength={}, currChar={}, input={}, ", index,
          input.length(), input.charAt(index), input);
      return LexerResult.error(new UnexpectedCharacter("Found unexpected character"));
    }

    return LexerResult.ok(tokens);
  }

  boolean isDigit1to9(char c) {
    return c >= '1' && c <= '9';
  }

  boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  boolean isHex(char c) {
    return (
        isDigit(c)
            || (c >= 'a' && c <= 'f')
            || (c >= 'A' && c <= 'F')
    );
  }

  boolean isExp(char c) {
    return c == 'e' || c == 'E';
  }

  ;

  enum StringState {
    _START_,
    START_QUOTE_OR_CHAR,
    ESCAPE
  }

  enum NumberState {
    _START_,
    MINUS,
    ZERO,
    DIGIT,
    POINT,
    DIGIT_FRACTION,
    EXP,
    EXP_DIGIT_OR_SIGN
  }

  ;


  public enum TokenType {
    LEFT_BRACE,    // {
    RIGHT_BRACE,   // }
    LEFT_BRACKET,  // [
    RIGHT_BRACKET, // ]
    COLON,         // :
    COMMA,         // ,
    STRING,        //
    NUMBER,        //
    TRUE,          // true
    FALSE,         // false
    NULL           // null
  }

  public record Token(
      TokenType tokenType,
      String raw,
      Loc loc
  ) {

  }

  protected record Offset(
      int index,
      int line,
      int column
  ) {

  }

}
