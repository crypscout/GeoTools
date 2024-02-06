package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;

import java.awt.event.ActionListener;

public abstract class AbstractAction implements ActionListener {

    protected final HGTWindow window;

    AbstractAction(HGTWindow window) {
        this.window = window;
    }

}
