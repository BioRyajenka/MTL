package ru.ifmo.sushencev.mtlcompiler.commands;

/**
 * Created by Jackson on 18.12.2016.
 */
public abstract class LineableCommand extends Command {
    protected LineableCommand(int line) {
        super("line" + line);
    }
}
