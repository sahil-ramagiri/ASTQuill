package io.github.astquill.util;

import io.github.astquill.model.JArray;
import io.github.astquill.model.JIdentifier;
import io.github.astquill.model.JLiteral;
import io.github.astquill.model.JNode;
import io.github.astquill.model.JObject;
import io.github.astquill.model.JProperty;
import io.github.astquill.model.JValue;
import io.github.astquill.model.Loc;

public class ASTToString {
  private int lin;
  private int col;

  public ASTToString() {
    this.lin = 1;
    this.col = 1;
  }

  public String toString(JValue value) {
    StringBuilder res = new StringBuilder();

    if (value instanceof JArray) {
      res.append(JArrayToString((JArray) value));
    }
    else if (value instanceof JLiteral) {
      res.append(JLiteralToString((JLiteral) value));
    }
    else if (value instanceof JObject) {
      res.append(JObjectToString((JObject) value));
    }
    else {
      throw new Error("ToString Error " + value.getClass().getName());
    }

    return res.toString();
  }

  public String toString(JNode node) {
    StringBuilder res = new StringBuilder();

    if (node instanceof JArray) {
      res.append(JArrayToString((JArray) node));
    }
    else if (node instanceof JLiteral) {
      res.append(JLiteralToString((JLiteral) node));
    }
    else if (node instanceof JObject) {
      res.append(JObjectToString((JObject) node));
    }
    else if (node instanceof JIdentifier) {
      res.append(JLiteralToString((JLiteral) node));
    }
    else if (node instanceof JProperty) {
      res.append(JPropertyToString((JProperty) node));
    }
    else {
      throw new Error("ToString Error " + node.getClass().getName());
    }

    return res.toString();
  }

  private String JArrayToString(JArray array) {
    StringBuilder res = new StringBuilder();
    Loc loc = array.getLoc();

    while (lin < loc.getStart().getLine()) {
      res.append("\n");
      col = 1;
      lin++;
    }

    while (col < loc.getStart().getColumn() - 1) {
      res.append(" ");
      col++;
    }

    res.append("[");
    col++;

    for (JValue value : array.getChildren()) {
      res.append(this.toString(value));
      res.append(",");
      col++;
    }

    if (res.charAt(res.length() - 1) == ',') {
      res.delete(res.length() - 1, res.length());
      col--;
    }

    while (lin < loc.getEnd().getLine()) {
      res.append("\n");
      col = 1;
      lin++;
    }

    res.append("]");
    col++;

    while (col < loc.getEnd().getColumn() - 1) {
      res.append(" ");
      col++;
    }

    return res.toString();
  }

  private String JIdentifierToString(JIdentifier identifier) {
    StringBuilder res = new StringBuilder();

    Loc loc = identifier.getLoc();

    while (lin < loc.getStart().getLine()) {
      res.append("\n");
      col = 1;
      lin++;
    }

    while (col < loc.getStart().getColumn()) {
      res.append(" ");
      col++;
    }

    col += identifier.getRaw().length();

    return res.append(identifier.getRaw()).toString();
  }

  private String JLiteralToString(JLiteral literal) {
    StringBuilder res = new StringBuilder();
    Loc loc = literal.getLoc();

    while (lin < loc.getStart().getLine()) {
      res.append("\n");
      col = 1;
      lin++;
    }

    while (col < loc.getStart().getColumn() - 1) {
      res.append(" ");
      col++;
    }

    col += literal.getRaw().length();
    res.append(literal.getRaw());

    while (col < loc.getEnd().getColumn() - 1 ) {
      res.append(" ");
      col++;
    }

    return res.toString();
  }

  private String JObjectToString(JObject object) {
    StringBuilder res = new StringBuilder();
    Loc loc = object.getLoc();

    while (lin < loc.getStart().getLine()) {
      res.append("\n");
      col = 1;
      lin++;
    }

    while (col < loc.getStart().getColumn() - 1) {
      res.append(" ");
      col++;
    }

    res.append("{");
    col++;

    for (JProperty property : object.getChildren()) {
      res.append(this.toString(property));
      res.append(",");
      col++;
    }

    if (res.charAt(res.length() - 1) == ',') {
      res.delete(res.length() - 1, res.length());
      col--;
    }

    while (lin < loc.getEnd().getLine()) {
      res.append("\n");
      lin++;
      col = 1;
    }

    while (col < loc.getEnd().getColumn() - 1) {
      res.append(" ");
      col++;
    }

    res.append("}");
    col++;
    return res.toString();
  }

  private String JPropertyToString(JProperty property) {
    col++;
    return JIdentifierToString(property.getKey()) + ":" + this.toString(property.getValue());
  }
}