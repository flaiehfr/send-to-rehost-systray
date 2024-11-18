package hfr.flaie;

import javax.swing.*;

public class LookAndFeelManager {
    public static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error applying system look and feel: " + e.getMessage());
        }
    }
}
