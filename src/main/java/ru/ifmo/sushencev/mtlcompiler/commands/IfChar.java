package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class IfChar extends LineableCommand {
    private char c;
    private LinkedList<Command> thenSequence;
    private LinkedList<Command> elseSequence;

    public IfChar(int line, char c, LinkedList<Command> thenSequence, LinkedList<Command> elseSequence) {
        super(line);
        this.c = c;
        this.thenSequence = thenSequence;
        this.elseSequence = elseSequence;
    }

    @Override
    public void setLinkTo(Command linkTo) {
        super.setLinkTo(linkTo);
        thenSequence.getLast().setLinkTo(linkTo);
        if (elseSequence != null) {
            elseSequence.getLast().setLinkTo(linkTo);
        }
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String
                .format("%s %c _ -> %s %c ^ _ ^", stateName, c, thenSequence.getFirst().stateName, c));
        if (elseSequence != null) {
            res.add(String
                    .format("%s !%c _ -> %s * ^ _ ^", stateName, c, elseSequence.getFirst().stateName));
        } else {
            res.add(String.format("%s !%c _ -> %s * ^ _ ^", stateName, c, link));
        }
        thenSequence.forEach(c -> res.addAll(c.genPreCode()));
        if (elseSequence != null) {
            elseSequence.forEach(c -> res.addAll(c.genPreCode()));
        }
        return res;
    }
}
