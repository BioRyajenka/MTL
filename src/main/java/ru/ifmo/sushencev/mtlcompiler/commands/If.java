package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 19.12.2016.
 */
public class If extends AbstractIf {
    private int definitionPointer;

    public If(int line, int definitionPointer, LinkedList<? extends Command> thenSequence, LinkedList<? extends Command> elseSequence) {
        super(line, thenSequence, elseSequence);
        this.definitionPointer = definitionPointer;
        FunctionCall.decoder.put(stateName, this);
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String.format("%s * _ -> line%d * ^ %s >", stateName, definitionPointer, stateName));

        // then
        res.add(String.format("%s * 1 -> %s * ^ _ ^", stateName, thenSequence.getFirst().stateName));
        thenSequence.forEach(c -> res.addAll(c.genPreCode()));
        // else
        if (elseSequence != null) {
            res.add(String.format("%s * 0 -> %s * ^ _ ^", stateName, elseSequence.getFirst().stateName));
            elseSequence.forEach(c -> res.addAll(c.genPreCode()));
        } else {
            res.add(String.format("%s * 0 -> %s * ^ _ ^", stateName, link));
        }
        return res;
    }
}
