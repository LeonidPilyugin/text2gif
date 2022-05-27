package gui;

import text2gif.GifWriterUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Main dialog form class
 */
public class MainDialog extends JDialog {
    /**
     * Read and write buffer
     */
    private static final int BUFFER_SIZE = 1024;
    /**
     * Is true when file is saved
     */
    private boolean isSaved;
    /**
     * Path to current file
     */
    private Path filePath;
    /**
     * Content pane
     */
    private JPanel contentPane;
    /**
     * Button
     */
    private JButton buttonConvert;
    /**
     * Text field
     */
    private JTextArea text;
    private JScrollPane scroll;
    private JLabel statusLabel;
    JMenuBar menuBar;

    /**
     * Main dialog constructor
     *
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public MainDialog() {
        // Set variables
        isSaved = true;
        filePath = null;
        // Do nothing at close
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // Set content pane
        setContentPane(contentPane);
        // Create window modal
        setModal(true);
        // Set default button
        getRootPane().setDefaultButton(buttonConvert);
        // Add convert button click listener
        buttonConvert.addActionListener(this::onConvert);

        // Add window closing event listener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel(e);
            }
        });
        // Add text changed document listener: if something changes, isChanged is setting to false
        text.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                isSaved = false;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                isSaved = false;
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                isSaved = false;
            }
        });

        // Call onCancel() on escape
        contentPane.registerKeyboardAction(this::onExit, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Create UI components
        createUIComponents();
    }


    /**
     * Creates UI components
     *
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void createUIComponents() {
        // Create menu
        createMenu();
        // Set title
        setTitle("text2gif");
    }

    /**
     * Create Menu
     *
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void createMenu() {
        JMenu fileMenu;
        //Create the menu bar
        menuBar = new JMenuBar();

        // File menu
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Save, open and create files");
        menuBar.add(fileMenu);

        // New menu item
        JMenuItem newItem = new JMenuItem("New",
                KeyEvent.VK_N);
        newItem.getAccessibleContext().setAccessibleDescription(
                "Create new file");
        newItem.addActionListener(this::onNew);
        fileMenu.add(newItem);

        // Open menu item
        JMenuItem openItem = new JMenuItem("Open",
                KeyEvent.VK_O);
        openItem.getAccessibleContext().setAccessibleDescription(
                "Open file");
        openItem.addActionListener(this::onOpen);
        fileMenu.add(openItem);

        // Save menu item
        JMenuItem saveItem = new JMenuItem("Save",
                KeyEvent.VK_S);
        saveItem.getAccessibleContext().setAccessibleDescription(
                "Save file");
        saveItem.addActionListener(this::onSave);
        fileMenu.add(saveItem);

        // Save As menu item
        JMenuItem saveAsItem = new JMenuItem("Save As",
                KeyEvent.VK_A);
        saveAsItem.getAccessibleContext().setAccessibleDescription(
                "Save new file");
        saveAsItem.addActionListener(this::onSaveAs);
        fileMenu.add(saveAsItem);

        // Convert menu item
        JMenuItem convertItem = new JMenuItem("Convert",
                KeyEvent.VK_C);
        convertItem.getAccessibleContext().setAccessibleDescription(
                "Convert file");
        fileMenu.add(convertItem);

        // Exit menu item
        JMenuItem exitItem = new JMenuItem("Exit",
                KeyEvent.VK_E);
        exitItem.getAccessibleContext().setAccessibleDescription(
                "Exit");
        exitItem.addActionListener(this::onExit);
        fileMenu.add(exitItem);

        // Set menu bar
        setJMenuBar(menuBar);
    }

    /**
     * Converting event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onConvert(ActionEvent e) {
        try {
            // Choose file to save
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            fileChooser.setFileFilter(new FileNameExtensionFilter("GIF images", "gif"));
            int userSelection = fileChooser.showSaveDialog(this);

            // If user chose file, convert
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Convert file
                String fileName = fileChooser.getSelectedFile().getPath();
                GifWriterUtil.writeString(text.getText(), fileName);
                statusLabel.setText("Converted");
            }
        } catch (Exception x) {
            // If something wrong, show error message
            JOptionPane.showMessageDialog(this, String.format("Unknown exception: %s%n", x),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cancel event handler
     *
     * @param e WindowEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onCancel(WindowEvent e) {
        // Suggest to save file
        if (!isSaved) {
            switch (showUnsavedWarning()) {
                // Save file and close
                case JOptionPane.YES_OPTION:
                    if (save()) {
                        dispose();
                        break;
                    } else return;
                // Close without saving
                case JOptionPane.NO_OPTION:
                    dispose();
                    break;
                // Cancel
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        } else {
            // Close
            dispose();
        }
    }

    /**
     * Creating new file event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onNew(ActionEvent e) {
        // Suggest to save file
        if (!isSaved) {
            switch (showUnsavedWarning()) {
                // Save file and create new
                case JOptionPane.YES_OPTION:
                    // If file saved, continue
                    if (save()) break;
                    // If not, cancel
                    else return;
                // Continue without saving
                case JOptionPane.NO_OPTION:
                    break;
                // Cancel
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }
        // Clear
        isSaved = false;
        text.setText("");
    }

    /**
     * Opening file event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onOpen(ActionEvent e) {
        // Suggest to save file
        if (!isSaved) {
            switch (showUnsavedWarning()) {
                // Save file
                case JOptionPane.YES_OPTION:
                    // If saved, continue
                    if (save()) break;
                    // If not, cancel
                    else return;
                // Continue without saving
                case JOptionPane.NO_OPTION:
                    break;
                // Cancel
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }

        // Choose file to open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to open");
        fileChooser.setFileFilter(new FileNameExtensionFilter("text files", "txt"));
        int userSelection = fileChooser.showOpenDialog(this);

        // If file chosen, show it
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Clear
            text.setText("");
            // Read file
            try (InputStream in = Files.newInputStream(fileChooser.getSelectedFile().toPath());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                // Reading buffer
                char[] buffer = new char[BUFFER_SIZE];
                // Read first chunk
                int read = reader.read(buffer, 0, BUFFER_SIZE);
                do {
                    // Add text to window
                    text.append(new String(buffer, 0, read));
                    // Read new chunk
                } while ((read = reader.read(buffer, 0, BUFFER_SIZE)) == BUFFER_SIZE);
                // Set file unchanged
                isSaved = true;
            } catch (Exception x) {
                // If something wrong, show error message
                JOptionPane.showMessageDialog(this, String.format("Unknown exception: %s%n", x),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Save file event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onSave(ActionEvent e) {
        // Save
        save();
    }

    /**
     * Save As file event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onSaveAs(ActionEvent e) {
        // Remember current file
        Path path = filePath;
        // Reset current file
        filePath = null;
        // If saving isn't successful, set previous file
        if (!save()) filePath = path;
    }

    /**
     * Exiting event handler
     *
     * @param e ActionEvent object
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onExit(ActionEvent e) {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Saving function
     *
     * @return true if file has saved, otherwise false
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private boolean save() {
        // If current path is null, choose file
        if (filePath == null) {
            // Show file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            fileChooser.setFileFilter(new FileNameExtensionFilter("text files", "txt"));
            int userSelection = fileChooser.showSaveDialog(this);

            // If file chosen, set new file path otherwise return
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().toPath();
            } else {
                return false;
            }
        }

        // Write text to file
        try (OutputStream out = Files.newOutputStream(filePath);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
            // Write all text
            writer.write(text.getText());
            // Set unchanged
            isSaved = true;
            // Set new title
            setTitle(filePath.getFileName().toString());
        } catch (Exception x) {
            // If something wrong, show error message
            JOptionPane.showMessageDialog(this, String.format("Unknown exception: %s%n", x),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Return result of saving
        return isSaved;
    }

    /**
     * Function showing dialog suggesting file saving
     *
     * @return an int indicating the option selected by the user
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private int showUnsavedWarning() {
        // Show dialog
        return JOptionPane.showConfirmDialog(this, "File isn't saved, save it?",
                "Unsaved warning", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    /**
     * Main function
     *
     * @param args starting args
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setSize(720, 480);
        dialog.setVisible(true);
        System.exit(0);
    }
}
