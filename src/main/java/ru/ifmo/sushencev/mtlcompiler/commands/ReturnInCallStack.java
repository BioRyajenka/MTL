package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public final class ReturnInCallStack extends Command {
    private static ReturnInCallStack instance = new ReturnInCallStack();

    public static ReturnInCallStack getInstance() {
        return instance;
    }

    private ReturnInCallStack() {
        super("return");
    }

    @Override
    public List<String> genPreCode() {
        List<String> res = new LinkedList<>();
        res.add(String.format("%s * _ -> %s * ^ _ <", stateName, stateName));
        FunctionCall.decoder.entrySet().forEach(e -> {
            res.add(String
                    .format("%s * %c -> %s * ^ _ ^", stateName, e.getKey(), e.getValue().link));
        });
        return res;
    }
}
