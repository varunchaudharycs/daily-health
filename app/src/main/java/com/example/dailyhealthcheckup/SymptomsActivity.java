package com.example.dailyhealthcheckup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dailyhealthcheckup.dao.SignAndSymptomsDAO;
import com.example.dailyhealthcheckup.db.CRUDMonitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SymptomsActivity extends AppCompatActivity {

    HashMap<String, Float> symptoms;
    final String username = "vchaudh7";
    final String today = String.valueOf(LocalDate.now());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        final CRUDMonitor crudMonitor = new CRUDMonitor(SymptomsActivity.this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Spinner symptomsDropdown = (Spinner)findViewById(R.id.symptoms);
        final RatingBar symptomRating = (RatingBar)findViewById(R.id.symptom_rating);
        Button uploadSymptoms = (Button)findViewById(R.id.upload_symptoms);

        symptoms = crudMonitor.getSymptomsData(username, today, getApplicationContext());

        symptomRating.setRating(symptoms.get(getResources().getStringArray(R.array.symptoms_list)[0]));

        symptomRating.setOnRatingBarChangeListener( new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                symptoms.put(symptomsDropdown.getSelectedItem().toString(), ratingBar.getRating());
            }
        });

        symptomsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Float symptom = symptoms.get(adapterView.getItemAtPosition(i));
                symptomRating.setRating(symptom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        uploadSymptoms.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                SignAndSymptomsDAO symptomsData = new SignAndSymptomsDAO();
                symptomsData.setUsername(username);
                symptomsData.setDate(today);
                symptomsData.setNausea(symptoms.get("Nausea"));
                symptomsData.setHeadache(symptoms.get("Headache"));
                symptomsData.setDiarrhea(symptoms.get("Diarrhea"));
                symptomsData.setSoreThroat(symptoms.get("Sore throat"));
                symptomsData.setFever(symptoms.get("Fever"));
                symptomsData.setMuscleAche(symptoms.get("Muscle ache"));
                symptomsData.setLossOfSmellOrTaste(symptoms.get("Loss of smell or taste"));
                symptomsData.setCough(symptoms.get("Cough"));
                symptomsData.setShortnessOfBreath(symptoms.get("Shortness of breath"));
                symptomsData.setTiredness(symptoms.get("Tiredness"));

                crudMonitor.insert(symptomsData, "symptoms");

                Toast.makeText(getApplicationContext(),
                        "Symptoms data saved!",
                        Toast.LENGTH_LONG)
                        .show();

                for(String key: symptoms.keySet()){
                    System.out.println(key+": "+symptoms.get(key));
                }
            }
        });
    }
}