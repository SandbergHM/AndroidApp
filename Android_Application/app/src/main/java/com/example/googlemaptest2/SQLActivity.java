package com.example.googlemaptest2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLActivity extends AppCompatActivity {

    TextView progressTextView;
    Button btnRetry;
    Context thisContext;
    String[] harborNames;
    double[] harborLats;
    double[] harborLangs;

    int i = 0;
    int rowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);

        Resources res = getResources();
        progressTextView = (TextView) findViewById(R.id.progressTextView);
        btnRetry = (Button) findViewById(R.id.retryBtn);
        thisContext = this;

        progressTextView.setText("");

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });

        final GetData retrieveData = new GetData();
        retrieveData.execute("");

    }

    private class GetData extends AsyncTask<String, String, String>{

        String msg = "";

        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

        static final String DB_URL = "jdbc:mysql://" + DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;

        protected void onPreExecute(){
            progressTextView.setText("Loading...");
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;

            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);

                stmt = conn.createStatement();

                String sql2 = "SELECT COUNT(*) FROM harbors";
                ResultSet rsRow = stmt.executeQuery(sql2);
                rsRow.next();
                rowCount = rsRow.getInt(1);
                harborNames = new String[rowCount];
                harborLats = new double[rowCount];
                harborLangs = new double[rowCount];

                String sql = "SELECT * FROM harbors";
                ResultSet rs = stmt.executeQuery(sql);

                while(rs.next()){
                    String name = rs.getString("name");
                    double lat = rs.getDouble("lat");
                    double lang = rs.getDouble("lang");

                    harborNames[i] = name;
                    harborLats[i] = lat;
                    harborLangs[i] = lang;
                    i++;
                }

                msg = "Welcome!";

                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException connError){
                msg = String.valueOf(connError.getErrorCode());

                connError.printStackTrace();
            } catch (ClassNotFoundException e) {
                msg = "A Class not found exception was thrown.";

                e.printStackTrace();
            } finally{
                try{
                    if(stmt != null){
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try{
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(String msg){

            progressTextView.setText(this.msg);

            if(rowCount > 0 && harborNames != null && harborLats != null & harborLangs != null){

                Bundle bundle = new Bundle(4);
                bundle.putStringArray("harborNames", harborNames);
                bundle.putDoubleArray("harborLats", harborLats);
                bundle.putDoubleArray("harborLangs", harborLangs);
                bundle.putInt("rows", rowCount);

                Intent startIntent = new Intent(getApplicationContext(), MapActivity.class);
                startIntent.putExtra("bundle", bundle);
                startActivity(startIntent);
                finish();
            }

        }

    }

}






