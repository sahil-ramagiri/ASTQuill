package io.github.astquill;

import io.github.astquill.model.JValue;
import io.github.astquill.result.LexerResult;
import io.github.astquill.settings.LexerOptions;
import io.github.astquill.util.ASTEdit;
import io.github.astquill.util.ASTToString;
import io.github.astquill.util.JsonPointer;
import io.github.astquill.util.Lexer;
import io.github.astquill.util.Lexer.Token;
import io.github.astquill.util.Parser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ASTQuill {

  public static ASTree read(String input) {
    Lexer lexer = new Lexer();
    LexerResult<List<Token>> lexerResult = lexer.tokenize(input,
        LexerOptions.builder().build());

    Parser parser = new Parser();
    var parserResult = parser.parse(lexerResult.getT());

    return new ASTree(parserResult.orElseThrow(Function.identity()));
  }

  public static String write(ASTree asTree) {
    ASTToString astToString = new ASTToString();
    return astToString.toString(asTree.getValue());
  }


  public static Optional<JValue> findValue(ASTree asTree, String xpath) {
    return JsonPointer.findValue(asTree.getValue(), xpath);
  }

  public static boolean editKey(ASTree asTree, String xpath, String newKey) {
    return ASTEdit.editKey(asTree.getValue(), xpath, newKey);
  }

  public static boolean editToString(ASTree asTree, String xpath, String newString) {
    return ASTEdit.editString(asTree.getValue(), xpath, newString);
  }

  public static boolean editToNumber(ASTree asTree, String xpath, long newNumber) {
    return ASTEdit.editNumber(asTree.getValue(), xpath, newNumber);
  }

  public static boolean editToBoolean(ASTree asTree, String xpath, boolean newBoolean) {
    return ASTEdit.editBoolean(asTree.getValue(), xpath, newBoolean);
  }

  public static boolean editToNull(ASTree asTree, String xpath) {
    return ASTEdit.editNull(asTree.getValue(), xpath);
  }
}