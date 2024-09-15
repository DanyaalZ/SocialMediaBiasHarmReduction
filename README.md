# SocialMediaBiasHarmReduction
An implementation aimed towards filtering text-based social media content to the preferences of the user - mitigating filter bubbles and algorithmic bias within the scope of what users are exposed to.

# Transparent Socials:

# Section 0 - Requirements:
You must have Python 3.11 installed to run this application. The pandas library is preferred for installation as it is required in the project. If this is not installed already, the application should install this automatically for you on execution.

NOTE -- IF YOU DO NOT HAVE PANDAS INSTALLED IN PYTHON, THE FIRST TIME YOU CLICK ON THE CONTENT FEED (RUN THE BACKEND), IT WILL SILENTLY INSTALL IT FOR YOU AND MAY TAKE UP TO FIVE MINUTES. IF YOU DO NOT HAVE PANDAS INSTALLED, BE PATIENT AS IT INSTALLS IN THIS CASE (THE APPLICATION IS NOT FROZEN). ONCE IT IS DONE IT MAY BRING UP AN EMPTY CONTENT FEED. IF THIS HAPPENS CLOSE THE CONTENT FEED WINDOW AND
RUN IT AGAIN - IT SHOULD NOW RUN SMOOTHLY.
 
You must have Java installed. Version 21.0.1 2023-10-17 LTS has been used to develop this application, and thus it should be assumed that anything prior to this is incompatible.

# Section 1 - running the project:

In order to run this project, execute the file "transparent_socials.jar" in the src folder. If this does not immediately open, navigate to the folder in your terminal of choice and execute it using the following parameters:  java -jar --enable-preview transparent_socials.jar (can also be done without the enable preview flag if it does not ask for it).

If this does not work, you can run the Java source code directly through the Main.java file in the src folder (this runs both the front and backend). Ensure that the contents of the lib folder are added to the classpath libraries in order to do this (they are packaged into the jar file already). 

Once the frontend is open, begin by creating an account, and proceed to set up your sentiment and category bias preferences (or leave them as default if you wish). Next, click on the content feed to execute the backend, and get the tweets for this run of the application (read the note in section 0 if this freezes for a minute, and ensure the right version of python is installed). These results can finally be measured against the original dataset (and your selections) in the measure bias window.

If you wish to execute and test the backend by itself, navigate to the "Data Handling - backend" folder in src and execute the command "python backend.py '[USERNAME HERE]'" In order to do this you must first create an account in the frontend. This can also be done to debug if you get an empty content feed window (should not occur).

The Extra_Testing.py file can be run independently (does not require a created user or frontend usage).

# Section 2 - navigating the files:

Frontend:
The constituents of the frontend are located in src. This contains the Java files which make up the frontend, beginning with Main to run it. Each file represents a class, with a specific purpose - for example the dashboard class provides a link (through the GUI class) to other classes such as sentiment preferences, which allows the user to select the sentiment they prefer to see the most. 

Database:
social.db - the database - is also (or will be when application is run) located in src. 
The tweets dataset is stored in 'src/Data Handling - backend/Data/Tweets.csv' - IF YOU WISH TO USE ANOTHER DATASET REPLACE THIS WITH ANOTHER CSV OF THE SAME NAME

Backend:
The constituents of the backend are located in 'src/Data Handling - backend'. The requirements for python to install by the backend is stored here also in 'requirements.txt'. The file where the whole backend is run in one, and will express its structure clearly, is 'backend.py'.

Measurements:
The json files used for measurement will be stored in 'src/Data Handling - backend' once the backend is run, and labelled accordingly (e.g. politics category measurements). 

Extra Testing:
The extra testing (not part of the backend) python file is also stored in 'src/Data Handling - backend'. This outputs four images of the comparisons between user input values and the algorithm output value (to measure accuracy) - see chapter 5.2 of report for details.

# Section 3 - attributions:

In Java, the following APIs are utilised for the application and should be accredited as such:

FlatLaf - https://github.com/JFormDesigner/FlatLaf by JFormDesigner - used to give a modern look to the Java Swing module

SL4J and Log4j - https://www.slf4j.org/ https://logging.apache.org/log4j/2.x/ - used for compatability (required by sqlite-jdbc)

SQLite-jdbc - https://github.com/xerial/sqlite-jdbc by Xerial - used to allow a portable database file which can be manipulated by both python and java through the SQLite format.

The test dataset can be found here:
Test Dataset - https://www.kaggle.com/datasets/yasserh/twitter-tweets-sentiment-dataset/ by M Yasser H
