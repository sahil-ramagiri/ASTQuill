package io.github.astquill.model;

import java.util.List;

public final class JObject extends JNode {

  private final String type = "Object";

  private List<JProperty> children;

  private Loc loc;

}
