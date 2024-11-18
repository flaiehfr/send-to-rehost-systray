package hfr.flaie;

import static javax.swing.SwingUtilities.invokeLater;

public class SendToRehost {
    public static void main(String[] args) {
        invokeLater(() -> {
            LookAndFeelManager.applySystemLookAndFeel();
            new ApplicationManager().start();
        });
    }
}

