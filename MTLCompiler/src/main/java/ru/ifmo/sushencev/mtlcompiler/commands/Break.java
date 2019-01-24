package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jackson on 19.12.2016.
 */
public class Break extends LineableCommand {
    private Cycle cycle;

    public Break(int line, Cycle cycle) {
        super(line);
        this.cycle = cycle;
    }

    @Override
    public void setLink(String link) {

    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s * ^ _ ^", stateName, cycle.link));
    }
}
