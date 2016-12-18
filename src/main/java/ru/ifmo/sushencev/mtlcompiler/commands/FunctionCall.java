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
    private char returnAbbrevation;

    private static String returnAbbrevationAlphabet = "_";
    private static char freeAbbrevation = 'a';
    public static Map<Character, FunctionCall> decoder = new HashMap<>();

    public static String getReturnAbbrevationAlphabet() {
        return returnAbbrevationAlphabet;
    }

    public FunctionCall(int definitionPointer, int callPointer) {
        super(callPointer);
        this.definitionPointer = definitionPointer;
        returnAbbrevation = freeAbbrevation++;
        returnAbbrevationAlphabet += returnAbbrevation;
        decoder.put(returnAbbrevation, this);
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String
                .format("%s * _ -> line%d * ^ %c >", stateName, definitionPointer, returnAbbrevation));
    }
}
