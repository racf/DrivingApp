package mx.edu.cenidet.drivingapp.fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import mx.edu.cenidet.cenidetsdk.db.SQLiteDrivingApp;
import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;
import mx.edu.cenidet.drivingapp.activities.HomeActivity;
import mx.edu.cenidet.drivingapp.services.SendDataService;
import www.fiware.org.ngsi.controller.AlertController;
import www.fiware.org.ngsi.datamodel.entity.Alert;
import www.fiware.org.ngsi.httpmethodstransaction.Response;
import www.fiware.org.ngsi.utilities.Constants;
import www.fiware.org.ngsi.utilities.DevicePropertiesFunctions;
import www.fiware.org.ngsi.utilities.Functions;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SendDataService.SendDataMethods, View.OnClickListener, AlertController.AlertResourceMethods{
    private View rootView;
    private double latitude, longitude;
    private double speedMS, speedKmHr;
    private IntentFilter filter;
    private static final String STATUS = "Status";
    private Context context;
    private SendDataService sendDataService;
    private TextView tvDetailCampus;
    private ImageView imagenViewDetailCampus;
    private FloatingActionButton btnFloating;
    private AlertController alertController;

    public HomeFragment() {
        context = HomeActivity.MAIN_CONTEXT;
        sendDataService = new SendDataService(context, this);
        alertController = new AlertController(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tvDetailCampus = (TextView) rootView.findViewById(R.id.tvDetailCampus);
        imagenViewDetailCampus = (ImageView) rootView.findViewById(R.id.imagenViewDetailCampus);
        btnFloating = (FloatingActionButton) rootView.findViewById(R.id.btnFloating);
        btnFloating.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void sendLocationSpeed(double latitude, double longitude, double speedMS, double speedKmHr) {
        //Log.i("STATUS: ","HomeFragment-sendLocationSpeed");
    }

    @Override
    public void detectCampus(Campus campus, boolean statusLocation) {
        if(statusLocation == true){
            //Log.i("STATUS: ","Campus Name: "+campus.getName()+" statusLocation: "+statusLocation);
            imagenViewDetailCampus.setImageResource(R.drawable.inside);
            tvDetailCampus.setText(context.getString(R.string.message_name_campus)+": "+campus.getName()+"\n"+context.getString(R.string.message_address_campus)+": "+campus.getAddress());
        }else{
            imagenViewDetailCampus.setImageResource(R.drawable.outside);
            tvDetailCampus.setText(context.getString(R.string.message_any_campus));
        }
        /*Log.i("STATUS: ","id: "+campus.getId());
        Log.i("STATUS: ","name: "+campus.getName());
        Log.i("STATUS: ","Address: "+campus.getAddress());
        Log.i("STATUS: ","Dentro/Fuera: "+statusLocation);*/
    }

    @Override
    public void sendDataAccelerometer(double ax, double ay, double az) {
        //Log.i("STATUS 1: ","ax: "+ax+" ay: "+ay+" az: "+az);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFloating:
                Date currentDate = new Date();
                Long date = currentDate.getTime() / 1000;
                Alert alert = new Alert();
                alert.setId("Alert:Device_Smartphone_"+new DevicePropertiesFunctions().getAndroidId(context)+":"+date);
                alert.getAlertSource().setValue("Device_Smartphone_"+new DevicePropertiesFunctions().getAndroidId(context));
                alert.getCategory().setValue("traffic");
                alert.getDateObserved().setValue("2018-02-12T18:10:33.00Z");
                alert.getDescription().setValue("Test Alert Android 1");
                alert.getLocation().setValue("18.879691, -99.221640");
                alert.getSeverity().setValue("high");
                alert.getSubCategory().setValue("UnauthorizedSpeedDetection");
                alert.getValidFrom().setValue("2018-02-12T18:10:33.00Z");
                alert.getValidTo().setValue("2018-02-12T18:10:33.00Z");
                try {
                    alertController.createEntity(context, alert.getId(), alert);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(context, "Floating: "+date, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onCreateEntityAlert(Response response) {
        Log.i(STATUS, "CODE Alert: "+response.getHttpCode());
    }

    @Override
    public void onUpdateEntityAlert(Response response) {

    }

    @Override
    public void onGetEntitiesAlert(Response response) {

    }
}
