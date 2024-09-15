public class signUp extends GUI {
	// given by user
	private String Username;
	private String Password;

	// for matching with database
	private String dataBaseUsername;
	private String dataBasePassword;

	// for writing default values to db for user when they sign up
	private DatabaseConnection db = new DatabaseConnection();

	public signUp() 
	{
		super();
	}

	// user can sign up - creates object, adds user to database
	public User userSignUp() 
	{
		Boolean verified = false;

		// verify length before anything else
		while (verified == false)
		{
			this.Username = getMessageString("Enter a new username:");
			this.Password = getMessageString("Enter a new password:");
			if (this.lengthValidation()) 
			{
				// login verified, otherwise try again
				if (this.verifyNotExists()) 
				{
					// create user object and return for usage
					User user = new User(this.Username);
					user.setUsername(this.Username);
					verified = true;

					// insert into database, sanitised for security - prepared statments , insert username, password and default values
					this.db.add("INSERT INTO users(Username, Password) VALUES (?,?);", this.Username, this.Password);
					//for sentiment and category biases (default values - 25 for all category preferences and neutral for sentiment preference)
					this.db.addToTableString("UPDATE users SET Sports_Preference = 25, Entertainment_Preference = 25, Politics_Preference = 25, Other_Preference = 25 WHERE Username = ?;",this.Username);
					this.db.addToTableString("UPDATE users SET Sentiment_Preference = 'Neutral' WHERE username = ?;", this.Username);
					//filter bubble seal starts off as unbroken for a new user
					this.db.addToTableString("UPDATE users SET Filter_Bubble_Seal = 'Unbroken' WHERE username = ?;", this.Username);
					return user;
				}

				else 
				{
					createMessage("User already exists");
				}
			}
		}
		return null;
	}

	public Boolean lengthValidation() 
	{
		Boolean lengthVerified = false;
		// length validation
		int fieldOneValidation = this.Username.length();
		int fieldTwoValidation = this.Password.length();

		if ((fieldOneValidation < 1) || (fieldTwoValidation < 1)) {
			createMessage("One or more fields empty");
			return lengthVerified;
		}

		if ((fieldOneValidation < 1) || (fieldTwoValidation < 1)) {
			createMessage("Length must not exceed 20 characters please try again");
			return lengthVerified;
		}
		return lengthVerified = true;
	}

	// check login details with database
	// SQL injection prevented using ?, prepared statments in db connect object (sanitisation)
	public void checkWithDatabase() 
	{
		DatabaseConnection db = new DatabaseConnection();
		if (db.verify("SELECT * FROM USERS WHERE username = ? AND password = ?;", this.Username, this.Password)) 
		{
			this.dataBaseUsername = this.Username;
			this.dataBasePassword = this.Password;
		}
	}

	// verify username does not already exist
	public boolean verifyNotExists() 
	{
		this.checkWithDatabase();
		if (!(this.Username.equals(dataBaseUsername) && this.Password.equals(dataBasePassword)))
		{
			return true;
		}

		else 
		{
			return false;
		}
	}
}