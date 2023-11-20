package io.github.astquill.model;

import java.util.List;

public final class JArray extends JNode {
  private final String type =  "Array";

  private List<JNode> children;

  private Loc loc;
}
