package net.caramel.snare.terminal;
import java.util.*;
import net.minecraft.client.MinecraftClient;
public final class TerminalState {
 public static final int MAX_OUTPUT_LINES=200; private final List<String> output=new ArrayList<>(),history=new ArrayList<>(); private int historyIndex; private String draft=""; private final TerminalCommandRegistry commands;
 public TerminalState(TerminalCommandRegistry commands){this.commands=Objects.requireNonNull(commands);}
 public boolean submit(String raw){String value=raw.trim(); if(value.isEmpty())return false; history.add(value); historyIndex=history.size(); draft=""; append("> "+value); MinecraftClient client=MinecraftClient.getInstance(); if(client!=null)commands.execute(value,new TerminalCommandContext(client,this::append)); return true;}
 public String historyUp(String current){if(history.isEmpty())return current; if(historyIndex==history.size())draft=current; historyIndex=Math.max(0,historyIndex-1); return history.get(historyIndex);}
 public String historyDown(){if(history.isEmpty())return ""; if(historyIndex<history.size()-1)return history.get(++historyIndex); historyIndex=history.size(); return draft;}
 public List<String> output(){return List.copyOf(output);}
 public List<TerminalSuggestion> suggestions(String input){return commands.suggest(input);}
 void append(String line){output.add(line); while(output.size()>MAX_OUTPUT_LINES)output.remove(0);}
}