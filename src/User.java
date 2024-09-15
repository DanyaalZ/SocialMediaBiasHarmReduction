/* User class, interacts with DataBase to get and set user preferences including name, age, sentiment bias preferences, and preferred 
category bias ratios 
Also contains initialises filter bubble seal for category bias, which will later be used for measurement*/

public class User {
	//user info
	private String Name;
	private String Username;
	private int Age = 0;

	//user bias preferences and bias ratios
	private String sentimentPreference;

	public int sportsCategoryBiasRatio;
	public int entertainmentCategoryBiasRatio;
	public int politicsCategoryBiasRatio;
	public int otherCategoryBiasRatio;

	//connect to db
	private DatabaseConnection dbConnect = new DatabaseConnection();

	public User(String Username)
	{
		this.Username = Username;
		// Get values from db if they exist already
		this.Name = dbConnect.getStringFromTable("SELECT Name FROM users WHERE Username = ?;", this.getUsername());
		this.Age = dbConnect.getIntFromTable("SELECT Age FROM users WHERE Username = ?;", this.getUsername());
		this.sentimentPreference = dbConnect.getStringFromTable("SELECT Sentiment_Preference FROM users WHERE Username = ?;", this.getUsername());
		
		//initialise category bias ratios (set to 25% each - balanced - if none exist in db)
		this.handleCategoryBiasRatios();
	}


	//get user's name, if one is not set it is set as "To be set"
	public String getName() 
	{
		if (this.Name != null) 
		{
			return this.Name;
		}

		else 
		{
			return "To be set";
		}
	}

	/* set and get editable user instance variables (username, name, age, sentiment and category preferences)*/

	//user attributes
	public void setUsername(String username) 
	{
		this.Username = username;
	}

	public String getUsername() 
	{
		return this.Username;
	}

	public void setName(String name) 
	{
		this.Name = name;
	}

	public int getAge() 
	{
		return this.Age;
	}

	public void setAge(int age) 
	{
		this.Age = age;
	}

	//sentiment preferences
	public void setSentimentPreference(String sentiment)
	{
		this.sentimentPreference = sentiment;
	}

	public String getSentimentPreference()
	{
		//if not set sentiment preference will be neutral
		if(this.sentimentPreference == null)
		{
			dbConnect.addToTableString("UPDATE users SET Sentiment_Preference = 'Neutral' WHERE Username = ?", this.Username);
			return "Neutral";
		}

		else
		{
			return this.sentimentPreference;
		}
	}

	//get values from db on initialision, and if they (any of them) don't exist set to 25% for user locally
	//they should all exist if the user has set values, and all should not exist if the user has not
	private void handleCategoryBiasRatios()
	{
		this.sportsCategoryBiasRatio = dbConnect.getIntFromTable("SELECT Sports_Preference FROM users WHERE Username = ?;", this.getUsername());
		this.entertainmentCategoryBiasRatio = dbConnect.getIntFromTable("SELECT Entertainment_Preference FROM users WHERE Username = ?;", this.getUsername());
		this.politicsCategoryBiasRatio = dbConnect.getIntFromTable("SELECT Politics_Preference FROM users WHERE Username = ?;", this.getUsername());
		this.otherCategoryBiasRatio = dbConnect.getIntFromTable("SELECT Other_Preference FROM users WHERE Username = ?;", this.getUsername());
	}

	//if conditions satisfied (a bias has been set to above 45%, the filter bubble seal is broken)
	public void breakFilterBubble()
	{
		this.dbConnect.addToTableString("UPDATE users SET Filter_Bubble_Seal = 'Broken' WHERE Username = ?;", this.getUsername());
	}

	//if conditions satisfied (a bias has been set to < 45%, the filter bubble seal is unbroken)
	public void unbreakFilterBubble()
	{
		this.dbConnect.addToTableString("UPDATE users SET Filter_Bubble_Seal = 'Unbroken' WHERE Username = ?;", this.getUsername());
	}

	//update category preference ratio in DataBase
	public void updateCategoryPreferenceDB(String Category, int newRatio)
	{
		//check which category to update using new ratio
		switch (Category) 
		{
			//sports
			case "Sports":
				dbConnect.addToTableInt("UPDATE users SET Sports_Preference = (?) WHERE Username = '" + this.Username + "';", newRatio);
				break;
			
			//entertainment
			case "Entertainment":
				dbConnect.addToTableInt("UPDATE users SET Entertainment_Preference = (?) WHERE Username = '" + this.Username + "';", newRatio);
				break;
			
			//politics
			case "Politics":
				dbConnect.addToTableInt("UPDATE users SET Politics_Preference = (?) WHERE Username = '" + this.Username + "';", newRatio);
				break;

			//other
			case "Other":
				dbConnect.addToTableInt("UPDATE users SET Other_Preference = (?) WHERE Username = '" + this.Username + "';", newRatio);
				break;
		}
	}
}