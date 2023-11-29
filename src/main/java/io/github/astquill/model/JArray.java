package io.github.astquill.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class JArray implements JNode, JValue {

  private final String type = "Array";

  private List<JValue> children;

  private Loc loc;

  public String toString() {
    StringBuilder newString = new StringBuilder();
    newString.append("[");

    for (JValue value : children) {
      newString.append(value.toString());
      newString.append(",");
    }

    newString.append("]");
    return newString.toString();
  }
}
