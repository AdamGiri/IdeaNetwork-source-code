/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.ideanetwork;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.SaveCallback;
import com.parse.SignUpCallback;


import java.util.List;

//TODO revamp this activity it is just a place holder for now
public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText username;
    EditText password;
    TextView textView;
    EditText emailSignIn;
    EditText usernameSignIn;
    EditText passwordSignIn;
    EditText usernameForgotPass;


    RelativeLayout relativeLayout;
    Button navigateToHome;
    public static int screenWidth;
    public static int screenHeight;
    public static int usernameCount = 16;
    RelativeLayout logInView;
    RelativeLayout entryView;
    RelativeLayout signInView;
    RelativeLayout forgotPassView;

    boolean sendPass;
    String emailText;

    /* public void onClickForgotPass(View view){
        if (logInView != null){
            entryView.removeView(logInView);
        } else {
            entryView.removeView(signInView);
        }
        if (entryView.getChildCount()==0){
            entryView.addView(forgotPassView);
            navigateToHome.setText("Send");
            sendPass = true;
        }



    }

   private void resetPass(){
        ParseUser.requestPasswordResetInBackground(emailText,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("resetPass","email sent successfully");
                        } else {
                        Log.i("resetPass","email sent unsuccessfully: "+e.getMessage());
                    }
                }
                });
    }*/


    public void login(View view){
        Log.i("sendpass", Boolean.toString(sendPass));
        parseQuery();
    /*    if (!sendPass){
            parseQuery();
        } else {
            Log.i("sendpass", "1");
            if (usernameForgotPass.getText()!=null && emailForgotPass.getText()!=null){
                Log.i("sendpass", "2");
                ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
                query.whereEqualTo("ParseUsername", usernameForgotPass.getText().toString());
                Log.i("sendpass", "3");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null) {

                            if (!objects.isEmpty()) {

                                emailText = objects.get(0).getString("email");
                                if (emailText != null){

                                    if (emailText.equals(emailForgotPass.getText().toString())){

                                        resetPass();
                                    } else {
                                        Toast.makeText(MainActivity.this, "The email provided is not registered with this user.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "There is no email associated with this user.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "The user provided does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Password request unsuccessful: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please enter valid log in details.", Toast.LENGTH_SHORT).show();
            }
        }*/

    }

    private void navigate(){
        ParseQuery<ParseObject> query = new ParseQuery<>("PseudoUser");
        query.whereEqualTo("ParseUsername", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    if (!objects.isEmpty()) {
                        if (objects.get(0).getString("ParseUsername") != null) {
                            objects.get(0).put("status", "online");
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    gotoHomePage();
                                }
                            });
                        }
                    } else {

                        gotoHomePage();
                    }
                } else {
                    gotoHomePage();
                }
            }
        });
    }

    private void gotoHomePage(){
        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == keyEvent.ACTION_DOWN) {
                Log.i("choco","key");
                parseQuery();
            } else {
                Log.i("choco", "not reached");
            }
            return false;
        }

        private void parseQuery(){
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            if (username!=null){
                query.whereEqualTo("username", username.getText().toString());
                Log.i("choco","username!=null");
            } else {
                query.whereEqualTo("username", usernameSignIn.getText().toString());
                Log.i("choco",usernameSignIn.getText().toString());
            }

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.isEmpty()) {
                        Log.i("choco","signup");
                        signUp();
                    } else {
                        Log.i("choco","logIn()");
                        logIn();
                    }
                }
            });
        }

        public void onSwitch(View view){
            sendPass = false;
            if (navigateToHome.getText().equals("Sign up")) {
                entryView.removeView(signInView);
                usernameSignIn.getText().clear();
                passwordSignIn.getText().clear();
                entryView.addView(logInView);
                password.setHint("pass");
                navigateToHome.setText("Login");
                textView.setText("or Sign up");
            } else {
                entryView.removeView(logInView);
                username.getText().clear();
                password.getText().clear();
                passwordSignIn.setHint("pass");
                entryView.addView(signInView);
                navigateToHome.setText("Sign up");
                textView.setText("or Login");
            }
        }


        public void logIn() {
            if (navigateToHome.getText().equals("Login")) {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Toast.makeText(com.parse.ideanetwork.MainActivity.this, "Log in Successful", Toast.LENGTH_SHORT).show();
                            navigate();
                        } else {
                            Toast.makeText(com.parse.ideanetwork.MainActivity.this, "Log in unsuccessful: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    }
                });
            } else {
                Toast.makeText(this, "This account already exists, please login", Toast.LENGTH_SHORT).show();
            }
        }

        public void signUp(){
            if (navigateToHome.getText().equals("Sign up")) {
                String usernameText = usernameSignIn.getText().toString();
                if (usernameText.length() <= usernameCount) {
                    final ParseUser user = new ParseUser();
                    user.setUsername(usernameText);
                    user.setPassword(passwordSignIn.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(com.parse.ideanetwork.MainActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseObject obj = new ParseObject("PseudoUser");
                                            obj.put("ParseUsername", ParseUser.getCurrentUser().getUsername());

                                            obj.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(com.parse.ideanetwork.MainActivity.this, " Sign up successful", Toast.LENGTH_SHORT).show();
                                                        navigate();
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(com.parse.ideanetwork.MainActivity.this, "Sign up unsuccessful " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(com.parse.ideanetwork.MainActivity.this, "Sign up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Max character limit exceeded, please limit username to 16 characters.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "This account does not exist, please sign up.", Toast.LENGTH_SHORT).show();
            }
            }





        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            setTitle("IdeaNetwork");
            entryView = (RelativeLayout)findViewById(R.id.entryField);
            logInView = (RelativeLayout)findViewById(R.id.logInView);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            signInView = (RelativeLayout) layoutInflater.inflate(R.layout.signin_view,entryView,false);
            username = (EditText)findViewById(R.id.username);
            password = (EditText)findViewById(R.id.password);
            usernameSignIn = (EditText)signInView.findViewById(R.id.username);
            passwordSignIn = (EditText)signInView.findViewById(R.id.password);

            forgotPassView = (RelativeLayout) layoutInflater.inflate(R.layout.pass_reset_view,entryView,false);
            usernameForgotPass = (EditText)forgotPassView.findViewById(R.id.username);

            textView = (TextView)findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
            relativeLayout.setOnClickListener(this);
            navigateToHome = (Button)findViewById(R.id.navigateToHome);
            password.setOnKeyListener(this);
            passwordSignIn.setOnKeyListener(this);
            if (ParseUser.getCurrentUser() !=null) {
                ParseUser.getCurrentUser().logOut();
            }
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;

        }


    @Override
    public void onClick(View v) {
        //TODO when youve added the logo to the main page set up a condition for it here like the relativelayout
        if (v.getId() == R.id.relativeLayout ) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus()!=null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}




