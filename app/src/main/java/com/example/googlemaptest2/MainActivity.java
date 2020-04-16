package com.example.googlemaptest2;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import androidx.appcompat.app.AppCompatActivity;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    EditText AccountText;
    EditText PasswordText;
    Button LoginButton;
    Button CreateAccountButton;
    TextView progressTextView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Resources res = getResources();

        AccountText = (EditText) findViewById(R.id.AccountText);
        PasswordText = (EditText) findViewById(R.id.PasswordText);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        CreateAccountButton = (Button) findViewById(R.id.CreateAccountText);
        progressTextView = (TextView) findViewById(R.id.progressTextView);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(startIntent);
                finish();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String noTxt = "";
                if(AccountText.getText().toString().equals(noTxt) || PasswordText.getText().toString().equals(noTxt)){
                    progressTextView.setText("Please enter proper login credentials");
                }else {
                    final GetData retrieveData = new GetData();
                    retrieveData.execute("");
                }
            }
        });

    }

    private class GetData extends AsyncTask<String, String, String> {

        String msg = "";
        Boolean loginSuccess = false;

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

                String sql = "SELECT * FROM accounts WHERE AccNames = '" + AccountText.getText().toString() +"'";
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();

                String passText = PasswordText.getText().toString();
                String pass = rs.getString("AccNames");

                if(passText.equals(pass)){
                    msg = "Login success!";
                    loginSuccess = true;

                }else{
                    msg = pass;
                    loginSuccess = false;
                }

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

            if(loginSuccess){
                Intent startIntent = new Intent(getApplicationContext(), SQLActivity.class);
                startActivity(startIntent);
                finish();
            }

        }

    }

}
