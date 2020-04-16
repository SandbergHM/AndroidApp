package com.example.googlemaptest2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateAccountActivity extends AppCompatActivity {

    Button createBtn;
    Button cancelBtn;
    EditText accountText;
    EditText passwordText;
    EditText mailText;
    TextView progressTextView;

    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        createBtn = (Button) findViewById(R.id.createBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        accountText = (EditText) findViewById(R.id.accountText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        mailText = (EditText) findViewById(R.id.mailText);
        progressTextView = (TextView) findViewById(R.id.progressTextView);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
                finish();
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String> {

        String msg = "";
        int credentialsValid = 0;

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

                String sql = "SELECT EXISTS(SELECT * FROM accounts WHERE AccNames = '" + accountText.getText().toString() +"' OR AccMail = '" + mailText.getText().toString() + "')";
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();

                credentialsValid =  rs.getInt("EXISTS(SELECT * FROM accounts WHERE AccNames = '" + accountText.getText().toString() +"' OR AccMail = '" + mailText.getText().toString() + "')");

                rs.close();

                    /*String sqlCreate = "INSERT INTO `mhs_example`.`accounts` (`AccNames`, `AccPass`, `AccMail`) VALUES ('"+
                            accountText.getText().toString() + "', '" +
                            passwordText.getText().toString() + "', '" +
                            mailText.getText().toString() + "')";*/

                    String sqlCreate = "INSERT INTO `mhs_example`.`accounts` (`AccNames`, `AccPass`, `AccMail`) VALUES ('r', 'r', 'r')";
                    ResultSet rsCreate = stmt.executeQuery(sqlCreate);
                    rsCreate.next();


                rsCreate.close();
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

            if(credentialsValid == 0){
                Intent startIntent = new Intent(getApplicationContext(), SQLActivity.class);
                startActivity(startIntent);
                finish();
            }else{
                progressTextView.setText("Account name or E-mail already in use");
            }

        }

    }
}
