package gui;

import text2gif.GifWriterUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;

/**
 * Main dialog form class
 */
public class MainDialog extends JDialog {
    /**
     * Content pane
     */
    private JPanel contentPane;
    /**
     * Button
     */
    private JButton buttonConvert;
    private JTextArea text;
    private JScrollPane scroll;
    private JLabel statusLabel;
    JMenuBar menuBar;

    /**
     * Main dialog constructor
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public MainDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonConvert);

        buttonConvert.addActionListener(e -> onConvert());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Create UI components
        createUIComponents();
    }

    /**
     * Converting event handler
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onConvert() {
        try {
            // Choose file to save
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            fileChooser.setFileFilter(new FileNameExtensionFilter("GIF images", "*.gif"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Convert file
                String fileName = fileChooser.getSelectedFile().getPath();
                GifWriterUtil.writeString(text.getText(), fileName);
                statusLabel.setText("Converted");
            }
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
        }
    }

    /**
     * Cancel event handler
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void onCancel() {
        dispose();
    }

    /**
     * Create Menu
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void createMenu() {
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;
        //Create the menu bar.
        menuBar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "Save, open and create files");
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("A text-only menu item",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);

        menuItem = new JMenuItem("Both text and icon",
                new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);

        menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menu.add(menuItem);

//a group of radio button menu items
        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

//a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);

//a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);

//Build second menu in the menu bar.
        menu = new JMenu("Another Menu");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        setJMenuBar(menuBar);
    }

    /**
     * Creates UI components
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private void createUIComponents() {
        // TODO: Create menu
        // createMenu();
        setTitle("text2gif");
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setSize(720, 480);
        dialog.setVisible(true);
        System.exit(0);
    }
}
