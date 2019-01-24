import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.GroupLayout;
/*
 * Created by JFormDesigner on Mon Dec 19 17:56:43 MSK 2016
 */



/**
 * @author Sushencev Mikhailovich
 */
public class MainWindow extends JFrame {
    public static void main(String[] args) {
        new MainWindow().setVisible(true);
    }

    public MainWindow() {
        initComponents();
    }

    private java.util.List<String> lines;
    private String currentState = "S";

    private void loadProgram() {
        try {
            lines = Util.getLines("M.out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findNextCommand() {
        String firstPart = String.format("%s %s %s %s -> ",
                currentState, tape1.getCurrentChar(), tape2.getCurrentChar(), tape3.getCurrentChar());
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            if (s.startsWith(firstPart)) {
                mtlLineLabel.setText("mtlLine: " + s);
                return s;
            }
        }
        JOptionPane.showMessageDialog(null, "Can't find where to jump");
        return null;
    }

    private void executeCommand(String s) {
        if (s == null) return;
        //line4 0 _ 0 -> line180 0 ^ line4 > 0 ^
        String ss[] = s.split(" ");

        String toState = ss[5];

        executeSubCommand(s, 0);
        executeSubCommand(s, 1);
        executeSubCommand(s, 2);

        currentState = toState;
        stateLabel.setText("State " + currentState);
    }

    private int stepNumber = 0;

    private void reset() {
        tape1.reset();
        tape2.reset();
        tape3.reset();
        currentState = "S";
        stateLabel.setText("State S");
        mtlLineLabel.setText("mtlLine: (nope)");
        stepNumber = 0;
    }

    private void doNSteps(int n) {
        for (int i = 0; i < n; i++) {
            doStep();
        }
    }

    private void doStep() {
        executeCommand(findNextCommand());
        stepNumber++;
    }

    private void executeSubCommand(String s, int m) {
        String ss[] = s.split(" ");
        String c = ss[6 + m * 2];
        char dir = ss[7 + m * 2].charAt(0);
        if (m == 0) tape1.doAction(c, TapeTable.Direction.charToDirection(dir));
        if (m == 1) tape2.doAction(c, TapeTable.Direction.charToDirection(dir));
        if (m == 2) tape3.doAction(c, TapeTable.Direction.charToDirection(dir));
    }

    private void createUIComponents() {
        tape1 = new TapeTable(18, "1 1 0 | 1 1 | 0 | 1 0".split(" "));
        tape2 = new TapeTable(18, new String[]{});
        tape3 = new TapeTable(18, new String[]{});

        nextButton = new JButton();
        prevButton = new JButton();
        resetButton = new JButton();
        jumpToLineButton = new JButton();
        runButton = new JButton();

        nextButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doStep();
            }
        });
        prevButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int temp = stepNumber;
                reset();
                doNSteps(temp - 1);
            }
        });
        resetButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        jumpToLineButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (!currentState.equals("line" + textField1.getText())) {
                    doStep();
                }
            }
        });
        runButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (!currentState.equals("AC")) {
                    doStep();
                }
            }
        });
    }

    private void afterCreateUI() {
        tape1.setRowHeight(tape1.getHeight());
        tape2.setRowHeight(tape2.getHeight());
        tape3.setRowHeight(tape3.getHeight());
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loadProgram();

        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Sushencev Mikhailovich
        createUIComponents();

        stateLabel = new JLabel();
        mtlLineLabel = new JLabel();
        textField1 = new JTextField();

        //======== this ========
        Container contentPane = getContentPane();

        //---- nextButton ----
        nextButton.setText("Next");

        //---- prevButton ----
        prevButton.setText("Prev");

        //---- stateLabel ----
        stateLabel.setText("State: S");

        //---- resetButton ----
        resetButton.setText("Reset");

        //---- mtlLineLabel ----
        mtlLineLabel.setText("mtlLine: 0");

        //---- gotoLineButton ----
        jumpToLineButton.setText("jump to line");

        //---- runButton ----
        runButton.setText("run");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(tape1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(stateLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                            .addComponent(mtlLineLabel)
                            .addGap(69, 69, 69)
                            .addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(prevButton, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(nextButton, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE))
                        .addComponent(tape2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tape3, GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(runButton, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jumpToLineButton, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(nextButton)
                            .addComponent(prevButton)
                            .addComponent(resetButton))
                        .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(stateLabel)
                            .addComponent(mtlLineLabel)))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tape1, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tape2, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tape3, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jumpToLineButton)
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(runButton))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        afterCreateUI();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Sushencev Mikhailovich
    private TapeTable tape1;
    private TapeTable tape2;
    private TapeTable tape3;
    private JButton nextButton;
    private JButton prevButton;
    private JLabel stateLabel;
    private JButton resetButton;
    private JLabel mtlLineLabel;
    private JTextField textField1;
    private JButton jumpToLineButton;
    private JButton runButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
