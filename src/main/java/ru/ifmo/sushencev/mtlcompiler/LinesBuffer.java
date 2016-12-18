package ru.ifmo.sushencev.mtlcompiler;

import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class LinesBuffer {
    private List<String> lines;
    private int pointer;

    public LinesBuffer(List<String> lines) {
        this.lines = lines;
        pointer = 0;
        skip();
    }

    public String get() {
        String res = peek();
        //System.out.printf("LinesBuffer.java: %3d %s\n", line(), res);
        pointer++;
        skip();
        return res;
    }

    public String peek() {
        return removeComments(lines.get(pointer));
    }

    private String removeComments(String s) {
        if (!s.contains("//")) return s;
        return s.substring(0, s.indexOf("//"));
    }

    private void skip() {
        while (!isEmpty() && peek().trim().isEmpty()) pointer++;
    }

    public int line() {
        return pointer + 1;
    }

    public boolean isEmpty() {
        return pointer == lines.size();
    }
}
