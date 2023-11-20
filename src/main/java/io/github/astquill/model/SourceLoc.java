package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SourceLoc {
  private int line;
  private int column;
  private int offset;
}
