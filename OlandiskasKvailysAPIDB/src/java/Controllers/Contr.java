package Controllers;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import other.GameRoom;
import other.User;

@Controller
public class Contr {
        
    String DB_URL = "jdbc:mysql://localhost/okapidb";
    String USER = "root";
    String PASS = "";

    Connection conn;
    
    //DONE NOT TESTED
    @RequestMapping(value = "user/register", method = RequestMethod.POST)
    @ResponseBody
    public String FunkcRegister(@RequestBody String req) throws Exception{
        //get user attempting to register
        Gson parser = new Gson();
        User attemptingUser = parser.fromJson(req, User.class);
        //check for such user in DB
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users WHERE accname=? OR email=? OR password=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, attemptingUser.getAccName());
        ps.setString(2, attemptingUser.getEMail());
        ps.setString(3, attemptingUser.getPassword());
        //return check user
        ResultSet rs = ps.executeQuery();
        if(rs.next()) return parser.toJson("User with one of those already exists");
        rs.close();
        ps.close();
        int newID = 1;
        query = "SELECT MAX(ID) FROM users";
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
            newID = rs.getInt("MAX(ID)")+1;
        }
        rs.close();
        ps.close();
        query = "INSERT INTO users(ID, usertoken, gamestotal, gameswon, accstatus, accname, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        ps = conn.prepareStatement(query);
        ps.setInt(1, newID);
        ps.setInt(2, (int)(Math.random()*10000)+1);
        ps.setInt(3, 0);
        ps.setInt(4, 0);
        ps.setString(5, "active");
        ps.setString(6, attemptingUser.getAccName());
        ps.setString(7, attemptingUser.getEMail());
        ps.setString(8, attemptingUser.getPassword());
        ps.executeUpdate();
        ps.close();
        conn.close();
        return parser.toJson("Successfully registered");
    }
    
    //DONE NOT TESTED
    @RequestMapping(value = "user/login", method = RequestMethod.POST)
    @ResponseBody
    public String FunkcLogin(@RequestBody String req) throws Exception{
        //get user attempting to log in
        Gson parser = new Gson();
        User attemptingUser = parser.fromJson(req, User.class);
        User loggedInUser = null;
        //check for user in DB
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users WHERE email=? AND password=?;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, attemptingUser.getEMail());
        ps.setString(2, attemptingUser.getPassword());
        //return logged in user
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            loggedInUser = new User(rs.getInt("ID"), rs.getInt("usertoken"),
                            rs.getInt("gamestotal"), rs.getInt("gameswon"),
                            rs.getString("accstatus"), rs.getString("accname"),
                            rs.getString("email"), "-");
        }
        rs.close();
        ps.close();
        conn.close();
        if(loggedInUser!=null)return parser.toJson(loggedInUser);
        return parser.toJson("Incorrect logins or no such user exists");
    }
    
    //DONE NOT TESTED
    @RequestMapping(value = "user/edit/{uid}", method = RequestMethod.PATCH)
    @ResponseBody
    public String FunkcEditUser(@RequestBody String req, @PathVariable int uid) throws Exception{
        //get user to edit
        Gson parser = new Gson();
        User editedUser = parser.fromJson(req, User.class);
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        //update user
        String query = "UPDATE users SET accname=?, email=? WHERE ID=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, editedUser.getAccName());
        ps.setString(2, editedUser.getEMail());
        ps.setInt(3, editedUser.getID());
        ps.executeUpdate();
        ps.close();
        conn.close();
        //return editted user
        return parser.toJson(editedUser);
    }
    
    //DONE NOT TESTED needs tunning
    @RequestMapping(value = "leaderboard", method = RequestMethod.GET)
    @ResponseBody
    public String FunkcLeaderBoardGet(/*@PathVariable int start*/) throws Exception{
        Gson parser = new Gson();
        ArrayList<User> leaderBoardUsers = new ArrayList();
        //fetch the top win users
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users ORDER BY gameswon ASC, gamestotal DESC LIMIT 0, 50;";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            leaderBoardUsers.add(new User(rs.getInt("ID"), 00000,
                            rs.getInt("gamestotal"), rs.getInt("gameswon"),
                            "-" , rs.getString("accname"), "-",
                            "-"));
        }
        rs.close();
        ps.close();
        conn.close();
        return parser.toJson(leaderBoardUsers);
    }
    
    //NOT DONE NOT TESTED
    @RequestMapping(value = "room/create", method = RequestMethod.POST)
    @ResponseBody
    public String FunkcCreateRoom(@RequestBody String req) throws Exception{
        
        Gson parser = new Gson();
        GameRoom newRoom = parser.fromJson(req, GameRoom.class);
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT MAX(ID) FROM rooms";
        int newID = 1;
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            newID = rs.getInt("MAX(ID)")+1;
        }
        rs.close();
        ps.close();
        query = "INSERT INTO rooms(ID, token, playerCount, slotadmin, "
                + "slot1, slot2, slot3, slot4, slot5, name, status, password, "
                + "passwordRequired) VALUES (?, ?, ?, ?, ?, -1, -1, -1, -1, ?, 'waiting', ?, ?);";
        ps = conn.prepareStatement(query);
        ps.setInt(1, newID);
        ps.setInt(2, (int)(Math.random()*10000)+1);
        ps.setInt(3, newRoom.getPlayerCount());
        ps.setInt(4, newRoom.getSlotAdmin());
        ps.setInt(5, newRoom.getSlotAdmin());
        ps.setString(6, newRoom.getPassword());
        ps.setInt(7, newRoom.getPasswordRequired());
        ps.executeUpdate();
        ps.close();
        conn.close();
        return parser.toJson("Successfully created");
    }
    
    //NOT DONE NOT TESTED
    @RequestMapping(value = "room/join/{uid}", method = RequestMethod.POST)
    @ResponseBody
    public String FunkcJoinRoom(@RequestBody String req, @PathVariable int uid) throws Exception{
        Gson parser = new Gson();
        GameRoom joiningRoom = parser.fromJson(req, GameRoom.class);
        GameRoom tempRoom = null;
        //
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM rooms WHERE ID=? AND password=?;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, joiningRoom.getID());
        ps.setString(2, joiningRoom.getPassword());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            tempRoom = new GameRoom(rs.getInt("ID"), rs.getInt("token"),
                            rs.getInt("playercount"), rs.getInt("slotadmin"),
                            rs.getInt("slot1"), rs.getInt("slot2"),
                            rs.getInt("slot3"), rs.getInt("slot4"),
                            rs.getInt("slot5"), rs.getString("name"),
                            rs.getString("status"), rs.getString("password"),
                            rs.getInt("passwordRequired"));
        }
        rs.close();
        ps.close();
        if(tempRoom.getSlot1()==-1) {
            query = "UPDATE rooms SET slot1=? WHERE ID=?;";
        }else if(tempRoom.getSlot2()==-1) {
            query = "UPDATE rooms SET slot2=? WHERE ID=?;";
        }else if(tempRoom.getSlot3()==-1) {
            query = "UPDATE rooms SET slot3=? WHERE ID=?;";
        }else if(tempRoom.getSlot4()==-1) {
            query = "UPDATE rooms SET slot4=? WHERE ID=?;";
        }else if(tempRoom.getSlot5()==-1) {
            query = "UPDATE rooms SET slot5=? WHERE ID=?;";
        }else return parser.toJson("Room full");
        ps= conn.prepareStatement(query);
        ps.setInt(1, uid);
        ps.setInt(2, joiningRoom.getID());
        ps.executeUpdate(query);
        ps.close();
        //
        query = "SELECT * FROM rooms WHERE ID=? AND password=?;";
        ps = conn.prepareStatement(query);
        ps.setInt(1, joiningRoom.getID());
        ps.setString(2, joiningRoom.getPassword());
        rs = ps.executeQuery();
        while (rs.next()) {
            tempRoom = new GameRoom(rs.getInt("ID"), rs.getInt("token"),
                            rs.getInt("playercount"), rs.getInt("slotadmin"),
                            rs.getInt("slot1"), rs.getInt("slot2"),
                            rs.getInt("slot3"), rs.getInt("slot4"),
                            rs.getInt("slot5"), rs.getString("name"),
                            rs.getString("status"), rs.getString("password"),
                            rs.getInt("passwordRequired"));
        }
        rs.close();
        ps.close();
        conn.close();
        return parser.toJson(tempRoom);
    }
    
    //NOT DONE NOT TESTED
    @RequestMapping(value = "room/edit/{rid}", method = RequestMethod.PATCH)
    @ResponseBody
    public String FunkcEditRoom(@RequestBody String req, @PathVariable int rid) throws Exception{
        /*//get user attempting to log in
        Gson parser = new Gson();
        User attemptingUser = parser.fromJson(req, User.class);
        User loggedInUser = null;
        //check for user in DB
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users WHERE email=? AND password=?;";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, attemptingUser.getEMail());
        ps.setString(2, attemptingUser.getPassword());
        //return logged in user
        ResultSet rs = ps.executeQuery();
        //try{}catch(Exception err){return parser.toJson("error");}
        while (rs.next()) {
            loggedInUser = new User(rs.getInt("ID"), rs.getInt("usertoken"),
                            rs.getInt("gamestotal"), rs.getInt("gameswon"),
                            rs.getString("accname"), rs.getString("email"),
                            "-");
        }
        rs.close();
        ps.close();
        conn.close();
        return parser.toJson(loggedInUser);*/
        return null;
    }
    
    //NOT DONE NOT TESTED
    @RequestMapping(value = "room/list", method = RequestMethod.GET)
    @ResponseBody
    public String FunkcRoomListGet(/*@PathVariable int start*/) throws Exception{
        /*Gson parser = new Gson();
        ArrayList<User> leaderBoardUsers = new ArrayList();
        //fetch the top win users
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users ORDER BY gameswon ASC, gamestotal DESC LIMIT 0, 50;";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            leaderBoardUsers.add(new User(rs.getInt("ID"), 00000,
                            rs.getInt("gamestotal"), rs.getInt("gameswon"),
                            rs.getString("accname"), "-",
                            "-"));
        }
        rs.close();
        ps.close();
        conn.close();
        return parser.toJson(leaderBoardUsers);*/
        return null;
    }
    
}
