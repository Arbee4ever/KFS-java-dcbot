package com.KingFightStudios.KFS_Bot;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowControls implements WindowListener {
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(Bot.bot != null) {
            Gui.botclass.shutdown();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if(Bot.bot != null) {
            Gui.botclass.shutdown();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
