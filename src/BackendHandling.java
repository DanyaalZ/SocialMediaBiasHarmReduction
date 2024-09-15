    /* This class is arguably one of the most powerful in the frontend - it intialises the connection with the python backend, by 
    running the Backend.py file, which compiles and runs in order all of the algorithms, encapsulating the backend into a single
    entity, with the purpose of creating biases given user preferences, and cleaning and collecting tweets to present to the user
    in their content feed, with a new set of tweets each time.* 
    */

import java.io.File;

public class BackendHandling
    {
        private User user;

        public BackendHandling(User user) throws InterruptedException
        {
            this.user = user;

            //on creation, the connection is automatically initialised, as the thread is run
            //this means the content feed can wait until it has executed
            //run thread for backend
            this.runBackend();
        }

        //run backend - this will run each backend algorithm in succession to each other
        private void runBackend()
        {
            //will take user as an argument, so that the database can be read by the backend for the correct logged in user's details
            //using process builder to open python and execute our backend - should be scalable to any operating system with python installed
            
            //path of backend
            String path = "backend.py";
            //feed username as an argument to log in as current user
            ProcessBuilder processBuilder = new ProcessBuilder("python", path, '"' + this.user.getUsername() + '"');
            //set process builder working directory to location of backend files
            File workingDirectory = new File("Data Handling - backend");
            processBuilder.directory(workingDirectory);
            try
            {
                Process process = processBuilder.start();
                //wait for it to finish before continuing the program
                process.waitFor();
            }
            
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
    }
