package io.github.astquill.util;

import io.github.astquill.error.InvalidXpathException;
import io.github.astquill.model.*;
import io.github.astquill.util.XpathLexer.XpathToken;
import io.github.astquill.util.XpathLexer.XpathTokenType;
import no.gorandalum.fluentresult.Result;
import java.util.List;
import java.util.function.Function;

public class ASTEdit {

  public static boolean editKey(JValue root, String xpath, String newKey) {

    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    if (xpathTokens.size() < 1
        || xpathTokens.get(xpathTokens.size() - 1).tokenType() != XpathTokenType.STRING) {
      return false;
    }

    List<JValue> parentChain = JsonPointer.findValueAndParent(root,
        xpathTokens.subList(0, xpathTokens.size() - 1));

    if (parentChain.isEmpty() || !(parentChain.get(
        parentChain.size() - 1) instanceof JObject object)) {
      return false;
    }

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

  public static boolean editString(JValue root, String xpath, String newString) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    List<JValue> chain = JsonPointer.findValueAndParent(root, xpathTokens);
    if (chain.size() < 2) {
      return false;
    }

    JValue child = chain.get(chain.size() - 1);

    String newRaw = LiteralParser.unparseString(newString);

    JLiteral newChild = new JLiteral(JPrimitive.STRING, newRaw,
        child.getLoc().setNewLength(newRaw.length()));

    JValue parent = chain.get(chain.size() - 2);

    return assignChildToParent(child, newChild, parent);
  }

  public static boolean editNumber(JValue root, String xpath, long newNumber) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    List<JValue> chain = JsonPointer.findValueAndParent(root, xpathTokens);
    if (chain.size() < 2) {
      return false;
    }

    JValue child = chain.get(chain.size() - 1);

    String newRaw = String.valueOf(newNumber);

    JLiteral newChild = new JLiteral(JPrimitive.NUMBER, newRaw,
        child.getLoc().setNewLength(newRaw.length()));

    JValue parent = chain.get(chain.size() - 2);

    return assignChildToParent(child, newChild, parent);
  }

  public static boolean editBoolean(JValue root, String xpath, boolean newBoolean) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    List<JValue> chain = JsonPointer.findValueAndParent(root, xpathTokens);
    if (chain.size() < 2) {
      return false;
    }

    JValue child = chain.get(chain.size() - 1);

    String newRaw = String.valueOf(newBoolean);

    JLiteral newChild = new JLiteral(JPrimitive.BOOL, newRaw,
        child.getLoc().setNewLength(newRaw.length()));

    JValue parent = chain.get(chain.size() - 2);

    return assignChildToParent(child, newChild, parent);
  }

  public static boolean editNull(JValue root, String xpath) {
    XpathLexer xpathLexer = new XpathLexer();
    Result<List<XpathToken>, InvalidXpathException> result = xpathLexer.tokenize(xpath);
    List<XpathToken> xpathTokens = result.orElseThrow(Function.identity());

    List<JValue> chain = JsonPointer.findValueAndParent(root, xpathTokens);
    if (chain.size() < 2) {
      return false;
    }

    JValue child = chain.get(chain.size() - 1);

    String newRaw = "null";

    JLiteral newChild = new JLiteral(JPrimitive.NULL, newRaw,
        child.getLoc().setNewLength(newRaw.length()));

    JValue parent = chain.get(chain.size() - 2);

    return assignChildToParent(child, newChild, parent);
  }

  private static boolean assignChildToParent(JValue child, JLiteral newChild, JValue parent) {
    if (parent instanceof JObject jObject) {
      for (int i = 0; i < jObject.getChildren().size(); i++) {
        JProperty jProperty = jObject.getChildren().get(i);
        if (jProperty.getValue().equals(child)) {
          jProperty.setValue(newChild);
          return true;
        }
      }
    } else if (parent instanceof JArray jArray) {
      for (int i = 0; i < jArray.getChildren().size(); i++) {
        JValue jValue = jArray.getChildren().get(i);
        if (jValue.equals(child)) {
          jArray.getChildren().set(i, newChild);
          return true;
        }
      }
    }
    return false;
  }
}
