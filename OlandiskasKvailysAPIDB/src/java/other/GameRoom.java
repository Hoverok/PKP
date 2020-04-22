package other;

import java.io.Serializable;

/**
 * @author JustasM
 */
public class GameRoom implements Serializable{
    
    int ID;
    int token;
    int playerCount;
    int slotAdmin;
    int slot1;
    int slot2;
    int slot3;
    int slot4;
    int slot5;
    String name;
    String status;
    String password;
    int passwordRequired;

    public GameRoom(int ID, int token, int playerCount, int slotAdmin, int slot1, int slot2, int slot3, int slot4, int slot5, String name, String status, String password, int passwordRequired) {
        this.ID = ID;
        this.token = token;
        this.playerCount = playerCount;
        this.slotAdmin = slotAdmin;
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.slot4 = slot4;
        this.slot5 = slot5;
        this.name = name;
        this.status = status;
        this.password = password;
        this.passwordRequired = passwordRequired;
    }
    
    public int getID() {
        return ID;
    }

    public int getToken() {
        return token;
    }

    public int getPlayerCount() {
        return playerCount;
    }
    
    public int getSlotAdmin() {
        return slotAdmin;
    }

    public int getSlot1() {
        return slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public int getSlot3() {
        return slot3;
    }

    public int getSlot4() {
        return slot4;
    }

    public int getSlot5() {
        return slot5;
    }
    
    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getPasswordRequired() {
        return passwordRequired;
    }
    
}
