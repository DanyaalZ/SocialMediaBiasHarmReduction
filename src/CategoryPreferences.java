import javax.swing.*;
import java.awt.*;

/*window for category preferences - a user can change the ratio of category (sports, politics, entertainment, other) bias of content shown by 
clicking buttons, default is even bias (25% each, 4 cats) and is stored in the DB. If the total bias ratio reaches over or below 100%, the user 
will have to adjust the ratios to add to get to 100%. The text is dynamically updated for the user's convenience.
 */
public class CategoryPreferences extends GUI {
    private User user;
    //window for category preferences, size 1000x500 using (inheriting) class GUI
    private JFrame window = createWindow("Category Preferences", 1000, 300);

    private DatabaseConnection dbConnect;

    //variables for displaying category preference bias ratios (as percentages, initialised as their values in the user class)
    private int sports;
    private int entertainment;
    private int politics;
    private int other; 

    //display text for ratios - can be updated
    private JLabel ratioDisplayText;
    private JLabel totalRatioText;
    private JPanel hold;

    public CategoryPreferences(User user) {
        super();
    
        this.user = user;
        this.sports = user.sportsCategoryBiasRatio;
        this.entertainment = user.entertainmentCategoryBiasRatio;
        this.politics = user.politicsCategoryBiasRatio;
        this.other = user.otherCategoryBiasRatio;
    }

    //category window
    public void categoryWindow() {
        this.hold = createPanel(this.window);

        //display current ratios of category biases, allow for user to change for each category
        this.ratioDisplayText = addText(this.window, hold, "Current bias ratios: " + this.getCategoryRatios(), 10, 30, 15);
        addText(this.window, hold, "You can repurpose the ratios to set a bias for categories you want to see more.", 10, 50, 15);

        this.totalRatioText = addText(this.window, hold, "Total ratio: " + this.calculateTotalRatio() + "%", 10, 70, 15);

        //add buttons for changing these attributes
        //different coloured buttons, one for each category type, each will prompt the user to set a bias ratio percentage

        //sports
        addButton(this.window, hold, () -> {this.changeBiasRatio("Sports");}, "Change Sports ratio", 110, 20, 10, 100, new Color(0, 89, 179));

        //entertainment
        addButton(this.window, hold, () -> {this.changeBiasRatio("Entertainment");}, "Change ENT. ratio", 110, 20, 260, 100, new Color(204, 204, 0));

        //politics
        addButton(this.window, hold, () -> {this.changeBiasRatio("Politics");}, "Change Politics ratio", 110, 20, 510, 100, new Color(134, 0, 179));

        //other
        addButton(this.window, hold, () -> {this.changeBiasRatio("Other");}, "Change Other ratio", 110, 20, 760, 100, new Color(230, 184, 0));

        //submit button for the above ratios
        addButton(this.window, hold, () -> {this.checkSetRatioCompatibility();}, "Submit", 50, 10, 10, 200, new Color(77, 84, 87));
        
        showWindow(this.window, hold);
    }

    //change bias ratio for a particular category
    public void changeBiasRatio(String category) 
    {   
        //User can enter new ratio, should not be above 100 or below 0
        int newRatio = getMessageInt("Enter the new bias ratio (integer) you would like for " + category + ".");

        //validate before applying changes (ensure a viable percentage is entered)
        if(newRatio >= 0 && newRatio <= 100)
        {
            switch (category) {
                case "Sports":
                    this.sports = newRatio;
                    break;
                case "Entertainment":
                    this.entertainment = newRatio;
                    break;
                case "Politics":
                    this.politics = newRatio;
                    break;
                case "Other":
                    this.other = newRatio;
                    break;
            }

            /* Filter bubble warning - if a ratio is above 45% users will be warned of potential filter bubbles */
            if (newRatio > 45)
            {
                createMessage("Warning - a bias ratio of more than 45% can lead to filter bubbles, please be mindful of this.");
            }

            //update display text
            this.ratioDisplayText.setText("Current bias ratios: " + this.getCategoryRatios()); 
            this.totalRatioText.setText("Total ratio: " + this.calculateTotalRatio() + "%");
        }

        else
        {
            createMessage("Ratio does not meet parameters, try again.");
            this.changeBiasRatio(category);
        }
    }

    //get total ratio for display and calculation
    private int calculateTotalRatio()
    {
        return this.sports + this.entertainment + this.politics + this.other;
    }

    //check the ratio compatability (should add up to 100), and if it is successful in that, set the ratio for that category
    private void checkSetRatioCompatibility() 
    {
        //total ratio count begins with the new ratio entered by the user
        int totalRatio = calculateTotalRatio();

        //finally perform validation to check as to whether those percentages add to 100, in which case we can set the ratios and update the db
        //otherwise we will be prompted again to change that bias ratio to something more suitable
        if (totalRatio == 100) 
        {
            setUserCategoryRatios();
            updateUserCategoryRatios();
            percentageAdditionSuccess();
        } 

        else 
        {
            //user will have to try again if failed
            createMessage("The total percentage does not equal 100. Please try again.");
        }
    }

    //set the category ratios for the user object variables (this initialises what ratios the user sees when they open a catpref window)
    private void setUserCategoryRatios() 
    {
            user.sportsCategoryBiasRatio = sports;
            user.entertainmentCategoryBiasRatio = entertainment;
            user.politicsCategoryBiasRatio = politics;
            user.otherCategoryBiasRatio = other;
    }
    

    //category ratio preferences to display at top
	public String getCategoryRatios()
	{
		//get all current category bias ratios as a string
		return "Sports: " + this.sports + "% Entertainment: " + this.entertainment + "% Politics: " + this.politics+ "% Other: " + this.other + "%";
	}

    //for updating in DataBase (from user class)
    private void updateUserCategoryRatios() 
    {
        //check filter bubble seal (if any category > 45%, break otherwise make sure it is left as unbroken)
        if (this.sports > 45 || this.entertainment > 45 || this.politics > 45 || this.other > 45)
        {
            user.breakFilterBubble();
        }

        else
        {
            user.unbreakFilterBubble();
        }

        user.updateCategoryPreferenceDB("Sports", this.sports);
        user.updateCategoryPreferenceDB("Entertainment", this.entertainment);
        user.updateCategoryPreferenceDB("Politics", this.politics);
        user.updateCategoryPreferenceDB("Other", this.other);
    }

    //a message for when the percentage addition to 100 is successful, in the case of success the window is closed so 
    //when the user reopens it the values will be updated on screen
    private void percentageAdditionSuccess() 
    {
        createMessage("Changes applied.");
        this.window.dispose();
    }
}
