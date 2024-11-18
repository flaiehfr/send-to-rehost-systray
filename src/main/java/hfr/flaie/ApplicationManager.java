package hfr.flaie;

import javax.swing.*;
import java.awt.*;

public class ApplicationManager {
    private final SystemTrayManager trayManager;
    private final MainWindow mainWindow;

    public ApplicationManager() {
        this.trayManager = new SystemTrayManager(this);
        this.mainWindow = new MainWindow();
    }

    public void start() {
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "System Tray not supported", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        trayManager.setup();
        System.out.println("Application started!");
    }

    public void showMainWindow() {
        mainWindow.show();
    }
}
