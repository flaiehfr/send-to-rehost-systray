package hfr.flaie;

import java.awt.*;

import static java.util.Objects.requireNonNull;
import static javax.swing.SwingUtilities.invokeLater;

public class SystemTrayManager {
    private final ApplicationManager appManager;

    public SystemTrayManager(ApplicationManager appManager) {
        this.appManager = appManager;
    }

    public void setup() {
        try {
            TrayIcon trayIcon = new TrayIcon(requireNonNull(createTrayIconImage()));
            trayIcon.setImageAutoSize(true);

            PopupMenu menu = new PopupMenu();
            MenuItem openItem = new MenuItem("Open");
            MenuItem quitItem = new MenuItem("Quit");

            trayIcon.addActionListener(e -> invokeLater(appManager::showMainWindow));
            openItem.addActionListener(e -> invokeLater(appManager::showMainWindow));
            quitItem.addActionListener(e -> System.exit(0));

            menu.add(openItem);
            menu.add(quitItem);
            trayIcon.setPopupMenu(menu);

            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            System.err.println("Error adding to system tray: " + e.getMessage());
        }
    }

    private Image createTrayIconImage() {
        try {
            return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/redface.png"));
        } catch (Exception e) {
            System.err.println("Error loading tray icon image: " + e.getMessage());
            return null;
        }
    }
}
