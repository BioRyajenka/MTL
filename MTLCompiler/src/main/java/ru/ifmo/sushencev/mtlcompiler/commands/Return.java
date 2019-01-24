package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class Return extends LineableCommand {
    private char returnValue;

    /**
     * @param returnValue _ if void
     */
    public Return(int line, char returnValue) {
        super(line);
        this.returnValue = returnValue;
    }

    private static Return autoReturn = new Return(0, '_');

    public static Command getAutoReturn() {
        return autoReturn;
    }

    @Override
    public void setLink(String link) {
        //throw new RuntimeException("Can't set link on return!");
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String.format("%s * _ -> %s * ^ _ <", stateName, stateName));
        FunctionCall.decoder.entrySet().forEach(e -> {
            String whereToGo = null;
            if (e.getValue() instanceof FunctionCall) {
                whereToGo = e.getValue().link;
            } else if (e.getValue() instanceof If) {
                whereToGo = e.getValue().stateName;
            }
            res.add(String
                    .format("%s * %s -> %s * ^ %c ^", stateName, e.getKey(), whereToGo, returnValue));
        });
        return res;
    }
}
