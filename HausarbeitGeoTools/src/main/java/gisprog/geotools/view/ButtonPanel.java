package gisprog.geotools.view;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.*;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {

    public ButtonPanel(HGTWindow window, JMapPane mapPane) {
        FlowLayout layout = (FlowLayout) getLayout();
        layout.setAlignment(FlowLayout.RIGHT);

        JButton tableButton = new JButton("open table...");
        tableButton.addActionListener(e -> new AttributeTable(window.getFctm()));
        add(tableButton);
        JButton zoomInBtn = new JButton(new ZoomInAction(mapPane));
        add(zoomInBtn);
        JButton zoomOutBtn = new JButton(new ZoomOutAction(mapPane));
        add(zoomOutBtn);
        JButton resetBtn = new JButton(new ResetAction(mapPane));
        add(resetBtn);
        JButton panBtn = new JButton(new PanAction(mapPane));
        add(panBtn);
        JButton infoBtn = new JButton(new InfoAction(mapPane));
        add(infoBtn);
        JButton noToolBtn = new JButton(new NoToolAction(mapPane));
        add(noToolBtn);
    }
}
