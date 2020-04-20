package com.example.googlemaptest2;
import java.net.*;
import java.io.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText AccountText;
    EditText PasswordText;
    Button LoginButton;
    Button CreateAccountButton;
    TextView progressTextView;
    Button socketButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        AccountText = findViewById(R.id.AccountText);
        PasswordText = findViewById(R.id.PasswordText);
        LoginButton = findViewById(R.id.LoginButton);
        CreateAccountButton = findViewById(R.id.CreateAccountText);
        progressTextView = findViewById(R.id.progressTextView);
        socketButton = findViewById(R.id.socketButton);

        if (getIntent().getBooleanExtra("ACCOUNT_CREATED", false) == true) {
            String createdUser = "";
            createdUser = (getIntent().getStringExtra("ACCOUNT_NAME"));
            AccountText.setText(createdUser);
        }

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
                progressTextView.setText("Logging in...");
                final Login checkCredentials = new Login();
                checkCredentials.execute("");
            }
        });
    }

    private class Login extends AsyncTask<String, String, String> {
        String msg = "";
        String loginStatus = "";

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            byte[] ipAddr = new byte[]{(byte) 188, (byte) 148, (byte) 116, (byte) 178};
            String initialString = "";
            InetAddress hostname = null;

            try {
                hostname = Inet4Address.getByAddress(ipAddr);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            try (Socket socket = new Socket(hostname, 3306)) {
                while (true) {
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    OutputStream output = socket.getOutputStream();
                    PrintWriter printer = new PrintWriter(output, true);

                    printer.println("Login");
                    printer.println(AccountText.getText().toString());
                    printer.println(PasswordText.getText().toString());

                    initialString = reader.readLine();

                    if (initialString != "") {
                        loginStatus = initialString;
                        socket.close();
                    }
                }
            } catch (UnknownHostException ex) {

                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {

                System.out.println("I/O error: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (loginStatus.equals("Success")) {
                progressTextView.setText("Login successful");
                Intent startIntent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(startIntent);
                finish();
            } else if (loginStatus.equals("Failed")) {
                progressTextView.setText("Incorrect login credentials");
            }


        }
    }
}
