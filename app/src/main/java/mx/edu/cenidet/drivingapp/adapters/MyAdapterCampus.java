package mx.edu.cenidet.drivingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mx.edu.cenidet.cenidetsdk.entities.Campus;
import mx.edu.cenidet.drivingapp.R;

/**
 * Created by Cipriano on 3/18/2018.
 */

public class MyAdapterCampus extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Campus> listCampus;

    public MyAdapterCampus(Context context, int layout, List<Campus> listCampus){
        this.context = context;
        this.layout = layout;
        this.listCampus = listCampus;
    }
    @Override
    public int getCount() {
        return this.listCampus.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listCampus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Copiamos la vista
        View view = convertView;
        //Inflamos la vista que nos ha llegado con nuestro layout personalizado
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        view = layoutInflater.inflate(R.layout.list_campus, null);

        //Nos traemos el valor dependiente de la posición
        String id = listCampus.get(position).getId();
        String name = listCampus.get(position).getName();

        //Referenciamos el elemento a modificar y lo rellenamos.
        TextView textViewCampus = (TextView) view.findViewById(R.id.textViewCampus);
        textViewCampus.setText(name);

        return view;
    }
}
