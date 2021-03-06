package mx.edu.cenidet.cenidetsdk.controllers;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodGET;
import mx.edu.cenidet.cenidetsdk.httpmethods.methods.MethodPOST;
import mx.edu.cenidet.cenidetsdk.utilities.ConfigServer;

/**
 * Created by Cipriano on 3/16/2018.
 */

public class UserController implements MethodGET.MethodGETCallback, MethodPOST.MethodPOSTCallback{
    private static String URL_BASE_LOGIN = ConfigServer.http_host_login.getPropiedad();
    private static String URL_BASE_NODE = ConfigServer.http_host_node.getPropiedad();
    private UsersServiceMethods uServiceMethods;
    private Context context;
    private String method;
    private MethodPOST mPOST;
    private MethodGET mGET;

    public UserController(Context context, UsersServiceMethods uServiceMethods) {
        this.uServiceMethods = uServiceMethods;
        this.context = context;
    }
    @Override
    public void onMethodGETCallback(Response response) {
        switch (method){
            case "readUser":
                uServiceMethods.readUser(response);
                break;
        }
    }

    @Override
    public void onMethodPOSTCallback(Response response) {
        switch (method){
            case "logInUser":
                uServiceMethods.logInUser(response);
                break;
        }
    }

    public interface UsersServiceMethods{
        void createUser(Response response);
        void readUser(Response response);
        void updateUser(Response response);
        void deleteUser(Response response);
        void logInUser(Response response);
        void logOutUser(Response response);
    }

    public void logInUser(String email, String password){
        Response response = new Response();
        method = "logInUser";
        String URL = URL_BASE_LOGIN + ConfigServer.http_login.getPropiedad();
        /*String json= "{\n" +
                "\t\"auth\": {\n" +
                "\t\t\"identity\": {\n" +
                "\t\t\t\"methods\": [\n" +
                "\t\t\t\t\"password\"\n" +
                "\t\t\t],\n" +
                "\t\t\t\"password\": {\n" +
                "\t\t\t\t\"user\": {\n" +
                "\t\t\t\t\t\"name\": \""+email+"\",\n" +
                "\t\t\t\t\t\"domain\": {\n" +
                "\t\t\t\t\t\t\"id\": \"default\"\n" +
                "\t\t\t\t\t},\n" +
                "\t\t\t\t\t\"password\": \""+password+"\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";*/
        String json = "{\n" +
                "\t\"email\":\""+email+"\",\n" +
                "\t\"password\":\""+password+"\"\n" +
                "}";
        JSONObject jsonLogInUser = response.parseJsonObject(json);
        Log.i("Status", "JSON: "+jsonLogInUser);
        mPOST = new MethodPOST(this);
        mPOST.execute(URL, jsonLogInUser.toString());
    }

    public void readUser(String email){
        method = "readUser";
        String URL = URL_BASE_NODE + ConfigServer.http_user.getPropiedad()+"?email="+email;
        mGET = new MethodGET(this);
        mGET.execute(URL);
    }
}
