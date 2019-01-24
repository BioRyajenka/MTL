package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jackson on 19.12.2016.
 */
public class Label extends LineableCommand {
    public static Map<String, Label> decoder = new HashMap<>();

    public Label(int line, String name) {
        super(line);
        decoder.put(name, this);
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s * ^ _ ^", stateName, link));
    }
}
