package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.LinkedList;

/**
 * Created by Jackson on 19.12.2016.
 */
public abstract class AbstractIf extends LineableCommand {
    protected LinkedList<? extends Command> thenSequence;
    protected LinkedList<? extends Command> elseSequence;

    protected AbstractIf(int line, LinkedList<? extends Command> thenSequence, LinkedList<? extends Command> elseSequence) {
        super(line);
        this.thenSequence = thenSequence;
        this.elseSequence = elseSequence;
    }

    @Override
    public void setLink(String link) {
        super.setLink(link);
        thenSequence.getLast().setLink(link);
        if (elseSequence != null) {
            elseSequence.getLast().setLink(link);
        }
    }
}
