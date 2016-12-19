package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jackson on 18.12.2016.
 */
public class FunctionCall extends LineableCommand {
    private int definitionPointer;

    public static Map<String, Command> decoder = new HashMap<>();

    public FunctionCall(int definitionPointer, int callPointer) {
        super(callPointer);
        this.definitionPointer = definitionPointer;
        decoder.put(stateName, this);
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String
                .format("%s * _ -> line%d * ^ %s >", stateName, definitionPointer, stateName));
    }
}
