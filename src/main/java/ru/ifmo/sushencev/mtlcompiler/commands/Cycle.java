package ru.ifmo.sushencev.mtlcompiler.commands;

import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class Cycle extends LineableCommand {
    private LinkedList<Command> subCommands = new LinkedList<>();

    public Cycle(int line) {
        super(line);
    }

    public void appendSubSequence(Command command) {
        command.setLinkTo(this);
        if (subCommands.isEmpty()) {
            subCommands.add(command);
            return;
        }
        subCommands.getLast().setLinkTo(command);
        subCommands.add(command);
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String.format("%s * _ -> %s * ^ _ ^", stateName, subCommands.getFirst().stateName));
        subCommands.forEach(c -> res.addAll(c.genPreCode()));
        return res;
    }
}
