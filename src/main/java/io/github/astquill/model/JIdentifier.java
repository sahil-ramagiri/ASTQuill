package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JIdentifier implements JNode {

  private final String type = "Identifier";

  private String value;

  private String raw;

  private Loc loc;
}
