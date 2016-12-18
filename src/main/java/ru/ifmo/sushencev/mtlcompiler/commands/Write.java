package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class Write extends LineableCommand {
    private char c;

    public Write(int line, char c) {
        super(line);
        this.c = c;
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s %c ^ _ ^", stateName, link, c));
    }
}
