package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class JProperty implements JNode {
  private final String type = "Property";

  private JIdentifier key;

  private JValue value;

  private Loc loc;
}
