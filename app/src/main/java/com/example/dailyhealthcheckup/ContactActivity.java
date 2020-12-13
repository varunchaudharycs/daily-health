package com.example.dailyhealthcheckup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    String url = "http://2ba2dd4d3ddf.ngrok.io/contacts";
    int subjectId = 1;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Spinner subjectSelector = findViewById(R.id.subject_id);
        final DateSelector fromDate = new DateSelector(this, R.id.date);
        Button getContacts = findViewById(R.id.contact_graph);

        subjectSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { subjectId = i+1; }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        getContacts.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String date = String.valueOf(LocalDate.of(fromDate._year,
                        fromDate._month + 1,
                        fromDate._day));
                System.out.println("User input = Subject ID - " + subjectId + ", Date - " + date);

                new GetCloseContacts().execute(new String[]{url, String.valueOf(subjectId), date});

                Toast.makeText(getApplicationContext(), "Contact Graph saved in server!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    static class DateSelector implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

        EditText _editText;
        public int _day = 14, _month = 6, _year = 2011;
        private final Context _context;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public DateSelector(Context context, int editTextViewID) {
            Activity act = (Activity)context;
            this._editText = act.findViewById(editTextViewID);
            this._editText.setOnClickListener(this);
            this._context = context;
            _editText.setText(String.valueOf(LocalDate.of(_year,
                    _month + 1,
                    _day)));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            _year = year;
            _month = monthOfYear;
            _day = dayOfMonth;
            updateDisplay();
        }
        @Override
        public void onClick(View v) {
            DatePickerDialog dialog = new DatePickerDialog(_context, this, _year, _month, _day);
            dialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void updateDisplay() {
            _editText.setText(String.valueOf(LocalDate.of(_year,
                    _month + 1,
                    _day)));
        }
    }
}

class GetCloseContacts extends AsyncTask<String[], Void, String> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected String doInBackground(String[]... urls) {

        try {
            URL url = new URL(urls[0][0]);
            String inp_userid = urls[0][1];
            String inp_date = urls[0][2];

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject requestObj = new JSONObject();
            requestObj.put("userid", inp_userid);
            requestObj.put("date", inp_date);
            String request = requestObj.toString();
            System.out.println("Request for Contact Tracing server -");
            System.out.println(request);

            // Request
            try(OutputStream os=con.getOutputStream()){
                byte[] input = request.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            // Response
            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) { response.append(responseLine.trim()); }
                return response.toString();
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    protected void onPostExecute(String response) {

        System.out.println("Response from Contact Tracing server -");
        System.out.println(response);

        List<String> myData = new ArrayList<>();
        myData.add(new String(...));
        myData.add(new String(...));
        myData.add(new String(...));

        TableView<Flight> table = find(R.id.adjacency);
        table.setHeaderAdapter(new SimpleHeaderAdapter("Time", "Airline", "Flight", "Destination"));
        table.setDataAdapter(new FlightDataAdapter(myData));
    }
}