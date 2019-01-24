import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Jackson on 19.12.2016.
 */
public class TapeTable extends JTable {
    private int size;
    private String[] contents;
    private int cursorPosition;

    private void init() {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.setRowCount(0);
        String[] values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = "_";
        }
        cursorPosition = (size - contents.length) / 2;
        for (int i = 0; i < contents.length; i++) {
            values[cursorPosition + i] = contents[i];
        }
        model.addRow(values);
    }

    TapeTable(int size, String[] contents) {
        this.size = size;
        this.contents = contents;

        String[] columnNames = new String[size];
        for (int i = 0; i < contents.length; i++) {
            columnNames[i] = "" + i;
        }

        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
        setModel(model);

        init();

        getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                Component comp = super
                        .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (col == cursorPosition) {
                    comp.setBackground(Color.GREEN);
                } else {
                    comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        };
        renderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(Object.class, renderer);
    }

    public void reset() {
        init();
    }

    public enum Direction {
        LEFT(-1), RIGHT(1), STAY(0);

        private int modifier;

        Direction(int modifier) {
            this.modifier = modifier;
        }

        public static Direction charToDirection(char c) {
            if (c == '<') return LEFT;
            if (c == '>') return RIGHT;
            return STAY; // '^'
        }
    }

    public String getCurrentChar() {
        DefaultTableModel model = (DefaultTableModel) getModel();
        return (String) model.getValueAt(0, cursorPosition);
    }

    private boolean isTapeClear() {
        DefaultTableModel model = (DefaultTableModel) getModel();
        for (int i = 0; i < size; i++) {
            if (!model.getValueAt(0, i).equals("_")) {
                return false;
            }
        }
        return true;
    }

    public void doAction(String c, Direction dir) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.setValueAt(c, 0, cursorPosition);

        cursorPosition += dir.modifier;

        if (isTapeClear()) cursorPosition = size / 2;

        repaint();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}