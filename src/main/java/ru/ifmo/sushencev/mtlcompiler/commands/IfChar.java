package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class IfChar extends AbstractIf {
    private char c;

    public IfChar(int line, char c, LinkedList<? extends Command> thenSequence, LinkedList<? extends Command> elseSequence) {
        super(line, thenSequence, elseSequence);
        this.c = c;
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String.format("%s %c _ -> %s %c ^ _ ^", stateName, c, thenSequence.getFirst().stateName, c));
        if (elseSequence != null) {
            res.add(String
                    .format("%s !%c _ -> %s * ^ _ ^", stateName, c, elseSequence.getFirst().stateName));
            elseSequence.forEach(c -> res.addAll(c.genPreCode()));
        } else {
            res.add(String.format("%s !%c _ -> %s * ^ _ ^", stateName, c, link));
        }
        thenSequence.forEach(c -> res.addAll(c.genPreCode()));
        return res;
    }
}
