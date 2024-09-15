public class Main {
    /*
     * Entry point for frontend - where user can login, signup and then on the
     * dashboard change their bias preferences and view
     * social media content or check the outputted bias measure.
     */
    public static void main(String[] args) {
        GUI Welcome = new GUI();

        // Welcome messages - including name of app and privacy policy
        Welcome.createMessage("Welcome to Transparent Socials.");
        Welcome.createMessage(
                "Purpose & Privacy Policy \n This social media application's purpose is to allow for complete user customisation in terms of what they see, and the level of content bias they would like. This extends to ensuring filter bubbles are not created. \n The only information collected is your preferences, name, and age for storage.");

        // initialise database (creates db and tables if they don't already exist in
        // constructor)
        DatabaseConnection dbInitialise = new DatabaseConnection();

        // User redirected to sign up/log in
        Login();
    }

    public static void Login() {
        // Create login object
        Login login = new Login();

        // User chooses sign up or login
        login.chooseSignUporLogin();

        // Once logged in they are redirected to the dashboard
    }
}