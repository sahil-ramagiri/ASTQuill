package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public final class JLiteral implements JNode, JValue {

  private final String type = "Literal";

  private JPrimitive subtype;

  private String raw;

  private Loc loc;

  public boolean isText() {
    return subtype.equals(JPrimitive.STRING);
  }

  public boolean isBoolean() {
    return subtype.equals(JPrimitive.BOOL);
  }

  public boolean isNumber() {
    return subtype.equals(JPrimitive.NUMBER);
  }

  public boolean isNull() {
    return subtype.equals(JPrimitive.NULL);
  }

}
