package Controllers;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import other.User;

@Controller
public class Contr {
        
    String DB_URL = "jdbc:mysql://localhost/okapidb";
    String USER = "root";
    String PASS = "";

    Connection conn;
    
    @RequestMapping(value = "login", method = RequestMethod.POST)
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
        return parser.toJson(loggedInUser);
    }
    
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public String FunkcRegister(@RequestBody String req) throws Exception{
        //get user attempting to register
        Gson parser = new Gson();
        User attemptingUser = parser.fromJson(req, User.class);
        //check for such user in DB
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        String query = "SELECT * FROM users WHERE email=? ";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, attemptingUser.getEMail());
        //return check user
        ResultSet rs = ps.executeQuery();
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
        query = "INSERT INTO users(ID, usertoken, gamestotal, gameswon, accname, email, password) VALUES (?, ?, ?, ?, ?, ?, ?);";
        ps = conn.prepareStatement(query);
        ps.setInt(1, newID);
        ps.setInt(2, (int)(Math.random()*10000)+1);
        ps.setInt(3, 0);
        ps.setInt(4, 0);
        ps.setString(5, attemptingUser.getAccName());
        ps.setString(6, attemptingUser.getEMail());
        ps.setString(7, attemptingUser.getPassword());
        ps.executeUpdate();
        ps.close();
        conn.close();
        return parser.toJson("Successfully registered");
    }
    
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
                            rs.getString("accname"), "-",
                            "-"));
        }
        rs.close();
        ps.close();
        conn.close();
        return parser.toJson(leaderBoardUsers);
    }
    
}
