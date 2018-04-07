package mx.edu.cenidet.drivingapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.fragments.HomeFragment;
import www.fiware.org.ngsi.datamodel.entity.DeviceSensor;
import www.fiware.org.ngsi.utilities.Constants;

/**
 * Created by Cipriano on 3/18/2018.
 */

public class SendDataService {
    private SendDataMethods sendDataMethods;
    private Context context;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    //Dectar Campus
    private ArrayList<Campus> listCampus;
    private ArrayList<LatLng> listLocation;
    private SQLiteDrivingApp sqLiteDrivingApp;
    private Campus campus = null, auxCampus = null;
    private boolean auxStatusLocation = false;

    public SendDataService(Context context, SendDataService.SendDataMethods sendDataMethods){
        this.context = context;
        this.sendDataMethods = sendDataMethods;
        filter = new IntentFilter(Constants.SERVICE_CHANGE_LOCATION_DEVICE);
        filter.addAction(Constants.SERVICE_RUNNING_SENSORS);
        ResponseReceiver receiver = new ResponseReceiver();
        // Registrar el receiver y su filtro
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

        //Detectar Campus
        sqLiteDrivingApp = new SQLiteDrivingApp(context);
        listCampus =  sqLiteDrivingApp.getAllCampus();
        //campus = new Campus();
    }


    public interface SendDataMethods{
        void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr);
        void detectCampus(Campus campus, boolean statusLocation);
        void sendDataAccelerometer(double ax, double ay, double az);
    }



    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.SERVICE_CHANGE_LOCATION_DEVICE:
                    latitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LATITUDE, 0);
                    longitude = intent.getDoubleExtra(Constants.SERVICE_RESULT_LONGITUDE, 0);
                    speedMS = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_MS, 0);
                    speedKmHr = intent.getDoubleExtra(Constants.SERVICE_RESULT_SPEED_KMHR, 0);
                    sendDataMethods.sendLocationSpeed(latitude, longitude, speedMS, speedKmHr);
                    //Detecta Campus
                    if(listCampus.size() > 0){
                        JSONArray arrayLocation;
                        String originalString, clearString;
                        double latitudePolygon, longitudePolygon;
                        String[] subString;
                        boolean statusLocation;
                        auxCampus = null;
                        auxStatusLocation = false;
                        for(int i=0; i<listCampus.size(); i++){
                            listLocation = new ArrayList<>();
                            campus = new Campus();
                            campus.setId(listCampus.get(i).getId());
                            campus.setType(listCampus.get(i).getType());
                            campus.setName(listCampus.get(i).getName());
                            campus.setAddress(listCampus.get(i).getAddress());
                            campus.setLocation(listCampus.get(i).getLocation());
                            campus.setPointMap(listCampus.get(i).getPointMap());
                            campus.setDateCreated(listCampus.get(i).getDateCreated());
                            campus.setDateModified(listCampus.get(i).getDateModified());
                            campus.setStatus(listCampus.get(i).getStatus());

                            //Log.i("Status: ", "Campus name: "+listCampus.get(i).getName());
                            try{
                                arrayLocation = new JSONArray(listCampus.get(i).getLocation());
                                for (int j=0; j<arrayLocation.length(); j++){
                                    originalString = arrayLocation.get(j).toString();
                                    clearString = originalString.substring(originalString.indexOf("[") + 1, originalString.indexOf("]"));
                                    subString =  clearString.split(",");
                                    latitudePolygon = Double.parseDouble(subString[0]);
                                    longitudePolygon = Double.parseDouble(subString[1]);
                                    listLocation.add(new LatLng(latitudePolygon,longitudePolygon));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            statusLocation = PolyUtil.containsLocation(new LatLng(latitude,longitude), listLocation, false);
                            //statusLocation = PolyUtil.containsLocation(new LatLng(18.870032,-99.211869), listLocation, false);
                            if(statusLocation == true){
                                auxCampus = campus;
                                auxStatusLocation = statusLocation;
                            }
                        }
                        //Logica para enviar el si se encuentra dentro del campus...
                        if(auxCampus == null && auxStatusLocation == false){
                            sendDataMethods.detectCampus(auxCampus, auxStatusLocation);
                        }else{
                            sendDataMethods.detectCampus(auxCampus, auxStatusLocation);
                        }

                    }else{
                        Log.i("STATUS: ","Carga los campus en el primer inicio de sesión");
                        listCampus =  sqLiteDrivingApp.getAllCampus();

                    }
                    //Log.i(STATUS, "VIEW Latitude: "+latitude+" Longitude: "+longitude+" Velocidad: "+speedMS+"m/s  Velocidad: "+speedKmHr+"km/hr");
                    break;
                case Constants.SERVICE_RUNNING_SENSORS:
                    if ((DeviceSensor) intent.getExtras().get(Constants.ACCELEROMETER_RESULT_SENSORS) != null) {
                        DeviceSensor deviceSensor = (DeviceSensor) intent.getExtras().get(Constants.ACCELEROMETER_RESULT_SENSORS);
                        //Log.i("json ACCELEROMETER: ", ""+functions.checkForNewsAttributes(deviceSensor));
                        //Log.i("Receiver acce: ", " ax: " + deviceSensor.getData().getValue().get(0) + " ay: " + deviceSensor.getData().getValue().get(1) + " az: " + deviceSensor.getData().getValue().get(2)+" id: " + deviceSensor.getId() + " type: " + deviceSensor.getType());
                        sendDataMethods.sendDataAccelerometer(deviceSensor.getData().getValue().get(0), deviceSensor.getData().getValue().get(1), deviceSensor.getData().getValue().get(2));
                        //Log.i("Receiver acce: ", " ax: " + deviceSensor.getData().getValue().get(0) + " ay: " + deviceSensor.getData().getValue().get(1) + " az: " + deviceSensor.getData().getValue().get(2));
                        deviceSensor = null;
                    }else if((DeviceSensor) intent.getExtras().get(Constants.GYROSCOPE_RESULT_SENSORS) != null){
                        DeviceSensor deviceSensor = (DeviceSensor) intent.getExtras().get(Constants.GYROSCOPE_RESULT_SENSORS);
                        //Log.i("Receiver gyro: ", " gx: " + deviceSensor.getData().getValue().get(0) + " gy: " + deviceSensor.getData().getValue().get(1) + " gz: " + deviceSensor.getData().getValue().get(2)+" id: " + deviceSensor.getId() + " type: " + deviceSensor.getType());
                        //Log.i("Receiver gyro: ", " gx: " + deviceSensor.getData().getValue().get(0) + " gy: " + deviceSensor.getData().getValue().get(1) + " gz: " + deviceSensor.getData().getValue().get(2));
                    }
                    break;
            }
        }
    }
}
