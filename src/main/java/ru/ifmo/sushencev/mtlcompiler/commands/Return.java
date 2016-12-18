package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public class Return extends LineableCommand {
    public Return(int line) {
        super(line);
    }

    @Override
    public void setLinkTo(Command linkTo) {
        //throw new RuntimeException("Can't set link on return!");
    }

    @Override
    public List<String> genPreCode() {
        return Collections.singletonList(String.format("%s * _ -> return * ^ _ ^", stateName));
    }
}
