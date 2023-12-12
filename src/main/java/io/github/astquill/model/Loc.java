package io.github.astquill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loc {

  private SourceLoc start;
  private SourceLoc end;

  public Loc addOffset(int colOffset, int rowOffset) {
    SourceLoc newStart = new SourceLoc(start.getLine() + rowOffset, start.getColumn() + colOffset,
        start.getOffset());
    SourceLoc newEnd = new SourceLoc(end.getLine() + rowOffset, end.getColumn() + colOffset,
        end.getOffset());

    return new Loc(newStart, newEnd);
  }

  public Loc addEndOffset(int colOffset, int rowOffset) {
    SourceLoc newStart = new SourceLoc(start.getLine(), start.getColumn(), start.getOffset());
    SourceLoc newEnd = new SourceLoc(end.getLine() + rowOffset, end.getColumn() + colOffset,
        end.getOffset());

    return new Loc(newStart, newEnd);
  }
}
