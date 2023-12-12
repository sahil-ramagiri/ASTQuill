package io.github.astquill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.astquill.model.JValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Optional;

class ASTQuillTest {
  @Test
  void read() throws JsonProcessingException {
    String json = """
        {
            "key1": [true, false, null],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            }
        }
        """;

    ASTree result = ASTQuill.read(json);

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writeValueAsString(result));
  }

  @Test
  void tostring() throws JsonProcessingException {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
            "key5": []
        }
        """;

    ASTree result = ASTQuill.read(json);
    System.out.println(ASTQuill.write(result));
  }

  @Test
  void findValue() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
            "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    Optional<JValue> value = ASTQuill.findValue(asTree, "key1[5].name");

    System.out.println(value);
  }

  @Test
  void editKey() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
               "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    boolean success = ASTQuill.editKey(asTree, "key1[5].name", "studentName");
    Assertions.assertTrue(success);
    System.out.println(ASTQuill.write(asTree));
  }

  @Test
  void editString() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
               "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    boolean success = ASTQuill.editToString(asTree, "key1[5].student", "maybe");
    success = success && ASTQuill.editToString(asTree, "key2.key 3", "an array");
    Assertions.assertTrue(success);
    System.out.println(ASTQuill.write(asTree));
  }

  @Test
  void editNumber() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
               "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    boolean success = ASTQuill.editToNumber(asTree, "key1[5].student", 1);
    success = success && ASTQuill.editToNumber(asTree, "key2.key 3[2]", 3);
    Assertions.assertTrue(success);
    System.out.println(ASTQuill.write(asTree));
  }

  @Test
  void editBoolean() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
               "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    boolean success = ASTQuill.editToBoolean(asTree, "key1[0]", false);
    success = success && ASTQuill.editToBoolean(asTree, "key2.key 3[0]", true);
    Assertions.assertTrue(success);
    System.out.println(ASTQuill.write(asTree));
  }

  @Test
  void editNull() {
    String json = """
        {
            "key1": [true,    false, null, 2, 1, {
                "name": "ian",
                "student": true
            }],
            "key2": {
                "key 3": [1, 2, "3", 1e10, 1e-3]
            },
            "key  4": [{}, {}, {}],
               "key5": []
        }
        """;

    ASTree asTree = ASTQuill.read(json);
    boolean success = ASTQuill.editToNull(asTree, "key  4");
    success = success && ASTQuill.editToNull(asTree, "key2.key 3[0]");
    Assertions.assertTrue(success);
    System.out.println(ASTQuill.write(asTree));
  }
}