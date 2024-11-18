package hfr.flaie;

import com.github.mizosoft.methanol.MultipartBodyPublisher;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static javax.swing.SwingUtilities.invokeLater;

class MainWindow {
    private final JFrame frame;
    private final ImageIcon rfBw = imageIcon("/redfacebw.png");
    private final ImageIcon rf = imageIcon("/redface.png");
    private final JLabel imageLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel statusLabel = new JLabel("Envoyer vers Rehost", JLabel.CENTER);

    public MainWindow() {
        this.frame = createFrame();
        this.addClipboardListener(this.frame);
    }

    public static java.util.List<String> fetchCookie(String urlString, String email, String password) throws Exception {
        HttpRequest getTokensRequest = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .GET()
                .build();

        String loginToken = null;
        String loginCookie;
        var client = HttpClient.newHttpClient();
        HttpResponse<String> foo = client.send(getTokensRequest, HttpResponse.BodyHandlers.ofString());

        String response = foo.body();
        loginCookie = foo.headers().allValues("Set-Cookie").stream()
                .filter(e -> e.contains("__RequestVerificationToken"))
                .findFirst().get();

        String regex = "<input[^>]*name=[\"']__RequestVerificationToken[\"'][^>]*value=[\"']([^\"']+)[\"']";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            loginToken = matcher.group(1);
        }

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .header("Cookie", loginCookie)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "__RequestVerificationToken=" + loginToken + "&Email=" + encode(email, UTF_8) + "&Password=" + encode(password, UTF_8) + "&RememberMe=false"
                ))
                .build();

        client = HttpClient.newHttpClient();
        return client.send(loginRequest, HttpResponse.BodyHandlers.ofString())
                .headers().allValues("Set-Cookie");
    }

    public void show() {
        frame.setVisible(true);
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("Send to Rehost");
        frame.setSize(300, 300);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(createContentPanel());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        int taskBarHeight = screenInsets.bottom;
        int x = screenSize.width - frame.getWidth();
        int y = screenSize.height - frame.getHeight() - taskBarHeight;
        frame.setLocation(x, y);

        return frame;
    }

    public void setBlackAndWhiteFace() {
        invokeLater(() -> {
            imageLabel.setIcon(rfBw);
        });
    }

    public void setRedFace() {
        invokeLater(() -> {
            imageLabel.setIcon(rf);
        });
    }

    private ImageIcon imageIcon(String path) {
        try {
            BufferedImage img = ImageIO.read(requireNonNull(this.getClass().getResource(path)));
            return new ImageIcon(img.getScaledInstance(250, 250, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));

        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setBlackAndWhiteFace();

        panel.add(statusLabel, BorderLayout.PAGE_START);
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.PAGE_END);

        addDragAndDropSupport(panel);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem configItem = new JMenuItem("Configuration");
        JMenuItem quitItem = new JMenuItem("Quitter");

        configItem.addActionListener(e -> {
            JPanel configPanel = new SettingsPanel(frame, panel).getPanel();
            frame.setContentPane(configPanel);
            frame.revalidate();
            frame.repaint();
        });

        quitItem.addActionListener(e -> System.exit(0));

        popupMenu.add(configItem);
        popupMenu.add(quitItem);

        panel.setComponentPopupMenu(popupMenu);

        return panel;
    }

    private boolean isImageSupported(File file) {
        String path = file.getAbsolutePath();
        String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
        return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif");
    }

    @SuppressWarnings("unchecked")
    private java.util.List<File> droppedFiles(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        return ((java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))
                .stream().filter(this::isImageSupported).collect(Collectors.toList());
    }

    private void addDragAndDropSupport(JPanel panel) {
        new DropTarget(panel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> files = droppedFiles(dtde);

                    new Thread(() -> handleFiles(files)).start();
                } catch (Exception e) {
                    System.err.println("Cannot handle files");
                }
            }
        });
    }

    java.util.List<String> login() {
        try {
            return fetchCookie("https://rehost.diberie.com/Account/Login", ConfigManager.getEmail(), ConfigManager.getPassword());
        } catch (Exception e) {
            return java.util.List.of();
        }
    }

    public void sendFiles(java.util.List<File> files, java.util.List<String> cookies) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI("https://rehost.diberie.com/Host/UploadFiles?SelectedAlbumId=0&PrivateMode=false&SendMail=false&KeepTags=&Comment=&SelectedExpiryType=0"));
        for (String cookie : cookies) {
            builder.header("Cookie", cookie);
        }
        var multipartBody = MultipartBodyPublisher.newBuilder();
        for (File file : files) {
            multipartBody.filePart(file.getName(), file.toPath());
        }

        var body = multipartBody.build();
        var request = builder
                .header("Content-Type", "multipart/form-data; boundary=" + body.boundary())
                .POST(body)
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> r = client.send(request, HttpResponse.BodyHandlers.ofString());
        var uploadResponse = r.body();
        if (uploadResponse.startsWith("{")) {
            var json = new Gson().fromJson(uploadResponse, UploadResponse.class);

            String output = "";
            if (!json.isMultiple()) {
                if (json.isGIF) {
                    output = json.toString(ConfigManager.getFormat());
                } else {
                    output = json.toString(ConfigManager.getFormat());
                }
            } else {
                output = json.getMultipleResults().stream()
                        .map(j -> j.toString(ConfigManager.getFormat()))
                        .collect(Collectors.joining("\n"));
            }

            StringSelection stringSelection = new StringSelection(output);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            System.out.println(output);
        } else {
            System.err.println("Response doesn't look like json:\n" + uploadResponse);
        }
    }

    private void handleFiles(java.util.List<File> files) {
        java.util.List<String> cookies = ConfigManager.isConnected() ? login() : List.of();

        invokeLater(() -> {
            setRedFace();
            progressBar.setVisible(true);
            progressBar.setValue(0);
            statusLabel.setText("Envoi en cours...");
        });

        try {
            sendFiles(files, cookies);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            invokeLater(() -> progressBar.setValue(100));
        }

        invokeLater(() -> {
            statusLabel.setText("Envoyer vers Rehost");
            setBlackAndWhiteFace();
            progressBar.setVisible(false);
            progressBar.setValue(0);
        });
    }

    private void addClipboardListener(JFrame frame) {
        KeyStroke ctrlV = KeyStroke.getKeyStroke("ctrl V");

        // Add an action for Ctrl+V
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        inputMap.put(ctrlV, "paste");
        actionMap.put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClipboardContent();
            }
        });
    }

    private void handleClipboardContent() {
        // Access the clipboard
        Transferable clipboardContent = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (clipboardContent != null) {
            try {
                // Check if the clipboard contains plain text
                if (clipboardContent.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    BufferedImage clip = (BufferedImage) clipboardContent.getTransferData(DataFlavor.imageFlavor);

                    // Save to a temporary file
                    File tempFile = File.createTempFile("clipboard_", ".png");
                    System.out.println(tempFile.getAbsolutePath());
                    ImageIO.write(clip, "png", tempFile);

                    System.out.println("Sending image from clipboard");
                    new Thread(() -> handleFiles(List.of(tempFile))).start();
                } else if (clipboardContent.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> clip = (List<File>) clipboardContent.getTransferData(DataFlavor.javaFileListFlavor);
                    List<File> supportedFiles = clip.stream().filter(this::isImageSupported).collect(Collectors.toList());

                    System.out.println("Sending images from files in clipboard");
                    new Thread(() -> handleFiles(supportedFiles)).start();
                } else {
                    System.out.println("Clipboard does not contain image.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Clipboard is empty.");
        }
    }
}
