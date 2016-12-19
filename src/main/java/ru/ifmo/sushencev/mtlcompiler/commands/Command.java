package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.List;

/**
 * Created by Jackson on 18.12.2016.
 */
public abstract class Command {
    protected Command(String stateName) {
        this.stateName = stateName;
    }

    protected String stateName;
    protected String link;

    public final void setLinkTo(Command linkTo) {
        setLink(linkTo.stateName);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public abstract List<String> genPreCode();
}