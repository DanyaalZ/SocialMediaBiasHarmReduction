/* This class entails the content feed - will show tweets given by the algorithm to the user, depending on their bias preferences.
This will be handled by the backend python algorithm, and this class's purpose is simply to show these tweets to the user without any 
further manipulation. Tweets shown will conform to the bias measures set out by the user, and will display the tweet, sentiment and category
for each tweet.
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class ContentFeed extends GUI {
    private User user;
    private DatabaseConnection dbConnect = new DatabaseConnection();
    BackendHandling backend;

    private JFrame window;
    private JPanel hold;

    public ContentFeed(User user) throws InterruptedException {
        this.user = user;
        this.window = createWindow("Content Feed", 500, 750);

        // display loading message
        createMessage("Loading content");

        // initialise and run the backend handling
        backend = new BackendHandling(this.user);

        // load content feed
        contentFeed();
    }

    // get tweetIDs from content in the database as a list
    private List<String> getTweetId() {
        return dbConnect.getStringsFromTable("SELECT textID FROM content");
    }

    // get tweet content from each tweetID
    private String getTweet(String tweetID) {
        return dbConnect.getStringFromTable("SELECT text FROM content WHERE textID = (?)", tweetID);
    }

    // get each sentiment from tweetID to match content
    private String getSentiment(String tweetID) {
        return dbConnect.getStringFromTable("SELECT sentiment FROM content WHERE textID = (?)", tweetID);
    }

    // as above, with category
    private String getCategory(String tweetID) {
        return dbConnect.getStringFromTable("SELECT category FROM content WHERE textID = (?)", tweetID);
    }

    // display content
    public void contentFeed() {
        // initialise panel for window
        this.hold = createPanel(window);

        // 'username's feed' text to display to the user
        addText(this.window, hold, user.getName() + "'s feed", 10, 5, 20);

        // initial y axis position, starting just below user's feed text to
        // display tweets and keep going further down
        int yAxis = 35;

        // current tweet number intitialised to 1
        int currentTweetNumber = 1;

        // iterate through tweets and display them
        /*
         * For each iteration (sentiment, category, tweet etc the y axis will move
         * slighlty further down)
         * A JScrollPane is later used so the user can scroll down and view all tweets
         * Tweet number kept so we can keep count of tweets
         * A horizontal line separator is created to separate tweets (containing text,
         * sentiment, category) from each other
         * for visibility.
         */
        List<String> tweets = getTweetId();
        for (String tweetId : tweets) {
            String tweet = getTweet(tweetId);
            String sentiment = getSentiment(tweetId);
            String category = getCategory(tweetId);

            // display tweet - blue colour
            JLabel tweetLabel = addText(window, hold, "Tweet " + currentTweetNumber + ": " + tweet, 10, yAxis, 15);
            tweetLabel.setForeground(Color.BLUE);

            // small gap
            yAxis += tweetLabel.getPreferredSize().height + 10;

            // display sentiment - orange colour
            JLabel sentimentLabel = addText(window, hold, "Sentiment: " + sentiment, 10, yAxis, 13);

            // small gap
            yAxis += sentimentLabel.getPreferredSize().height + 10;
            // create orange colour (darker to be visible on screen)
            Color orange = new Color(179, 71, 0);
            sentimentLabel.setForeground(orange);

            // display category - green colour
            JLabel categoryLabel = addText(window, hold, "Category: " + category, 10, yAxis, 13);

            // gap is slightly bigger between different tweets for differentiation
            yAxis += categoryLabel.getPreferredSize().height + 20;

            // create green colour (darker to be visible on screen)
            Color green = new Color(85, 128, 0);
            categoryLabel.setForeground(green);

            // horizontal line to help differentiate between tweets using separator
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            // takes a large number as the width, resizing window will not exceed this
            separator.setBounds(10, yAxis, 2000, 2);
            separator.setForeground(Color.black);
            hold.add(separator);

            // iterate tweet number
            currentTweetNumber += 1;
        }

        // set the preferred size of hold based on all content
        hold.setPreferredSize(new Dimension(480, yAxis));

        // make a jscrollpane so we can scroll down through all tweets on our panel
        JScrollPane scrollableWindow = new JScrollPane(hold);
        scrollableWindow.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollableWindow.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // add it to our window
        this.window.setContentPane(scrollableWindow);

        // set the window to visible, normally done automatically by GUI but we created
        // a custom scrollable window here
        this.window.setVisible(true);
    }
}