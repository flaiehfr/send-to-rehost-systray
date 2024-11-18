package hfr.flaie;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel {
    private final JPanel panel;
    private final JCheckBox connectedCheckbox;
    private final JTextField email;
    private final JLabel emailLabel;
    private final JPasswordField password;
    private final JLabel passwordLabel;
    private final JComboBox<String> options;

    public SettingsPanel(JFrame frame, JPanel mainPanel) {
        connectedCheckbox = new JCheckBox("Utilisateur connecté");
        email = new JTextField();
        email.setText(ConfigManager.getEmail());
        password = new JPasswordField();
        password.setText(ConfigManager.getPassword());
        options = new JComboBox<>(new String[]{
                "URL de l'image pleine",
                "BBCode de l'image pleine",
                "URL de l'image réduite",
                "BBCode de l'image réduite avec lien",
                "URL de l'image miniature",
                "BBCode de l'image miniature",
                "BBCode de l'image miniature avec lien"
        });
        options.setSelectedItem(ConfigManager.getFormat());
        JButton okButton = new JButton("OK");

        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Config label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        var configLabel = new JLabel("Configuration");
        configLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(configLabel, gbc);

        // CheckBox
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(connectedCheckbox, gbc);

        // Email Label and TextField
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        emailLabel = new JLabel("Email : ");
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(email, gbc);

        // Password Label and TextField
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        passwordLabel = new JLabel("Password : ");
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(password, gbc);

        // Format Label and ComboBox
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Format :"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(options, gbc);

        // OK Button
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(okButton, gbc);

        // Spacer
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        emailLabel.setVisible(false);
        passwordLabel.setVisible(false);
        email.setVisible(false);
        password.setVisible(false);

        connectedCheckbox.addActionListener(e -> {
            onConnectedCheckbox();
        });

        connectedCheckbox.setSelected(ConfigManager.isConnected());
        if (connectedCheckbox.isSelected()) {
            onConnectedCheckbox();
        }

        okButton.addActionListener(e -> {
            ConfigManager.saveConfig(
                    connectedCheckbox.isSelected(),
                    email.getText(),
                    new String(password.getPassword()),
                    (String) options.getSelectedItem()
            );

            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    void onConnectedCheckbox() {
        boolean isConnected = connectedCheckbox.isSelected();
        emailLabel.setVisible(isConnected);
        email.setVisible(isConnected);
        passwordLabel.setVisible(isConnected);
        password.setVisible(isConnected);
        panel.revalidate();
        panel.repaint();
    }
}
