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
}
