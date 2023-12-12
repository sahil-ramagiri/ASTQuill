package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import io.github.astquill.model.JIdentifier;
import io.github.astquill.model.JObject;
import io.github.astquill.model.JProperty;
import io.github.astquill.model.JValue;
import io.github.astquill.model.Loc;
import io.github.astquill.util.XpathLexer.XpathToken;
import io.github.astquill.util.XpathLexer.XpathTokenType;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import no.gorandalum.fluentresult.Result;

public class ASTEdit {

  public static boolean editKey(JValue root, String xpath, String newKey) {

    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    if (xpathTokens.size() < 1
        || xpathTokens.get(xpathTokens.size() - 1).tokenType() != XpathTokenType.STRING) {
      return false;
    }

    Optional<JValue> value = JsonPointer.findValue(root,
        xpathTokens.subList(0, xpathTokens.size() - 1));

    if (value.isEmpty() || !(value.get() instanceof JObject)) {
      return false;
    }

    JObject object = (JObject) value.get();
    String prevKey = xpathTokens.get(xpathTokens.size() - 1).getText();

    for (JProperty jProperty : object.getChildren()) {
      if (jProperty.getKey().getValue().equals(prevKey)) {
        String newKeyRaw = LiteralParser.unparseString(newKey);
        int columnDiff = newKeyRaw.length() - jProperty.getKey().getRaw().length();
        Loc loc = jProperty.getKey().getLoc().addEndOffset(columnDiff, 0);
        jProperty.setKey(new JIdentifier(newKey, newKeyRaw, loc));
        return true;
      }
    }

    return false;
  }
}
