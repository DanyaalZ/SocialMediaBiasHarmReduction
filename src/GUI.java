import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatLightLaf;

/* this class provides a framework for GUI to be used throughout the application, where we can create windows, buttons, text and more */
public class GUI {

    // use flatlightlaf for modern look and feel - initialised in static so each
    // instance of GUI will have this
    static {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // create message for user
    public JFrame createMessage(String message) {
        // Set font for window (flatlaf)
        UIManager.put("OptionPane.messageFont", new Font("Century Gothic", Font.PLAIN, 14));

        JFrame jframe = new JFrame();
        JOptionPane.showMessageDialog(jframe, message);
        return jframe;
    }

    // set a window to do a system exit when closed
    public void setExitOnClose(JFrame window) {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // create a window with a specified name, not visible until ready for display
    public JFrame createWindow(String name, int width, int height) {
        // create window with given name
        JFrame window = new JFrame(name);

        // set size of the window
        window.setSize(width, height);

        // invisible until showWindow called
        window.setVisible(false);

        // centre it
        window.setLocationRelativeTo(null);

        return window;
    }

    // create panel for the window
    public JPanel createPanel(JFrame window) {
        // null layout so we have absolute positioning
        JPanel hold = new JPanel(null);
        hold.setBackground(Color.LIGHT_GRAY);

        return hold;
    }

    // add text to a window with position and size, as well as font size and the
    // text we want to use
    public JLabel addText(JFrame window, JPanel hold, String text, int x, int y, int fontSize) {
        JLabel textOnPage = new JLabel(text);

        // scaling
        Dimension size = textOnPage.getPreferredSize();

        // we use the font century gothic as it displays well
        textOnPage.setFont(new Font("Century Gothic", Font.BOLD, fontSize));

        // set the position of the JPanel in the JFrame
        textOnPage.setBounds(x, y, size.width * 2, size.height * 2);

        hold.add(textOnPage);

        return textOnPage;
    }

    // add a button to a window with position and size, take function to be
    // executed, colour
    // allows for a specified function (as a Runnable task) to executed as lambda
    // (anonymous function) when button is clicked
    public void addButton(JFrame window, JPanel hold, Runnable onClick, String text, int width, int height, int x,
            int y, Color colour) {
        JButton buttonOnPage = new JButton(text);

        // button colour - light blue (default) if no colour is specified
        if (colour == null) {
            colour = new Color(48, 108, 138);
        }

        // set button colour as specified
        buttonOnPage.setBackground(colour);

        // set button text to white
        buttonOnPage.setForeground(Color.white);

        // set standard button font, bold
        buttonOnPage.setFont(new Font("Century Gothic", Font.BOLD, 14));

        // rounded buttons (all buttons will be rounded) - using flatlightlaf properties
        buttonOnPage.putClientProperty("JButton.buttonType", "roundRect");

        // action listener for event click, take runnable
        buttonOnPage.addActionListener(e -> {
            onClick.run();
        });

        buttonOnPage.setPreferredSize(new Dimension(width, height));
        Dimension size = buttonOnPage.getPreferredSize();

        // set position of the JPanel in the JFrame
        buttonOnPage.setBounds(x, y, size.width * 2, size.height * 2);
        hold.add(buttonOnPage);
    }

    // show window when needed, as we set it to invisible by default
    public JFrame showWindow(JFrame window, JPanel hold) {
        window.add(hold);
        window.setVisible(true);
        window.setResizable(false);
        return window;
    }

    // for scrollable windows using scrollpane instead of jpanel
    public JFrame showScrollableWindow(JFrame window, JScrollPane scroll) {
        window.add(scroll);
        window.setVisible(true);
        window.setResizable(false);
        return window;
    }

    // get string from user
    public String getMessageString(String message) {
        JFrame jframe = new JFrame();
        // Set font for window (flatlaf)
        UIManager.put("OptionPane.messageFont", new Font("Century Gothic", Font.PLAIN, 14));
        String getMessage = JOptionPane.showInputDialog(jframe, message);
        return getMessage;
    }

    // get password from user, displaying password as dots (password field), taking
    // a message as input
    public static String getPasswordString(String message) {
        JFrame jframe = new JFrame();

        // create a password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Century Gothic", Font.PLAIN, 14));

        // create label for password field with same font and size as rest of GUI
        JLabel passwordLabel = new JLabel("Enter password:");
        passwordLabel.setFont(new Font("Century Gothic", Font.PLAIN, 14));

        // create a panel to hold label and password field
        JPanel hold = new JPanel();

        // using box layout so we don't need to set bounds
        hold.setLayout(new BoxLayout(hold, BoxLayout.Y_AXIS));

        // make sure password label shows in the right place
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // add both labels to the panel
        hold.add(passwordLabel);
        hold.add(passwordField);

        // show the JOptionPane with the panel, get the result as an int which will let
        // us determine if OK or CANCEL were pressed
        int option = JOptionPane.showOptionDialog(jframe, hold, message, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null);

        // handle user input (string entered and ok pressed)
        if (option == JOptionPane.OK_OPTION) {
            // get each character from password field as an array,
            char[] password = passwordField.getPassword();
            return new String(password);
        } else {
            // handle cancellation (cancel pressed)
            return null;
        }
    }

    // get a double from user
    public Double getMessageDouble(String message) {
        JFrame jframe = new JFrame();
        // set font for window (flatlaf)
        UIManager.put("OptionPane.messageFont", new Font("Century Gothic", Font.PLAIN, 14));
        String getMessage = JOptionPane.showInputDialog(jframe, message);
        Double getMessageDouble = Double.parseDouble(getMessage);
        return getMessageDouble;
    }

    // get an int from user
    public int getMessageInt(String message) {
        JFrame jframe = new JFrame();
        // set font for window (flatlaf)
        UIManager.put("OptionPane.messageFont", new Font("Century Gothic", Font.PLAIN, 14));
        String getMessage = JOptionPane.showInputDialog(jframe, message);
        int getMessageInt = Integer.parseInt(getMessage);
        return getMessageInt;
    }
}