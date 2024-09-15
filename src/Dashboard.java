import javax.swing.*;
import java.awt.*;

/* Main dashboard, presents multiple options for the user to choose - profile settings, view content, change biases (sentiment and category),
measure current bias levels, and logout or see about.
 */
public class Dashboard extends GUI {
    // user object from login/signup
    private User user;

    // to check if the user has run the backend yet (they shouldn't be able to check
    // measurements for other people)
    private boolean contentFeedExecuted;

    // JFrame created here to allow for closing by methods in class (login)
    private JFrame mainDashboard = createWindow("Dashboard", 500, 750);

    public Dashboard(User user) {
        super();
        this.user = user;

        this.createMessage(
                "Instructions: Each time you open the content feed, a new feed is created, and as such measured. In order to manipulate what you see (including measurements), you must set up bias preferences and then click on the content feed to execute them.");
    }

    // give user dashboard options
    // main dashboard interface
    public void options() {
        // create dashboard interface and components
        JPanel dashboardComponents = createPanel(mainDashboard);

        // text at the top
        addText(mainDashboard, dashboardComponents, user.getUsername(), 10, 0, 15);
        addText(mainDashboard, dashboardComponents, "Dashboard", 190, 0, 20);

        // user profile button, on click open profile window
        // nice grey colour
        addButton(mainDashboard, dashboardComponents, () -> {
            this.showUserProfile();
        }, "Profile", 50, 10, 350, 40, new Color(77, 84, 87));

        // present options (default four)
        addButton(mainDashboard, dashboardComponents, () -> {
            contentFeed();
        }, "Content", 200, 20, 50, 200, null);
        addButton(mainDashboard, dashboardComponents, () -> {
            sentimentPreferences();
        }, "Sentiment Bias Preferences", 200, 20, 50, 300, null);
        addButton(mainDashboard, dashboardComponents, () -> {
            categoryPreferences();
        }, "Category Bias Preferences", 200, 20, 50, 400, null);
        addButton(mainDashboard, dashboardComponents, () -> {
            biasMeasure();
        }, "Bias Measurement", 200, 20, 50, 500, null);

        // logout button
        addButton(mainDashboard, dashboardComponents, () -> {
            this.logout();
        }, "Logout", 50, 15, 350, 600, new Color(77, 84, 87));

        // about button - presents information regarding the program and how bias is
        // mitigated
        addButton(mainDashboard, dashboardComponents, () -> {
            this.aboutButton();
        }, "About", 50, 10, 10, 650, new Color(62, 57, 65));

        // set to system exit on close window
        setExitOnClose(mainDashboard);

        showWindow(mainDashboard, dashboardComponents);
    }

    // show user profile window when button clicked to do so
    private void showUserProfile() {
        Profile profile = new Profile(this.user);
        profile.profileWindow();
    }

    // user can choose preference for algorithm to use as a main preference
    // depending on bias level
    private void sentimentPreferences() {
        SentimentPreferences sentiments = new SentimentPreferences(this.user);
        sentiments.sentimentWindow();
    }

    // user can set category ratio bias
    private void categoryPreferences() {
        CategoryPreferences category = new CategoryPreferences(this.user);
        category.categoryWindow();
    }

    // user can view bias measures
    private void biasMeasure() {
        // user needs to execute backend first before checking measurements
        if (!contentFeedExecuted) {
            this.createMessage("You need to run the backend first! Click on content feed.");
        }

        else {
            BiasMeasure bias = new BiasMeasure(this.user);
            bias.biasWindow();
        }
    }

    // user can view content
    private void contentFeed() {
        try {
            ContentFeed content = new ContentFeed(this.user);
            content.contentFeed();
            // user can now execute the measurement window
            contentFeedExecuted = true;
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // text for about button
    private void aboutButton() {
        createMessage(
                "About: \nThis social media application's purpose is to give complete freedom of choice to the user in terms of what they see, with no algorithmic bias implemented by the program - bias is instead user controlled. Users are also prompted as to if their choices may incur filter bubbles.");
    }

    // user can logout
    // close dashboard window on logout
    public void logout() {
        Logout logout = new Logout(this.user);
        mainDashboard.dispose();
        logout.logout();
    }
}