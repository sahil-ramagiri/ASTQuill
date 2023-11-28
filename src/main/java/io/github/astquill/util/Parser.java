package io.github.astquill.util;

import io.github.astquill.error.ParserError;
import io.github.astquill.model.*;
import io.github.astquill.util.Lexer.Token;
import io.github.astquill.util.Lexer.TokenType;
import lombok.extern.slf4j.Slf4j;
import no.gorandalum.fluentresult.OptionalResult;
import no.gorandalum.fluentresult.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class Parser {

  List<Character> passEscapes = List.of('"', '\\', '/');
  private Map<Character, Character> escapes = Map.of(
      'b', '\b',  // Backspace
      'f', '\f',  // Form feed
      'n', '\n',  // New line
      'r', '\r',  // Carriage return
      't', '\t'  // Horizontal tab
  );

  protected ParserError errorEof(List<Token> tokens) {
    return new ParserError("Unexpected EOF");
  }

  protected Result<Offset, ParserError> parseProperty(List<Token> tokens, int index) {
    // property: STRING COLON value

    log.debug("Start parsing property at index:{}", index);

    Token startToken = null;
    PropertyState state = PropertyState._START_;

    JIdentifier key = null;
    JValue value;

    while (index < tokens.size()) {
      Token token =  tokens.get(index);

      switch (state) {
        case _START_ -> {
          log.debug("Parsing property at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.STRING) {
            key = new JIdentifier(LiteralParser.parseString(token.raw()), token.raw(), token.loc());
            startToken = token;
            state = PropertyState.KEY;
            index++;
          }
        }
        case KEY -> {
          log.debug("Parsing property at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.COLON) {
            state = PropertyState.COLON;
            index++;
          } else {
            return Result.error(new ParserError("Unexpected token"));
          }
        }
        case COLON -> {
          log.debug("Parsing property at Index: {}, currentState: {}", index, state);
          Result<Offset, ParserError> offsetResult = parseValue(tokens, index);
          Offset offset = offsetResult.orElseThrow(Function.identity());
          value = (JValue) offset.value();
          Loc loc = new Loc();
          loc.setStart(startToken.loc().getStart());
          loc.setEnd(token.loc().getEnd());
          JProperty jProperty = new JProperty(key, value, loc);
          return Result.success(new Offset(jProperty, offset.index));
        }
      }
    }
    return Result.error(errorEof(tokens));
  }

  protected Optional<Offset> parseLiteral(List<Token> tokens, int index) {
    // literal: STRING | NUMBER | TRUE | FALSE | NULL

    log.debug("Start parsing literal at index:{}", index);

    Token token = tokens.get(index);
    Optional<JLiteral> value = switch (token.tokenType()) {
      case STRING -> Optional.of(new JLiteral(JPrimitive.STRING, token.raw(), token.loc()));
      case NUMBER -> Optional.of(new JLiteral(JPrimitive.NUMBER, token.raw(), token.loc()));
      case TRUE, FALSE -> Optional.of(new JLiteral(JPrimitive.BOOL, token.raw(), token.loc()));
      case NULL -> Optional.of(new JLiteral(JPrimitive.NULL, token.raw(), token.loc()));
      default -> Optional.empty();
    };

    return value.map(jLiteral -> new Offset(jLiteral, index + 1));
  }

  protected OptionalResult<Offset, ParserError> parseObject(List<Token> tokens, int index) {
    // object: LEFT_BRACE (property (COMMA property)*)? RIGHT_BRACE

    log.debug("Start parsing object at index:{}", index);


    List<JProperty> children = new ArrayList<>();
    ObjectState state = ObjectState._START_;
    Token startToken = null;

    JObject object;

    while (index < tokens.size()) {
      Token token = tokens.get(index);

      switch (state) {
        case _START_ -> {
          log.debug("Parsing object at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.LEFT_BRACE) {
            startToken = token;
            state = ObjectState.OPEN_OBJECT;
            index++;
          } else {
            return OptionalResult.empty();
          }
        }
        case OPEN_OBJECT -> {
          log.debug("Parsing object at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.RIGHT_BRACE) {
            Loc loc = new Loc();
            loc.setStart(startToken.loc().getStart());
            loc.setEnd(token.loc().getEnd());
            object = new JObject(children, loc);

            return OptionalResult.success(new Offset(object, index + 1));
          } else {
            Result<Offset, ParserError> offsetResult = parseProperty(
                tokens, index);
            try {
              Offset offset = offsetResult.orElseThrow(Function.identity());
              index = offset.index;
              children.add((JProperty) offset.value);
              state = ObjectState.PROPERTY;
            } catch (ParserError e) {
              return OptionalResult.error(e);
            }
          }
        }
        case PROPERTY -> {
          log.debug("Parsing object at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.RIGHT_BRACE) {
            Loc loc = new Loc();
            loc.setStart(startToken.loc().getStart());
            loc.setEnd(token.loc().getEnd());
            object = new JObject(children, loc);

            return OptionalResult.success(new Offset(object, index + 1));
          } else if (token.tokenType() == TokenType.COMMA) {
            state = ObjectState.COMMA;
            index++;
          } else {
            return OptionalResult.error(new ParserError());
          }
        }
        case COMMA -> {
          log.debug("Parsing object at Index: {}, currentState: {}", index, state);
          Result<Offset, ParserError> offsetResult = parseProperty(
              tokens, index);
          try {
            Offset offset = offsetResult.orElseThrow(Function.identity());
            index = offset.index;
            children.add((JProperty) offset.value);
            state = ObjectState.PROPERTY;
          } catch (ParserError e) {
            return OptionalResult.error(e);
          }
        }
      }
    }

    return OptionalResult.error(errorEof(tokens));
  }

  protected OptionalResult<Offset, ParserError> parseArray(List<Token> tokens, int index) {
    // array: LEFT_BRACKET (value (COMMA value)*)? RIGHT_BRACKET

    log.debug("Start parsing array at index:{}", index);


    List<JValue> children = new ArrayList<>();
    ArrayState state = ArrayState._START_;
    Token startToken = null;

    JArray array;

    while (index < tokens.size()) {
      Token token = tokens.get(index);

      switch (state) {
        case _START_ -> {
          log.debug("Parsing array at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.LEFT_BRACKET) {
            startToken = token;
            state = ArrayState.OPEN_ARRAY;
            index++;
          } else {
            return OptionalResult.empty();
          }
        }
        case OPEN_ARRAY -> {
          log.debug("Parsing array at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.RIGHT_BRACKET) {
            Loc loc = new Loc();
            loc.setStart(startToken.loc().getStart());
            loc.setEnd(token.loc().getEnd());
            array = new JArray(children, loc);

            return OptionalResult.success(new Offset(array, index + 1));
          } else {
            Result<Offset, ParserError> offsetResult = parseValue(tokens, index);
            try {
              Offset offset = offsetResult.orElseThrow(Function.identity());
              index = offset.index;
              children.add((JValue) offset.value);
              state = ArrayState.VALUE;
            } catch (ParserError e) {
              return OptionalResult.error(e);
            }
          }
        }
        case VALUE -> {
          log.debug("Parsing array at Index: {}, currentState: {}", index, state);
          if (token.tokenType() == TokenType.RIGHT_BRACKET) {
            Loc loc = new Loc();
            loc.setStart(startToken.loc().getStart());
            loc.setEnd(token.loc().getEnd());
            array = new JArray(children, loc);

            return OptionalResult.success(new Offset(array, index + 1));
          } else if (token.tokenType() == TokenType.COMMA) {
            state = ArrayState.COMMA;
            index++;
          } else {
            return OptionalResult.error(new ParserError());
          }
        }
        case COMMA -> {
          log.debug("Parsing array at Index: {}, currentState: {}", index, state);
          Result<Offset, ParserError> offsetResult = parseValue(tokens, index);
          try {
            Offset offset = offsetResult.orElseThrow(Function.identity());
            index = offset.index;
            children.add((JValue) offset.value);
            state = ArrayState.VALUE;
          } catch (ParserError e) {
            return OptionalResult.error(e);
          }
        }
      }
    }

    return OptionalResult.error(errorEof(tokens));
  }

  protected Result<Offset, ParserError> parseValue(List<Token> tokens, int index) {
    log.debug("Start parsing value at index:{}", index);

    Optional<Offset> value;
    try {
      value = Optional.<Offset>empty()
          .or(() -> parseLiteral(tokens, index))
          .or(() -> parseObject(tokens, index).orElseThrow(Function.identity()))
          .or(() -> parseArray(tokens, index).orElseThrow(Function.identity()));
    } catch (ParserError parserError) {
      return Result.error(parserError);
    }

    return value.map(Result::<Offset, ParserError>success)
        .orElseGet(() -> Result.error(new ParserError()));
  }

  public Result<JValue, ParserError> parse(List<Token> tokenStream) {
    return parseValue(tokenStream, 0).map(node -> (JValue) node.value());
  }

  enum ObjectState {
    _START_,
    OPEN_OBJECT,
    PROPERTY,
    COMMA
  }

  enum PropertyState {
    _START_,
    KEY,
    COLON
  }

  enum ArrayState {
    _START_,
    OPEN_ARRAY,
    VALUE,
    COMMA
  }

  record Offset(
      JNode value,
      int index
  ) {

  }
}
