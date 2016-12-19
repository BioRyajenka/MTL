package ru.ifmo.sushencev.mtlcompiler;

import com.google.common.base.Joiner;
import ru.ifmo.sushencev.mtlcompiler.commands.*;
import ru.ifmo.sushencev.mtlcompiler.commands.MoveCursor.Direction;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Jackson on 17.12.2016.
 */
public final class Compiler {
    public Compiler() {

    }

    public static void main(String[] args) throws IOException, ParseException {
        //Compiler compiler = new Compiler();
        /*if (args.length != 2) {
            System.out.println("Usage: MTLCompiler sourcePath resultPath");
            return;
        }
        compiler.compile(args[0], args[1]);*/
        //compiler.compile("M.mtl", "M.out");
        List<String> lines = Util.getLines("M.out");
        try (PrintWriter writer = new PrintWriter("M.obf")) {
            for (String line : lines) {
                String ss[] = line.split(" ");
                for (String s : ss) {
                    if (s.startsWith("line")) {
                        if (!obf.containsKey(s)) obf.put(s, "" + (freeObf++));
                        line = line.replaceAll(s, obf.get(s));
                    }
                }
                writer.println(line);
            }
        }
    }

    private static int freeObf = 0;
    private static Map<String, String> obf = new HashMap<>();

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

        LinkedList<ThirdTapeAdapter> allCommands = new LinkedList<>();

        forwardFunctions();

        while (!lines.isEmpty()) {
            String line = lines.get();
            if (line.startsWith("def")) {
                allCommands.addAll(parseFunction(line));
            } else {
                throw new ParseException("", 0);
            }
        }
        allCommands.add(new ThirdTapeAdapter(Return.getAutoReturn(), false));

        try (PrintWriter writer = new PrintWriter(resultPath)) {
            int entryPoint = functionConformity.get("main");
            writer.printf("3\n");
            preCodeToCode(String.format("S * _ _ -> line%d * ^ _ ^ _ ^", entryPoint)).forEach(writer::println);
            allCommands.forEach(c -> c.genPreCode()
                    .forEach(s -> preCodeToCode(s) // + "(from " + c.getCommand().getClass().getSimpleName() + ")"
                            .forEach(x -> writer.println(x))));// + " (from " + s + ")"))));
        }
    }

    private void forwardFunctions() {
        while (!lines.isEmpty()) {
            String line = lines.get();
            if (line.startsWith("def")) {
                int fp = lines.line(); // pointer to the first command
                String name = line.split(" ")[1];
                functionConformity.put(name, fp);
            }
        }
        lines.reset();
        lines.get();
    }

    private List<String> preCodeToCode(String s) {
        List<String> res = new LinkedList<>();
        //preCodeToCode1(s, 0).forEach(line -> res.addAll(preCodeToCode1(line, 1)));
        preCodeToCode1(s, 0).forEach(s1 -> preCodeToCode1(s1, 1)
                .forEach(s2 -> res.addAll(preCodeToCode1(s2, 2))));
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

            for (char c : getAlphabet(m)) {
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

        System.out.println(s);
        for (char c : getAlphabet(m)) {
            if (cur.equals("*") || cur.substring(1).charAt(0) != c) {
                String news = m2 == 0 ? replace(s, "" + c, to, m) : replace(s, from, "" + c, m);
                res.add(news);
            }
        }
        return res;
    }

    private char[] getAlphabet(int m) {
        String alphabet;
        if (m == 0 || m == 2) {
            alphabet = this.alphabet;
        } else {
            throw new RuntimeException("Trying to get stack tape alphabet!");
            //alphabet = FunctionCall.getReturnAbbrevationAlphabet();
        }
        return alphabet.toCharArray();
    }

    private String[] parseFromTo(String s, int m) {
        final int MAXM = 3;
        String ss[] = s.split(" ");
        //%s * * * -> %s * ^ * ^ * ^
        String from = ss[m + 1];
        String to = ss[MAXM + 3 + m * 2];
        return new String[]{from, to};
    }

    private String replace(String s, String from, String to, int m) {
        //System.out.println("BEFORE " + s);
        String ss[] = s.split(" ");
        ss[m + 1] = from;
        ss[6 + m * 2] = to;
        String res = Joiner.on(" ").join(ss);
        //System.out.println("AFTER " + s);
        return res;
    }

    private LinkedList<ThirdTapeAdapter> parseFunction(String header) throws ParseException {
        String ss[] = header.split(" ");
        if (ss.length != 2 || ss[1].trim().isEmpty()) throw new ParseException("", 0);
        String name = ss[1];

        LinkedList<ThirdTapeAdapter> commands = readCommands(1, null);
        if (name.equals("main")) {
            commands.getLast().setLink("AC");
        } else {
            commands.getLast().setLinkTo(Return.getAutoReturn());
        }
        return commands;
    }

    private int depth(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '\t') return i;
        }
        throw new RuntimeException();
    }


    private ThirdTapeAdapter parseCommand(int depth, Cycle cyclePointer) throws ParseException {
        int commandPointer = lines.line();
        String line = lines.get();
        if (depth(line) != depth) throw new ParseException("", 0);
        line = line.trim();

        if (line.equals("cycle")) {
            Cycle cycle = new Cycle(commandPointer);
            List<ThirdTapeAdapter> subCommands = readCommands(depth + 1, cycle);
            subCommands.forEach(cycle::appendSubSequence);
            return new ThirdTapeAdapter(cycle, false);
        }
        if (line.startsWith("ifchar ") || line.startsWith("ifchar1 ")) {
            String ss[] = line.split(" ");
            if (ss.length != 2 || ss[1].trim().length() != 1) throw new ParseException("", 0);
            char c = ss[1].trim().charAt(0);
            boolean onBuffer = ss[0].endsWith("1");
            LinkedList<ThirdTapeAdapter> thenSequence = readCommands(depth + 1, cyclePointer);
            LinkedList<ThirdTapeAdapter> elseSequence = null;
            if (lines.peek().trim().equals("else") && depth(lines.peek()) == depth) {
                lines.get();
                elseSequence = readCommands(depth + 1, cyclePointer);
            }
            return new ThirdTapeAdapter(new IfChar(commandPointer, c, thenSequence, elseSequence), onBuffer);
        }
        if (line.startsWith("if ")) {
            String ss[] = line.split(" ");
            if (ss.length != 2) throw new ParseException("", 0);
            String functionName = ss[1];
            int functionPointer = functionConformity.get(functionName);
            LinkedList<ThirdTapeAdapter> thenSequence = readCommands(depth + 1, cyclePointer);
            LinkedList<ThirdTapeAdapter> elseSequence = null;
            if (lines.peek().trim().equals("else") && depth(lines.peek()) == depth) {
                lines.get();
                elseSequence = readCommands(depth + 1, cyclePointer);
            }
            return new ThirdTapeAdapter(new If(commandPointer, functionPointer, thenSequence, elseSequence), false);
        }
        if (line.matches(">+1?") || line.matches("<+1?")) {
            boolean onBuffer = line.endsWith("1");
            Direction dir = line.startsWith("<") ? Direction.LEFT : Direction.RIGHT;
            int times = onBuffer ? line.length() - 1 : line.length();
            return new ThirdTapeAdapter(new MoveCursor(commandPointer, dir, times), onBuffer);
        }
        if (line.startsWith("setchar ") || line.startsWith("setchar1 ")) {
            String ss[] = line.split(" ");
            if (ss.length != 2 || ss[1].trim().length() != 1) throw new ParseException("", 0);
            boolean onBuffer = ss[0].endsWith("1");
            return new ThirdTapeAdapter(new Write(commandPointer, ss[1].charAt(0)), onBuffer);
        }
        if (line.startsWith("return ") || line.equals("return")) {
            char c = '_';
            if (!line.equals("return")) {
                String ss[] = line.split(" ");
                if (ss.length != 2 || ss[1].trim().length() != 1) throw new ParseException(line, 0);
                c = ss[1].charAt(0);
            }
            return new ThirdTapeAdapter(new Return(commandPointer, c), false);
        }
        if (line.equals("continue")) {
            return new ThirdTapeAdapter(new Continue(commandPointer, cyclePointer), false);
        }
        if (line.equals("break")) {
            return new ThirdTapeAdapter(new Break(commandPointer, cyclePointer), false);
        }
        if (line.endsWith(":")) {
            if (line.contains(" ") || line.contains("\t")) throw new ParseException("", 0);
            return new ThirdTapeAdapter(new Label(commandPointer, line
                    .substring(0, line.length() - 1)), false);
        }
        if (line.startsWith("goto ")) {
            String ss[] = line.split(" ");
            if (ss.length != 2) throw new ParseException(line, ss[0].length());
            return new ThirdTapeAdapter(new Goto(commandPointer, ss[1]), false);
        }
        if (functionConformity.containsKey(line)) {
            return new ThirdTapeAdapter(
                    new FunctionCall(functionConformity.get(line), commandPointer), false);
        }
        throw new ParseException(line, 0);
    }

    private LinkedList<ThirdTapeAdapter> readCommands(int depth, Cycle cyclePointer) throws ParseException {
        LinkedList<ThirdTapeAdapter> res = new LinkedList<>();
        do {
            ThirdTapeAdapter command = parseCommand(depth, cyclePointer);
            if (!res.isEmpty()) res.getLast().setLinkTo(command);
            res.add(command);
        } while (!lines.isEmpty() && depth(lines.peek()) == depth);
        return res;
    }
}