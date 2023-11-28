package io.github.astquill;

import io.github.astquill.model.JValue;
import lombok.Getter;

@Getter
public class ASTree {

  JValue value;

  protected ASTree(JValue jValue) {
    this.value = jValue;
  }
}
