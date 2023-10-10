package io.github.astquill.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ASTNodeTest {

  @Test
  void setKey() {
    // given
    ASTNode astNode = new ASTNode("key1", "value1");

    // when
    String key = astNode.getKey();

    // then
    Assertions.assertThat(key).isEqualTo("key1");
  }

  @Test
  void setValue() {
    // given
    ASTNode astNode = new ASTNode("key1", "value1");

    // when
    String value = astNode.getValue();

    // then
    Assertions.assertThat(value).isEqualTo("value1");
  }
}