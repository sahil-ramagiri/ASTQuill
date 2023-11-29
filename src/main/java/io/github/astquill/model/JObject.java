package io.github.astquill.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public final class JObject implements JNode, JValue {

  private final String type = "Object";

  private List<JProperty> children;

  private Loc loc;

  public String toString() {
    StringBuilder newString = new StringBuilder();
    newString.append("{");

    for (JProperty property : children) {
      newString.append(property.toString());
      newString.append(",");
    }

    newString.append("}");
    return newString.toString();
  }
}
