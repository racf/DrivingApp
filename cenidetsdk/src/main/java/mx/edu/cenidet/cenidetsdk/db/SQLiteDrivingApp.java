package mx.edu.cenidet.cenidetsdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mx.edu.cenidet.cenidetsdk.entities.Campus;

/**
 * Created by Cipriano on 3/17/2018.
 */

public class SQLiteDrivingApp extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "db_driving.db";

    interface  Tables{
        String TBL_CAMPUS = "tbl_campus";
    }

    //Campos de la tabla tbl_campus:
    public static final String TBL_CAMPUS_ID = "id";
    public static final String TBL_CAMPUS_TYPE = "type";
    public static final String TBL_CAMPUS_NAME = "name";
    public static final String TBL_CAMPUS_ADDRESS = "address";
    public static final String TBL_CAMPUS_LOCATION = "location";
    public static final String TBL_CAMPUS_POINTMAP = "pointMap";
    public static final String TBL_CAMPUS_DATECREATED = "dateCreated";
    public static final String TBL_CAMPUS_DATEMODIFIED = "dateModified";
    public static final String TBL_CAMPUS_STATUS = "status";

    //Sentencia SQL para crear la tabla tbl_campus
    String sqlCreateTblCampus = "CREATE TABLE "+ Tables.TBL_CAMPUS +" ("+ TBL_CAMPUS_ID + " VARCHAR(200) NOT NULL, "+ TBL_CAMPUS_TYPE +" VARCHAR(120) NOT NULL, "+ TBL_CAMPUS_NAME +" TEXT NOT NULL, "+ TBL_CAMPUS_ADDRESS +" TEXT NOT NULL, "+ TBL_CAMPUS_LOCATION + " TEXT NOT NULL, "+ TBL_CAMPUS_POINTMAP +" TEXT NOT NULL, "+ TBL_CAMPUS_DATECREATED + " TEXT, "+ TBL_CAMPUS_DATEMODIFIED +" TEXT, " + TBL_CAMPUS_STATUS+ " VARCHAR(2))";

    public SQLiteDrivingApp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTblCampus);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla;
        db.execSQL("DROP TABLE IF EXISTS "+Tables.TBL_CAMPUS);
        //Se crea la nueva versión de la tabla.
        db.execSQL(sqlCreateTblCampus);
    }

    /**
     * Elimina la base de datos.
     * @param context el contexto de donde se desea eliminar a DB.
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public boolean createCampus(Campus campus){
        boolean band = false;
        if(campus == null){
            band = false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(TBL_CAMPUS_ID, campus.getId());
            values.put(TBL_CAMPUS_TYPE, campus.getType());
            values.put(TBL_CAMPUS_NAME, campus.getName());
            values.put(TBL_CAMPUS_ADDRESS, campus.getAddress());
            values.put(TBL_CAMPUS_LOCATION, campus.getLocation());
            values.put(TBL_CAMPUS_POINTMAP, campus.getPointMap());
            values.put(TBL_CAMPUS_DATECREATED, campus.getDateCreated());
            values.put(TBL_CAMPUS_DATEMODIFIED, campus.getDateModified());
            campus.setStatus("0");
            values.put(TBL_CAMPUS_STATUS, campus.getStatus());
            if (campus.getId() == null){
                band = false;
            }else{
                long newRowId = db.insert(Tables.TBL_CAMPUS, null, values);
                Log.i("Result Insert: ", "" + newRowId);
                if (newRowId == -1) {
                    band = false;
                } else {
                    band = true;
                }
            }
            db.close();
        }
        return band;
    }
    public ArrayList<Campus> getAllCampus() {
        ArrayList<Campus> list = new ArrayList<Campus>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Tables.TBL_CAMPUS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Campus campus = new Campus();
                campus.setId(cursor.getString(0));
                campus.setType(cursor.getString(1));
                campus.setName(cursor.getString(2));
                campus.setAddress(cursor.getString(3));
                campus.setLocation(cursor.getString(4));
                campus.setPointMap(cursor.getString(5));
                campus.setDateCreated(cursor.getString(6));
                campus.setDateModified(cursor.getString(7));
                campus.setStatus(cursor.getString(8));
                // Adding to list
                list.add(campus);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }
}
