""" For reading from the sqlite database, will take user as an input from the java frontend to 
work with. Will be used for getting user bias preferences from the users table, and for writing
 to the content table. 
 
 The content table is wiped on every instance of this object, so new content can be presented to 
 the user once the bias algorithm is executed on it.
 
 The structure of the tables are as follows:
 """
import sqlite3
import pandas

class ConnectandReadDatabase:
    def __init__(self, user):
        self.user = user
        
        #sqlite objects for connection
        #this db is created automatically by the backend if it doesn't already exist
        self.connect = sqlite3.connect("../social.db")
        self.cursor = self.connect.cursor()

        #on each instance of the database object we must wipe the content table to provide fresh
        #conent for the user
        self.wipe_content_table()

    def __enter__(self):
        #for exit to work, which closes connection when done
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        #close connection on exit
        self.close_connection()
    
    def wipe_content_table(self):
        #akin to truncating the table
        self.cursor.execute("DELETE FROM CONTENT")
        self.connect.commit()

    #get a value from the users table given a username
    #username sanitised to prevent sql injection
    def get_value_from_users(self, value_to_get):
        self.cursor.execute(f"SELECT {value_to_get} FROM USERS WHERE Username = ?", (self.user,))

        #there will be only one result returned as only one is written per user, or overwritten
        result = self.cursor.fetchone()
        
        return result

    #write final pandas df to content table
    def write_to_content(self, final_df):
        self.cursor.execute("DROP TABLE IF EXISTS content")
        final_df.to_sql("content", con=self.connect, if_exists="replace") 
    
    #run when backend is finished for security
    def close_connection(self):
        self.connect.close()

    