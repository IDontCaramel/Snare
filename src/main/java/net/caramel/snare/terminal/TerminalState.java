package net.caramel.snare.terminal;
import java.util.*;
public final class TerminalState {
 public static final int MAX_OUTPUT_LINES=200; private final List<String> output=new ArrayList<>(),history=new ArrayList<>(); private int historyIndex; private String draft="";
 public boolean submit(String raw){String value=raw.trim(); if(value.isEmpty())return false; history.add(value); historyIndex=history.size(); draft=""; append("> "+value); append("Command system not implemented yet."); return true;}
 public String historyUp(String current){if(history.isEmpty())return current; if(historyIndex==history.size())draft=current; historyIndex=Math.max(0,historyIndex-1); return history.get(historyIndex);}
 public String historyDown(){if(history.isEmpty())return ""; if(historyIndex<history.size()-1)return history.get(++historyIndex); historyIndex=history.size(); return draft;}
 public List<String> output(){return List.copyOf(output);} private void append(String line){output.add(line); while(output.size()>MAX_OUTPUT_LINES)output.remove(0);}
}