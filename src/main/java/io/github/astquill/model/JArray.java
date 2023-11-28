package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public final class JArray implements JNode, JValue {

  private final String type = "Array";

  private List<JValue> children;

  private Loc loc;
}
