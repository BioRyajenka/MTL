package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class Continue extends LineableCommand {
    private Command cycleCommand;

    public Continue(int line, Command cycleCommand) {
        super(line);
        this.cycleCommand = cycleCommand;
    }

    @Override
    public List<String> genPreCode() {
        return Collections
                .singletonList(String.format("%s * _ -> %s * ^ _ ^", stateName, cycleCommand.stateName));
    }
}
