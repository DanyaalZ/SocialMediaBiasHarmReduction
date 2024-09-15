import javax.swing.*;
import java.awt.*;

/*window for sentiment preferences - a user can select what kind of sentiment bias they have a preference for */
public class SentimentPreferences extends GUI {
    private User user;
    //window for sentiment preferences, size 750x300 using class GUI
    private JFrame window = createWindow("Sentiment Preferences", 750, 300);

    //connect to database, use username as identifier
    private DatabaseConnection dbConnect = new DatabaseConnection();
    private String username;

    public SentimentPreferences(User user)
    {
        super();
        this.user = user;
        this.username = user.getUsername();
    }

    //sentiment window
    public void sentimentWindow() 
    {
        JPanel hold = createPanel(this.window);

        //state name of window (Sentiment bias, with some info) + some information underneath
        addText(this.window, hold, "Preferred sentiment bias: " + this.getUserSentiment(), 10, 30, 15);
        addText(this.window, hold, "You can have a preference towards positive, negative content or neither (no bias).", 10, 50, 15);

        //add buttons for changing these attributes
        //different coloured buttons, one for each sentiment type, each will set a bias

        //negative - red colour
        addButton(this.window, hold, () -> {this.changeSentiment("Negative");}, "Set Negative bias", 110, 20, 10, 100, new Color(153, 0, 0));

        //neutral - yellow colour
        addButton(this.window, hold, () -> {this.changeSentiment("Neutral");}, "Set Neutral (none) bias", 110, 20, 260, 100, new Color(204, 204, 0));

        //positive - green colour
        addButton(this.window, hold, () -> {this.changeSentiment("Positive");}, "Set Positive bias", 110, 20, 510, 100, new Color(0, 230, 0));

        showWindow(this.window, hold);
    }

    //change sentiment bias preference in user object and database
    public void changeSentiment(String newSentiment) 
    {
        user.setSentimentPreference(newSentiment);
        createMessage("Changes applied.");

        //set in database
        dbConnect.addToTableString("UPDATE users SET Sentiment_Preference = (?) WHERE Username = '" + username + "';", user.getSentimentPreference());
        this.window.dispose();
    }

    //to update current sentiment regularly for display
    private String getUserSentiment()
    {
        return this.user.getSentimentPreference();
    }

}