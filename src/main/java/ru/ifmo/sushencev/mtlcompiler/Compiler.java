package ru.ifmo.sushencev.mtlcompiler;

import com.google.common.base.Joiner;
import ru.ifmo.sushencev.mtlcompiler.commands.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Jackson on 17.12.2016.
 */
public final class Compiler {
    public Compiler() {

    }

    public static void main(String[] args) throws IOException, ParseException {
        Compiler compiler = new Compiler();
        if (args.length != 2) {
            System.out.println("Usage: MTLCompiler sourcePath resultPath");
            return;
        }
        compiler.compile(args[0], args[1]);
    }

    private String alphabet;
    private LinesBuffer lines;
    private Map<String, Integer> functionConformity = new HashMap<>();

    public void compile(String sourcePath, String resultPath) throws IOException, ParseException {
        lines = new LinesBuffer(Util.getLines(sourcePath));
        //System.out.println(Util.getLines(sourcePath));
        String first = lines.get();
        if (!first.startsWith("#alphabet ")) throw new ParseException("", 0);
        String ss[] = first.split(" ");
        if (ss.length != 2 || ss[1].trim().isEmpty()) throw new ParseException("", 0);
        if (!ss[1].contains("_")) throw new ParseException("Alphabet should contain blank character", 0);
        alphabet = ss[1];

        LinkedList<Command> allCommands = new LinkedList<>();

        while (!lines.isEmpty()) {
            String line = lines.get();
            if (line.startsWith("def")) {
                allCommands.addAll(parseFunction(line));
            } else {
                throw new ParseException("", 0);
            }
        }
        allCommands.add(ReturnInCallStack.getInstance());

        try (PrintWriter writer = new PrintWriter(resultPath)) {
            int entryPoint = functionConformity.get("main");
            //writer.printf("start: \naccept: AC\nreject: RJ\nblank: _\n", entryPoint);
            writer.printf("2\n");
            preCodeToCode(String.format("S * _ -> line%d * ^ _ ^", entryPoint)).forEach(writer::println);
            allCommands.forEach(c -> c.genPreCode()
                    .forEach(s -> preCodeToCode(s).forEach(x -> writer.println(x))));// + " (from " + s + ")"))));
        }
    }

    private List<String> preCodeToCode(String s) {
        List<String> res = new LinkedList<>();
        preCodeToCode1(s, 0).forEach(line -> res.addAll(preCodeToCode1(line, 1)));
        return res;
    }

    private List<String> preCodeToCode1(String s, int m) {
        List<String> res = new LinkedList<>();

        String ss[] = parseFromTo(s, m);
        String from = ss[0];
        String to = ss[1];

        if ((from.startsWith("!") || from.equals("*")) &&
                (to.startsWith("!") || to.equals("*"))) {
            if (from.startsWith("!") && to.startsWith("!")) throw new RuntimeException();
            Character cc = null;
            if (from.startsWith("!")) cc = from.substring(1).charAt(0);
            if (to.startsWith("!")) cc = to.substring(1).charAt(0);
            String alphabet;
            if (m == 0) {
                alphabet = this.alphabet;
            } else {
                alphabet = FunctionCall.getReturnAbbrevationAlphabet();
            }
            for (char c : alphabet.toCharArray()) {
                if (cc == null || cc != c) {
                    res.add(replace(s, "" + c, "" + c, m));
                }
            }
            return res;
        }

        preCodeToCode2(s, m, 0).forEach(line -> res.addAll(preCodeToCode2(line, m, 1)));

        return res;
    }

    private List<String> preCodeToCode2(String s, int m, int m2) {
        List<String> res = new LinkedList<>();
        String ss[] = parseFromTo(s, m);
        String from = ss[0];
        String to = ss[1];
        String cur = m2 == 0 ? from : to;
        if (!cur.startsWith("!") && !cur.equals("*")) return Collections.singletonList(s);

        String alphabet;
        if (m == 0) {
            alphabet = this.alphabet;
        } else {
            alphabet = FunctionCall.getReturnAbbrevationAlphabet();
        }
        for (char c : alphabet.toCharArray()) {
            if (cur.equals("*") || cur.substring(1).charAt(0) != c) {
                String news = m2 == 0 ? replace(s, "" + c, to, m) : replace(s, from, "" + c, m);
                res.add(news);
            }
        }
        return res;
    }

    private String[] parseFromTo(String s, int m) {
        String ss[] = s.split(" ");
        String from;
        String to;
        if (m == 0) {
            from = ss[1];
            to = ss[5];
        } else {
            from = ss[2];
            to = ss[7];
        }
        return new String[]{from, to};
    }

    private String replace(String s, String from, String to, int m) {
        String ss[] = s.split(" ");
        if (m == 0) {
            ss[1] = from;
            ss[5] = to;
        } else {
            ss[2] = from;
            ss[7] = to;
        }
        return Joiner.on(" ").join(ss);
    }

    private LinkedList<Command> parseFunction(String header) throws ParseException {
        String ss[] = header.split(" ");
        if (ss.length != 2 || ss[1].trim().isEmpty()) throw new ParseException("", 0);
        String name = ss[1];
        int fp = lines.line(); // pointer to the first command
        functionConformity.put(name, fp);

        LinkedList<Command> commands = readCommands(1, null);
        if (name.equals("main")) {
            commands.getLast().setLink("AC");
        } else {
            commands.getLast().setLinkTo(ReturnInCallStack.getInstance());
        }
        return commands;
    }

    private int depth(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '\t') return i;
        }
        throw new RuntimeException();
    }


    private Command parseCommand(int depth, Cycle cyclePointer) throws ParseException {
        int commandPointer = lines.line();
        String line = lines.get();
        if (depth(line) != depth) throw new ParseException("", 0);
        line = line.trim();

        if (line.equals("cycle")) {
            Cycle cycle = new Cycle(commandPointer);
            List<Command> subCommands = readCommands(depth + 1, cycle);
            subCommands.forEach(cycle::appendSubSequence);
            return cycle;
        }

        if (line.startsWith("ifchar")) {
            String ss[] = line.split(" ");
            if (ss.length != 2 || ss[1].trim().length() != 1) throw new ParseException("", 0);
            char c = ss[1].trim().charAt(0);
            //int thenPointer = lines.line();
            LinkedList<Command> thenSequence = readCommands(depth + 1, cyclePointer);
            IfChar ifChar;
            if (lines.peek().trim().equals("else") && depth(lines.peek()) == depth) {
                lines.get();
                LinkedList<Command> elseSequence = readCommands(depth + 1, cyclePointer);
                ifChar = new IfChar(commandPointer, c, thenSequence, elseSequence);
            } else {
                ifChar = new IfChar(commandPointer, c, thenSequence, null);
            }
            return ifChar;
        }
        if (line.equals("_r")) {
            return new MoveCursor(commandPointer, MoveCursor.Direction.RIGHT);
        }
        if (line.equals("_l")) {
            return new MoveCursor(commandPointer, MoveCursor.Direction.LEFT);
        }
        if (line.startsWith("_w")) {
            String ss[] = line.split(" ");
            if (ss.length != 2 || ss[1].trim().length() != 1) throw new ParseException("", 0);
            return new Write(commandPointer, ss[1].charAt(0));
        }
        if (line.equals("return")) {
            return new Return(commandPointer);
        }
        if (line.equals("continue")) {
            return new Continue(commandPointer, cyclePointer);
        }
        if (functionConformity.containsKey(line)) {
            return new FunctionCall(functionConformity.get(line), commandPointer);
        }
        throw new ParseException("", 0);
    }

    private LinkedList<Command> readCommands(int depth, Cycle cyclePointer) throws ParseException {
        LinkedList<Command> res = new LinkedList<>();
        do {
            Command command = parseCommand(depth, cyclePointer);
            if (!res.isEmpty()) res.getLast().setLinkTo(command);
            res.add(command);
        } while (!lines.isEmpty() && depth(lines.peek()) == depth);
        return res;
    }
}