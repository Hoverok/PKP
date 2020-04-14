package other;

import java.io.Serializable;

/**
 * @author JustasM
 */
public class User implements Serializable {

    int ID;
    int userToken;
    int gamesTotal;
    int gamesWon;
    String accName;
    String EMail;
    String password;

    public User(int ID, int userToken, int gamesTotal, int gamesWon, String accName, String EMail, String Password) {
        this.ID = ID;
        this.userToken = userToken;
        this.gamesTotal = gamesTotal;
        this.gamesWon = gamesWon;
        this.accName = accName;
        this.EMail = EMail;
        this.password = Password;
    }

    public int getID() {
        return ID;
    }

    public int getUserToken() {
        return userToken;
    }

    public int getGamesTotal() {
        return gamesTotal;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public String getAccName() {
        return accName;
    }

    public String getEMail() {
        return EMail;
    }

    public String getPassword() {
        return password;
    }

}
