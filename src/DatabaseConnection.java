import java.sql.*;
import org.sqlite.JDBC;
import java.util.List;
import java.util.ArrayList;

/* This class handles the database connection for the Java Frontend, allowing for the user to login (verification), signup (create account), add/update single 
values to a table in the db based on their type (e.g. update or add a new int to the user's value), and likewise retrieve values for display where needed
throughout the implementation.

It creates an SQLite db if it doesn't already exist, for the users and contents to be displayed.
    User tables - User defined info such as name, username, pass, age, category bias preference percentages (ratio for each cat), and sentiment bias preferences 
    are kept.
    Content tables - Refreshed everytime by the python backend, bringing content based on user defined preferences and containing sentiment ratios old and new for bias measurement.

The users table has for each user: Name, Username, Password, Age, their Sentiment Preference (positive, negative, none) as a string, their category
preferences (sports, politics, entertainment, other) as percentages.

*/

public class DatabaseConnection {
    // db will be stored in the same folder as social.db (name of the database is
    // social)
    private String url = "jdbc:sqlite:social.db";

    // constructor for initialising the db connection
    public DatabaseConnection() {
        // check for required SQLite JDBC driver
        this.checkForDrivers();

        // create tables if they don't exist - for users' information to be stored, and
        // a content table for content to be displayed
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement statement = conn.createStatement();

            // SQL statements for creating USERS and CONTENT tables
            // users table - containing name, username, password, age, filter bubble seal,
            // sentiment preference and each category preference
            String createTableUsers = "CREATE TABLE IF NOT EXISTS USERS(Name TEXT, Username TEXT, Password TEXT, Age INTEGER, Filter_Bubble_Seal TEXT, Sentiment_Preference TEXT, Sports_Preference INTEGER, Entertainment_Preference INTEGER, Politics_Preference INTEGER, Other_Preference INTEGER);";
            // content table - containing the ID of each tweet, the tweet itself, its
            // sentiment, and its category
            String createTableContent = "CREATE TABLE IF NOT EXISTS CONTENT(textID TEXT, text TEXT, sentiment TEXT, category TEXT);";
            statement.executeUpdate(createTableUsers);
            statement.executeUpdate(createTableContent);
        }

        catch (SQLException e) {
            e.printStackTrace();
            this.errorMessage();
        }
    }

    // method to check for the SQLite JDBC driver - application cannot work without
    // it so this checked for
    // on object initialisation
    public void checkForDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
        }

        catch (ClassNotFoundException e) {
            e.printStackTrace();
            this.errorMessage();
        }
    }

    // error message method for database issues
    public void errorMessage() {
        GUI errorGui = new GUI();
        errorGui.createMessage("Issue connecting to database. Please contact your administrator.");
        System.exit(0);
    }

    // method to add data to the database using prepared statements
    // written for scalability (adding two items in a db given a table), but in this
    // project will be used for adding user login details (Signing up)
    // generally SQL Insert (Update) statement
    // used primarily in the signup class
    public void add(String dbQuery, String arg1, String arg2) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            stmt.setString(1, arg1);
            stmt.setString(2, arg2);
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method to verify data in the database using prepared statements (for
    // security, preventing SQL Injection)
    // written for scalability (verifiying items in a db), but in this project will
    // be used for verifying users login details
    // used in the login class
    // generally used for SQL Get statement
    public Boolean verify(String dbQuery, String arg1, String arg2) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            stmt.setString(1, arg1);
            stmt.setString(2, arg2);
            ResultSet result = stmt.executeQuery();

            return result.next();
        }

        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to add a single string to a table
    // Used generally for SQL Update statement
    // Written for general purposes (scalability), but in this project will be used
    // for updating user's name, and other string preferences such as sentiment
    // preferences
    public void addToTableString(String dbQuery, String arg1) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            stmt.setString(1, arg1);
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method to add a single integer to a table
    // SQL Update statement
    // written for general purposes (scalability), but in this project will be used
    // for updating user's age, and other integer preferences
    public void addToTableInt(String dbQuery, int arg1) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            stmt.setInt(1, arg1);
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // method to retrieve a single string value from a table
    // SQL Get statement
    // written for general purposes (scalability), but in this project will be used
    // for getting user's name, and sentiment preference strings for example
    public String getStringFromTable(String dbQuery, String arg1) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            // set sanitised part of statement to username (or other variable such as
            // tweetID or sentiment preference or category preference)
            stmt.setString(1, arg1);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return result.getString(1);
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // return a row of strings in an array
    // generally used to get rows of strings from the DatabaseConnection
    // arg1 generally used as tweetid, so dbquery would be select x from content
    // where tweetid = arg1
    public List<String> getStringsFromTable(String dbQuery) {
        // initialise list of rows to return (list string)
        List<String> rowsToReturn = new ArrayList<String>();

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            ResultSet result = stmt.executeQuery();

            // get each result given input and append to list (will be TweetID generally)
            while (result.next()) {
                rowsToReturn.add(result.getString(1));
            }

            return rowsToReturn;
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // method to retrieve a single integer value from a table
    // SQL Get statement
    // written for general purposes (scalability), but in this project will be used
    // for getting user's age, and sentiment preference numbers for example
    public int getIntFromTable(String dbQuery, String username) {
        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(dbQuery)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        // if nothing found number is 0
        return 0;
    }
}
