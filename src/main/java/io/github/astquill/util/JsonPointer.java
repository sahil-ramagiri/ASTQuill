package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import io.github.astquill.model.JArray;
import io.github.astquill.model.JObject;
import io.github.astquill.model.JProperty;
import io.github.astquill.model.JValue;
import io.github.astquill.util.XpathLexer.XpathToken;
import no.gorandalum.fluentresult.Result;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class JsonPointer {

  public static Optional<JValue> findValue(JValue root, String xpath) throws InvalidXpathException {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    if (xpathTokens.isEmpty()) {
      return Optional.empty();
    }
    return findValue(root, xpathTokens);
  }

  public static Optional<JValue> findValue(JValue root, List<XpathToken> xpathTokens)
      throws InvalidXpathException {
    JValue current = root;
    for (XpathToken xpathToken : xpathTokens) {
      switch (xpathToken.tokenType()) {
        case INDEX -> {
          if (current instanceof JArray jArray) {
            if (xpathToken.getIndex() < jArray.getChildren().size()) {
              current = jArray.getChildren().get(xpathToken.getIndex());
            } else {
              return Optional.empty();
            }
          } else {
            return Optional.empty();
          }
        }

        case STRING -> {
          if (current instanceof JObject jObject) {
            Optional<JValue> value = jObject.getChildren().stream()
                .filter(jProperty -> jProperty.getKey().getValue().equals(xpathToken.getText()))
                .map(JProperty::getValue)
                .findFirst();
            if (value.isPresent()) {
              current = value.get();
            } else {
              return Optional.empty();
            }
          }
        }
      }
    }
    return Optional.of(current);
  }

}
