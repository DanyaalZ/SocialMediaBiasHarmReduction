import subprocess


""" For checking if requirements are satisfied to run the backend files - based on requirements.txt
and part of testing """
class Requirements():
    def __init__(self):
        self.check_install_requirements()

    #check and install requirements
    def check_install_requirements(self):
        subprocess.run(["pip", "install", "-r", "requirements.txt"])