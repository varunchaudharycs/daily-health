package com.example.dailyhealthcheckup.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.dailyhealthcheckup.R;
import com.example.dailyhealthcheckup.dao.SignAndSymptomsDAO;

import java.util.HashMap;


public class CRUDMonitor extends SQLiteOpenHelper {

    public static final String DB_NAME = "chaudhary_v2.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "SignAndSymptoms";
    public static final String USERNAME = "username";
    public static final String DATE = "date";
    public static final String HEART_RATE = "heartRate";
    public static final String RESPIRATORY_RATE = "respiratoryRate";
    public static final String NAUSEA = "nausea";
    public static final String HEADACHE = "headache";
    public static final String DIARRHEA = "diarrhea";
    public static final String SORE_THROAT = "soreThroat";
    public static final String FEVER = "fever";
    public static final String MUSCLE_ACHE = "muscleAche";
    public static final String LOSS_OF_SMELL_OR_TASTE = "lossOfSmellOrTaste";
    public static final String COUGH = "cough";
    public static final String SHORTNESS_OF_BREATH = "shortnessOfBreath";
    public static final String TIREDNESS = "tiredness";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String[] COLUMNS = {USERNAME, DATE, HEART_RATE,
            RESPIRATORY_RATE, NAUSEA, HEADACHE, DIARRHEA, SORE_THROAT, FEVER, MUSCLE_ACHE,
            LOSS_OF_SMELL_OR_TASTE, COUGH, SHORTNESS_OF_BREATH, TIREDNESS, LATITUDE, LONGITUDE
            };
    private String DB_PATH = null;

    /**
     * Creating a database if doesn't exist
     * @param context
     */
    public CRUDMonitor(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    /**
     * Creating a table in existing database
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATION_TABLE = "CREATE TABLE if NOT EXISTS SignAndSymptoms ( "
                + "username TEXT, " + "date TEXT, "
                + "heartRate REAL, " + "respiratoryRate REAL, " + "nausea REAL, "
                + "headAche REAL, " + "diarrhea REAL, " + "soreThroat REAL, "
                + "fever REAL, " + "muscleAche REAL, " + "lossOfSmellOrTaste REAL, "
                + "cough REAL, " + "shortnessOfBreath Real, " + "tiredness Real, "
                + "latitude REAL, " + "longitude REAL, "
                + "PRIMARY KEY(username,date))";

        sqLiteDatabase.execSQL(CREATION_TABLE);

    }

    /**
     * Placeholder for abstract method implementation
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }


    /**
     * Inserts data in existing database
     * @param signAndSymptomsDAO
     * @param type : type of data (symptoms/signs)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insert(SignAndSymptomsDAO signAndSymptomsDAO, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, signAndSymptomsDAO.getUsername());
        values.put(DATE, signAndSymptomsDAO.getDate());
        values.put(HEART_RATE, signAndSymptomsDAO.getHeartRate());
        values.put(RESPIRATORY_RATE, signAndSymptomsDAO.getRespiratoryRate());
        values.put(NAUSEA, signAndSymptomsDAO.getNausea());
        values.put(HEADACHE, signAndSymptomsDAO.getHeadache());
        values.put(DIARRHEA, signAndSymptomsDAO.getDiarrhea());
        values.put(SORE_THROAT, signAndSymptomsDAO.getSoreThroat());
        values.put(FEVER, signAndSymptomsDAO.getFever());
        values.put(MUSCLE_ACHE, signAndSymptomsDAO.getMuscleAche());
        values.put(LOSS_OF_SMELL_OR_TASTE, signAndSymptomsDAO.getLossOfSmellOrTaste());
        values.put(COUGH, signAndSymptomsDAO.getCough());
        values.put(SHORTNESS_OF_BREATH, signAndSymptomsDAO.getShortnessOfBreath());
        values.put(TIREDNESS, signAndSymptomsDAO.getTiredness());
        values.put(LATITUDE, signAndSymptomsDAO.getLatitude());
        values.put(LONGITUDE, signAndSymptomsDAO.getLongitude());

        try {
            if (!isDataPresent(signAndSymptomsDAO.getUsername(), signAndSymptomsDAO.getDate())) {
                db.insert(TABLE_NAME,null, values);
                db.close();
            } else {
                if (type.equalsIgnoreCase("symptoms"))
                    update(signAndSymptomsDAO);
                else if (type.equalsIgnoreCase("signs"))
                    updateSignsData(signAndSymptomsDAO);
                else if (type.equalsIgnoreCase("location"))
                    updateLocationData(signAndSymptomsDAO);
            }

        } catch (Exception e) {
            System.out.println("ERROR: Database Insertion Failed! Exception - " + e);
        }

    }

    /**
     * Checks if data exists
     * @param username
     * @param currentDate
     * @return
     */
    public boolean isDataPresent(String username, String currentDate) {

        boolean isPresent = false;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT *" +
                " FROM " + TABLE_NAME +
                " WHERE username = ?" +
                " AND date = ?",
                new String[] {username, currentDate});

        if (cursor.moveToFirst() && cursor.getCount() > 0) { isPresent = true; }

        cursor.close();
        return isPresent;
    }

    /**
     * Checks if data exists
     * @param username
     * @param currentDate
     * @return
     */
    public HashMap<String, Float> getSymptomsData(String username, String currentDate, Context context) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nausea, headache, diarrhea, soreThroat, fever, muscleAche, lossOfSmellOrTaste, cough, shortnessOfBreath, tiredness" +
                        " FROM " + TABLE_NAME +
                        " WHERE username = ?" +
                        " AND date = ?",
                new String[] {username, currentDate});

        HashMap<String, Float> symptoms = new HashMap<>(); // "nausea" : 3, etc.
        String[] names = context.getResources().getStringArray(R.array.symptoms_list);
        for(int i = 0; i < names.length; ++i) { symptoms.put(names[i],0f); }

        if(cursor.moveToFirst() && cursor.getCount() > 0) {

            for(int i = 0; i < names.length; ++i) { symptoms.put(names[i], cursor.getFloat(i)); }

            return symptoms;
        }

        cursor.close();
        return symptoms;
    }

    /**
     * Checks if data exists
     * @param username
     * @param currentDate
     * @return
     */
    public Float[] getSignsData(String username, String currentDate) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT heartRate, respiratoryRate" +
                        " FROM " + TABLE_NAME +
                        " WHERE username = ?" +
                        " AND date = ?",
                new String[] {username, currentDate});

        Float[] rates = new Float[2]; // 0 - heart rate, 1 - respiratory rate

        if(cursor.moveToFirst() && cursor.getCount() > 0) {

            rates[0] = cursor.getFloat(0);
            rates[1] = cursor.getFloat(1);
        }
        else {

            rates[0] = 0f;
            rates[1] = 0f;
        }

        cursor.close();
        return rates;
    }

    /**
     * Updates data in the database
     * @param signAndSymptomsDAO
     */
    public void update(SignAndSymptomsDAO signAndSymptomsDAO) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues symptomsValue = new ContentValues();
        symptomsValue.put(NAUSEA, signAndSymptomsDAO.getNausea());
        symptomsValue.put(HEADACHE, signAndSymptomsDAO.getHeadache());
        symptomsValue.put(DIARRHEA, signAndSymptomsDAO.getDiarrhea());
        symptomsValue.put(SORE_THROAT, signAndSymptomsDAO.getSoreThroat());
        symptomsValue.put(FEVER, signAndSymptomsDAO.getFever());
        symptomsValue.put(MUSCLE_ACHE, signAndSymptomsDAO.getMuscleAche());
        symptomsValue.put(LOSS_OF_SMELL_OR_TASTE, signAndSymptomsDAO.getLossOfSmellOrTaste());
        symptomsValue.put(COUGH, signAndSymptomsDAO.getCough());
        symptomsValue.put(SHORTNESS_OF_BREATH, signAndSymptomsDAO.getShortnessOfBreath());
        symptomsValue.put(TIREDNESS, signAndSymptomsDAO.getTiredness());
        symptomsValue.put(LATITUDE, signAndSymptomsDAO.getLatitude());
        symptomsValue.put(LONGITUDE, signAndSymptomsDAO.getLongitude());
        try {
            db.update(TABLE_NAME, symptomsValue, "username=? and date=?",
                    new String[]{signAndSymptomsDAO.getUsername(), signAndSymptomsDAO.getDate()});
            db.close();
        } catch (Exception e) {
            System.out.println("ERROR: Symptoms update failed! Exception - " + e);
        }

    }

    /**
     * updating data entry in the table based on signs data
     * @param signAndSymptomsDAO
     */
    public void updateSignsData(SignAndSymptomsDAO signAndSymptomsDAO) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues signsValue = new ContentValues();
        signsValue.put(HEART_RATE, signAndSymptomsDAO.getHeartRate());
        signsValue.put(RESPIRATORY_RATE, signAndSymptomsDAO.getRespiratoryRate());
        try {
            db.update(TABLE_NAME, signsValue, "username=? and date=?",
                    new String[]{signAndSymptomsDAO.getUsername(), signAndSymptomsDAO.getDate()});
            db.close();
        } catch (Exception e) {
            System.out.println("ERROR: Signs update failed! Exception - " + e);
        }
    }

    /**
     * Update GPS location to DB
     * @param signAndSymptomsDAO
     */
    public void updateLocationData (SignAndSymptomsDAO signAndSymptomsDAO) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues signsValue = new ContentValues();
        signsValue.put(LATITUDE, signAndSymptomsDAO.getLatitude());
        signsValue.put(LONGITUDE, signAndSymptomsDAO.getLongitude());
        try {
            db.update(TABLE_NAME, signsValue, "userName=? and date=?",
                    new String[]{signAndSymptomsDAO.getUsername(), signAndSymptomsDAO.getDate()});
            db.close();
        } catch (Exception e) {
            System.out.println("ERROR: GPS update failed! Exception - " + e);
        }
    }

    /**
     * Get path of DB saved on device
     * @return
     */
    public String getPath() { return DB_PATH; }
}
