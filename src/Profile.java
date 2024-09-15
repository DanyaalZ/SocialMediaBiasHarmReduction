import javax.swing.*;
import java.awt.*;


public class Profile extends GUI {
    private User user;
    //window made to be a class variable so that live updates can be made to age, etc
    private JFrame window = createWindow("Profile", 500, 300);

    // Connect to database, use username as identifier
    private DatabaseConnection dbConnect = new DatabaseConnection();
    private String username;

    public Profile(User user) 
    {
        super();
        this.user = user;
        this.username = user.getUsername();
    }

    // profile window
    public void profileWindow() 
    {
        JPanel hold = createPanel(this.window);
        hold.setBackground(Color.LIGHT_GRAY);

        addText(this.window, hold, user.getUsername() + "'s profile", 10, 0, 20);

        // show user's name, age
        addText(this.window, hold, "Name: " + user.getName(), 70, 30, 15);
        addText(this.window, hold, "Age: " + user.getAge(), 270, 30, 15);

        // add buttons for changing these attributes
        // grey coloured buttons
        addButton(this.window, hold, () -> {this.changeName();}, "Change Name", 180, 20, 70, 80, new Color(77, 84, 87));

        addButton(this.window, hold, () -> {this.changeAge();}, "Change Age", 180, 20, 70, 140, new Color(77, 84, 87));

        showWindow(this.window, hold);
    }

    // change name in user object and database
    public void changeName() 
    {
        String name = getMessageString("Set Name:");
        user.setName(name);
        createMessage("Changes applied");
        this.window.dispose();

        // set in database
        dbConnect.addToTableString("UPDATE users SET Name = (?) WHERE Username = '" + username + "';", user.getName());
    }

    // change age in user object and database
    public void changeAge() 
    {
        int age = getMessageInt("Set Age:");
        user.setAge(age);
        createMessage("Changes applied");
        this.window.dispose();

        // set in database
        dbConnect.addToTableInt("UPDATE users SET AGE = (?) WHERE Username = '" + username + "';", user.getAge());
    }
}