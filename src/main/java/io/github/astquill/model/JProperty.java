package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JProperty implements JNode {

  private final String type = "Property";

  private JIdentifier key;

  private JValue value;

  private Loc loc;

  @Override
  public String toString() {
    return "\"" + key.getValue() + "\"" + ": " + value.toString();
  }
}
