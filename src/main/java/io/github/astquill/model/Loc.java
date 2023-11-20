package io.github.astquill.model;

import lombok.Data;

@Data
public class Loc {

  private SourceLoc start;
  private SourceLoc end;
}
