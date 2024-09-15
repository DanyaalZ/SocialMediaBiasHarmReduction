import javax.swing.*;

import java.awt.*;

public class Login extends GUI {

	//given by user
	private String Username;
	private String Password;

	//for matching with database
	private String dataBaseUsername;
	private String dataBasePassword;

	//login window
	private JFrame chooseWindow = createWindow("Login", 500, 200);

	public Login() 
	{
		super();
	}

	//give option to sign up or login then return a user for the dashboard to use
	//this has uses GUI class's create window for GUI implementation
	public void chooseSignUporLogin() {
		//create GUI for choosing signup or login
		//using chooseWindow
		JPanel hold = createPanel(this.chooseWindow);
		
		//add text prompting user to choose
		addText(chooseWindow, hold, "Would you like to login or signup?", 90, 25, 20);

		//login button - uses login function within this class
		addButton(chooseWindow, hold, () -> {this.userLogin();}, "Login", 50, 15, 140, 100, Color.GRAY);
		
		//signup button - creating instance of signup class so user can signup, same effect as login function in this class
		addButton(chooseWindow, hold, () -> {this.userSignUp();}, "Sign Up", 50, 15, 275, 100, Color.GRAY);
		
		//set window to exit on close
		setExitOnClose(chooseWindow);

		showWindow(chooseWindow, hold);
	}

	public void redirectToDashboard(User user) {
		Dashboard dashboard = new Dashboard(user);
		dashboard.options();
	}

	// user can log in
	public void userLogin() 
	{
		Boolean verified = false;
	
		while (verified == false) 
		{
			this.Username = getMessageString("Enter Username:");
			this.Password = getPasswordString("Enter Password:");

			//validate character length before anything else
			if (this.lengthValidation())
			{
				//login verified, otherwise try again
				if (this.verifyLogin()) 
				{
					//create user object and return for usage
					User user = new User(this.Username);
					verified = true;

					//redirect to dashboard and close login window (set to invisible)
					this.redirectToDashboard(user);
					chooseWindow.setVisible(false);
				}
				//failed try again
				else 
				{
					createMessage("Sign in failed");
				}
			}
			//failed (length validation) try again
			else 
			{
				createMessage("Sign in failed");
			}
		}
	}

	// user can sign up (encapsulating sign up class)
	// once signed up they are automatically added to the database
	public void userSignUp() 
	{
		signUp signup = new signUp();
		User user = signup.userSignUp();

		//as with login redirect and set window to invisible
		this.redirectToDashboard(user);
		this.chooseWindow.setVisible(false);
	}

	// if too long or short automatically reject login/signup
	public Boolean lengthValidation()
	{
		Boolean lengthVerified = false;
		//length validation
		int usernameValidation = this.Username.length();
		int passwordValidation = this.Password.length();

		if ((usernameValidation < 1) || (passwordValidation < 1)) 
		{
			createMessage("One or more fields empty");
			return lengthVerified;
		}

		//limit of 20 characters
		if ((usernameValidation > 20) || (passwordValidation > 20)) 
		{
			createMessage("Length must not exceed 20 characters please try again");
			return lengthVerified;
		}
		return lengthVerified = true;
	}

	//check login details with database
	//SQL injection prevented using ?, prepared statments in db connect object
	public void checkWithDatabase() 
	{
		DatabaseConnection db = new DatabaseConnection();
		if (db.verify(
				"SELECT * FROM USERS WHERE username = ? AND password = ?;", this.Username, this.Password)) {
			this.dataBaseUsername = this.Username;
			this.dataBasePassword = this.Password;
		}
	}

	//verify once checked with database returning true or false
	public boolean verifyLogin() 
	{
		this.checkWithDatabase();
		if (this.Username.equals(dataBaseUsername) && this.Password.equals(dataBasePassword))
		{
			return true;
		}

		else 
		{
			return false;
		}
	}
}
