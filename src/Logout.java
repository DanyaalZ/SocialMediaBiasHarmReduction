public class Logout {
    // Destroy session, restart program at login

    private User user;

    public Logout(User user) {
        this.user = user;
    }

    public void logout() {
        user.setAge(0);
        user.setName(null);
        user.setUsername(null);

        Login login = new Login();
        login.chooseSignUporLogin();
    }
}