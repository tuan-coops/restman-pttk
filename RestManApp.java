package com.restman;

import com.restman.ui.MainFrm;
import javax.swing.SwingUtilities;

public final class RestManApp {
    private RestManApp() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrm().setVisible(true);
        });
    }
}
