package com.example.googlemaptest2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                if(isEmailValid(mailText.getText())){
                    final CreateAccount createAccount = new CreateAccount();
                    createAccount.execute("");
                }else if (isAccountAndPasswordValid(accountText.getText().toString(), passwordText.getText().toString())){
                    progressTextView.setText("The password or account name that you have entered is invalid");
                }else{
                    progressTextView.setText("Please enter a valid e-mail address");
                }
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

    private class CreateAccount extends AsyncTask<String, String, String> {
        String createStatus = "";

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

                    printer.println("CreateAccount");
                    printer.println(accountText.getText().toString());
                    printer.println(passwordText.getText().toString());
                    printer.println(mailText.getText().toString());

                    initialString = reader.readLine();

                    if (initialString != "") {
                        createStatus = initialString;
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
            if(createStatus.equals("Success")){
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("ACCOUNT_CREATED", true);
                startIntent.putExtra("ACCOUNT_NAME", accountText.getText().toString());
                startActivity(startIntent);
                finish();
            }else{
                progressTextView.setText("Failed to create account");
            }
            super.onPostExecute(s);
        }
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isAccountAndPasswordValid(String account, String password){
        boolean isValid;

        if(Pattern.matches("[a-zA-Z]+", account) && account.length() > 8 && account.length() < 25){
            isValid = true;
        }else{
            isValid = false;
        }

        if(password.length() > 8 && password.length() < 25 && !password.contains(" ")){
            isValid = true;
        }else{
            isValid = false;
        }

        return isValid;
    }

}
