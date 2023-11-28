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

}
