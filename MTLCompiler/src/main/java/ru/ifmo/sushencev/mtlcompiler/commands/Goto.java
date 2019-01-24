package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jackson on 19.12.2016.
 */
public class Goto extends LineableCommand {
    private Label label;

    public Goto(int line, String name) {
        super(line);
        label = Label.decoder.get(name);
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s * ^ _ ^", stateName, label.stateName));
    }
}
