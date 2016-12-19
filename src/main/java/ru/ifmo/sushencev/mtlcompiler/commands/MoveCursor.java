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

    public MoveCursor(int line, Direction direction, int times) {
        super(line);
        if (times != 1) throw new RuntimeException("move cursor times should be equals to 1");
        this.direction = direction;
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> %s * %c _ ^", stateName, link,
                direction == Direction.LEFT ? '<' : '>'));
    }
}
