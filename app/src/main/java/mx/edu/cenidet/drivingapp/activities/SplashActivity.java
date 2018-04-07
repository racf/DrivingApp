package mx.edu.cenidet.drivingapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import mx.edu.cenidet.cenidetsdk.controllers.CampusController;
import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.drivingapp.R;

public class SplashActivity extends AppCompatActivity implements CampusController.CampusServiceMethods{
    private Intent mIntent;
    private CampusController campusController;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private ArrayList<Campus> listCampus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteDrivingApp = new SQLiteDrivingApp(this);
        campusController = new CampusController(getApplicationContext(), this);
        listCampus = sqLiteDrivingApp.getAllCampus();

        if(listCampus.size() == 0){
            campusController.readCampus();
        }else{
            JSONArray arrayLocation;
            String originalString, clearString;
            double latitude, longitude;
            String[] subString;
            for(int i=0; i<listCampus.size(); i++){
                Log.i("Status: ", "ID: "+listCampus.get(i).getId());
                Log.i("Status: ", "type: "+listCampus.get(i).getType());
                Log.i("Status: ", "name: "+listCampus.get(i).getName());
                Log.i("Status: ", "address: "+listCampus.get(i).getAddress());
                Log.i("Status: ", "location: "+listCampus.get(i).getLocation());
                try {
                    arrayLocation = new JSONArray(listCampus.get(i).getLocation());
                    for (int j=0; j<arrayLocation.length(); j++){
                        Log.i("Status: ", "location: "+j+" "+arrayLocation.get(j).toString());
                        originalString = arrayLocation.get(j).toString();
                        clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                        //Log.i("Status: ", "clearString: "+clearString);
                        subString =  clearString.split(",");
                        latitude = Double.parseDouble(subString[0]);
                        longitude = Double.parseDouble(subString[1]);
                        Log.i("Status: ", "Latitude: "+latitude+ " Longitude: "+longitude);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Status: ", "pointMap: "+listCampus.get(i).getPointMap());
                Log.i("Status: ", "Create: "+listCampus.get(i).getDateCreated());
                Log.i("Status: ", "Modified: "+listCampus.get(i).getDateModified());
            }
            Log.i("Status ", "Lista con datos");
        }

        if(isEnableGPS()){
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            showGPSDisabledAlert();
            Log.i("Status ", "Inactivo gps");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isEnableGPS()){
            //Inicia el servicio del GPS
            mIntent = new Intent(this, HomeActivity.class);
            startActivity(mIntent);
            Log.i("Status ", "Activo gps");
            this.finish();
        }else {
            Log.i("Status ", "Inactivo gps");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private boolean isEnableGPS(){
        LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            return true;
        }else{
            return false;
        }
    }

    private void showGPSDisabledAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.message_alert_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.button_enable_alert_gps,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void readCampus(Response response) {
        //Log.i("Status: ", "Code: "+response.getHttpCode());
        //Log.i("Status: ", "BODY: "+response.getBodyString());
        switch (response.getHttpCode()){
            case 200:
                Campus campus;
                JSONArray jsonArray = response.parseJsonArray(response.getBodyString());
                //Log.i("Status: ", "----------");
                //Log.i("Status: ", "BODY Array: "+jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        campus = new Campus();
                        JSONObject object = jsonArray.getJSONObject(i);
                         campus.setId(object.getString("_id"));
                         campus.setType(object.getString("type"));
                        campus.setName(object.getString("name"));
                        campus.setAddress(object.getString("address"));
                        campus.setLocation(""+object.getJSONArray("location"));
                        campus.setPointMap(""+object.getJSONArray("pointMap"));
                        campus.setDateCreated(object.getString("dateCreated"));
                        campus.setDateModified(object.getString("dateModified"));
                        Log.i("Status: ", "ID: "+campus.getId());
                        Log.i("Status: ", "type: "+campus.getType());
                        Log.i("Status: ", "name: "+campus.getName());
                        Log.i("Status: ", "address: "+campus.getAddress());
                        Log.i("Status: ", "location: "+campus.getLocation());
                        Log.i("Status: ", "pointMap: "+campus.getPointMap());
                        Log.i("Status: ", "Create: "+campus.getDateCreated());
                        Log.i("Status: ", "Modified: "+campus.getDateModified());
                        if(sqLiteDrivingApp.createCampus(campus) == true){
                            Log.i("Status: ", "Dato insertado correctamente...!");
                        }else{
                            Log.i("Status: ", "Error al insertar...!");
                        }
                        Log.i("--------: ", "--------------------------------------");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
}
