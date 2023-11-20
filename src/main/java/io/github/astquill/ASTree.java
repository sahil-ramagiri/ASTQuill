package io.github.astquill;

import io.github.astquill.model.JNode;
import lombok.Getter;

@Getter
public class ASTree {

  JNode value;

  protected ASTree(JNode jNode) {
    this.value = jNode;
  }
}
