package org.gbif.dwca.action;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidateActionTest {

  @Test
  public void testNullPattern() throws Exception {
    assertTrue(ValidateAction.NULL_REPL.matcher(" ").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("null").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("Null").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("NULL").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("NULL ").find());
    assertTrue(ValidateAction.NULL_REPL.matcher(" NULL ").find());
    assertTrue(ValidateAction.NULL_REPL.matcher(" NULL").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("   NULL  ").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("\\N").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("\\n").find());
    assertTrue(ValidateAction.NULL_REPL.matcher("\\N ").find());
    assertTrue(ValidateAction.NULL_REPL.matcher(" \\n ").find());

    assertFalse(ValidateAction.NULL_REPL.matcher("").find());
    assertFalse(ValidateAction.NULL_REPL.matcher("a null").find());
    assertFalse(ValidateAction.NULL_REPL.matcher("nulll").find());
    assertFalse(ValidateAction.NULL_REPL.matcher("nu ll").find());
    assertFalse(ValidateAction.NULL_REPL.matcher("nul").find());
    assertFalse(ValidateAction.NULL_REPL.matcher("n").find());
  }
}
