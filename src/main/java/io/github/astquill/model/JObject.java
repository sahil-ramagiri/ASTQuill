package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.List;

@AllArgsConstructor
@ToString
public final class JObject implements JNode, JValue {

  private final String type = "Object";

  private List<JProperty> children;

  private Loc loc;

}
