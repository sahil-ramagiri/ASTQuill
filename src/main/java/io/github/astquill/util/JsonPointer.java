package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import io.github.astquill.model.JArray;
import io.github.astquill.model.JObject;
import io.github.astquill.model.JProperty;
import io.github.astquill.model.JValue;
import io.github.astquill.util.XpathLexer.XpathToken;
import no.gorandalum.fluentresult.Result;

import java.util.ArrayList;
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
    List<JValue> parentChain = findValueAndParent(root, xpathTokens);
    if (parentChain.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(parentChain.get(parentChain.size() - 1));
  }

  public static List<JValue> findValueAndParent(JValue root, List<XpathToken> xpathTokens)
      throws InvalidXpathException {
    JValue current = root;
    List<JValue> parentChain = new ArrayList<>();
    for (XpathToken xpathToken : xpathTokens) {
      switch (xpathToken.tokenType()) {
        case INDEX -> {
          if (current instanceof JArray jArray) {
            if (xpathToken.getIndex() < jArray.getChildren().size()) {
              parentChain.add(current);
              current = jArray.getChildren().get(xpathToken.getIndex());
            } else {
              return List.of();
            }
          } else {
            return List.of();
          }
        }

        case STRING -> {
          if (current instanceof JObject jObject) {
            Optional<JValue> value = jObject.getChildren().stream()
                .filter(jProperty -> jProperty.getKey().getValue().equals(xpathToken.getText()))
                .map(JProperty::getValue)
                .findFirst();
            if (value.isPresent()) {
              parentChain.add(current);
              current = value.get();
            } else {
              return List.of();
            }
          }
        }
      }
    }
    parentChain.add(current);
    return parentChain;
  }

}
