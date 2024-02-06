package gisprog.geotools.view;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class AttributeTable extends JFrame {
    private JTable attrTable;

    public AttributeTable(TableModel model) {
        setPreferredSize(new Dimension(400, 300));
        getContentPane().setLayout(new BorderLayout(0, 0));

        attrTable = new JTable(model);

        JScrollPane tableScrollPane = new JScrollPane(attrTable);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }
}
