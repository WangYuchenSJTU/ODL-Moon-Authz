package net.yuchen.shiroservlet;

import java.io.IOException;  
import java.io.Writer;  
import javax.servlet.ServletException;  
import javax.servlet.annotation.WebServlet;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import org.json.*;
import java.util.*;

public class ShiroServlet extends HttpServlet {  
    private static final long serialVersionUID = 1L;  
    private HashMap<String, String> userList;
    private HashMap<String, JSONArray> userRoles;
    private HashMap<String, JSONArray> userPermissions;
    public ShiroServlet() {  
        userList = new HashMap<String, String>();
        userList.put("admin","123456");
        userList.put("alice","888888");
        userList.put("bob","666666");
        
        userRoles = new HashMap<String, JSONArray>();
        userRoles.put("admin",new JSONArray("[\"admin\",\"teacher\"]"));
        userRoles.put("alice",new JSONArray("[\"teacher\"]"));
        userRoles.put("bob",new JSONArray("[]"));
       
        userPermissions = new HashMap<String, JSONArray>();
        userPermissions.put("admin",new JSONArray("[\"user:*\",\"teacher:*\"]"));
        userPermissions.put("alice",new JSONArray("[\"student:*\"]"));
        userPermissions.put("bob",new JSONArray("[]"));
    }  
      
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        Writer out = response.getWriter();  
        out.write("ok");  
    }  
  
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException, JSONException {  

        response.setContentType("text/html;charset=utf-8");  
        response.setHeader("Access-Control-Allow-Origin", "*");  
        response.setHeader("Access-Control-Allow-Methods", "GET,POST");  
  
        Writer out = response.getWriter();  
  
        JSONObject loginUser=JsonReader.receivePost(request);
        try {
            final String username = (String) loginUser.get("username");
            if (loginUser.get("method").equals("authc")){
                if (userList.get(username).equals(loginUser.get("password"))){
                    loginUser.put("message", "success!"); 
                }
                else {
                    loginUser.put("message", "fail!"); 
                }
                out.write(loginUser.toString());  
                out.flush();
            }
            else if (loginUser.get("method").equals("authz")){
                if (userRoles.containsKey(username) && userPermissions.containsKey(username)){
                    loginUser.put("roles", userRoles.get(username)); 
                    loginUser.put("permissions", userPermissions.get(username));
                    loginUser.put("response", "false"); 
                    out.write(loginUser.toString());  
                    out.flush();
                }
            }
        } catch (JSONException e) {}
    }
}  
