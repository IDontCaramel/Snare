package net.caramel.snare.terminal;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class TerminalStateTest {
 @Test void submissionAddsPromptResponseAndNavigableHistory(){TerminalState t=new TerminalState(new TerminalCommandRegistry()); assertFalse(t.submit("  ")); assertTrue(t.submit("help")); assertEquals(java.util.List.of("> help"),t.output()); assertEquals("help",t.historyUp("draft")); assertEquals("draft",t.historyDown());}
 @Test void outputIsBounded(){TerminalState t=new TerminalState(new TerminalCommandRegistry()); for(int i=0;i<250;i++)t.submit("c"+i); assertEquals(TerminalState.MAX_OUTPUT_LINES,t.output().size()); assertEquals("> c50",t.output().get(0));}
}