package ru.ifmo.sushencev.mtlcompiler.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jackson on 19.12.2016.
 */
public class ThirdTapeAdapter extends Command {
    private Command command;
    private boolean onBuffer;

    public ThirdTapeAdapter(Command command, boolean onBuffer) {
        super(command.stateName);
        this.command = command;
        this.onBuffer = onBuffer;
    }

    @Override
    public void setLink(String link) {
        command.setLink(link);
    }

    private String adapt(String s, boolean onBuffer) {
        // 0  1 2 3  4  5 6 7 8
        // %s * _ -> %s * ^ _ ^
        String ss[] = s.split(" ");
        if (ss.length > 9) return s; // was adapted
        String res;
        if (onBuffer) {
            res = String.format("%s * %s %s -> %s * ^ %s %s %s %s",
                    ss[0], ss[2], ss[1], ss[4], ss[7], ss[8], ss[5], ss[6]);
            System.out.println("ss is " + Arrays.toString(ss));
        } else {
            res = String.format("%s %s %s * -> %s %s %s %s %s * ^",
                    ss[0], ss[1], ss[2], ss[4], ss[5], ss[6], ss[7], ss[8]);
        }
        return res;
    }

    @Override
    public List<String> genPreCode() {
        return command.genPreCode().stream().map(s -> adapt(s, onBuffer)).collect(Collectors.toList());
    }

    public Command getCommand() {
        return command;
    }
}