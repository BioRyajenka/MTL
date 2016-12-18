package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class MoveCursor extends LineableCommand {
    public enum Direction {
        LEFT, RIGHT
    }

    private Direction direction;

    public MoveCursor(int line, Direction direction) {
        super(line);
        this.direction = direction;
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s * %c _ ^", stateName, link,
                direction == Direction.LEFT ? '<' : '>'));
    }
}
