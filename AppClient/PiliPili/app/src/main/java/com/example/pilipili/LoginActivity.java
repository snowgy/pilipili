package com.example.pilipili;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pilipili.utils.PostUtils;
import com.example.pilipili.utils.Session;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Login Activity */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static String baseURL = "http://10.20.48.113:8080";
    private String globalUserName = "";

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_login)
    Button loginButton;
    @BindView(R.id.link_signup)
    TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    /**
     * <p>Implement user login</p>
     * <p>1. Read User Input </p>
     * <p>2. Call Remote login Interface </p>
     * <p>3. Get Response and Go to Main activity </p>
     */
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Please Check Your Input");
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String url = baseURL + "/userLogin";
        String userNameValue = userName.getText().toString();
        globalUserName = userNameValue;
        String passwordValue = password.getText().toString();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("userName", userNameValue);
        parameters.put("password", passwordValue);
        PostUtils postUtils = new PostUtils(url, parameters);
        postUtils.start();
        try {
            postUtils.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        org.json.simple.JSONObject res = postUtils.getJson();
        int code = Integer.parseInt(res.get("code").toString());
        Log.e(TAG, code+"");
        if (code != 0) {
            Log.d(TAG, "res is null");
            onLoginFailed(res.get("message").toString());
            hideProgress(progressDialog);
            return;
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        hideProgress(progressDialog);
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        // Disable going back to the MainActivity
//        moveTaskToBack(true);
//    }

    /**
     * Specify the behavior when login success
     */
    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Session.isLogin = true;
        Session.userName = globalUserName;
        finish();
    }

    /**
     * Specify the behavior when login fail
     * @param message the message which will be displayed to user
     */
    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);

    }

    /**
     * Check the user input
     * @return Boolean: whether the input is valid
     */
    public boolean validate() {
        boolean valid = true;

        String passwordValue = password.getText().toString();


        if (passwordValue.isEmpty() || passwordValue.length() < 4 || passwordValue.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    /**
     * An safe version of processDialog.dismiss()
     * @param mProgressDialog the processDialog you want to close
     */
    public void hideProgress(ProgressDialog mProgressDialog) {
        if(mProgressDialog != null) {
            if(mProgressDialog.isShowing()) { //check if dialog is showing.

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper)mProgressDialog.getContext()).getBaseContext();

                //if the Context used here was an activity AND it hasn't been finished or destroyed
                //then dismiss it
                if(context instanceof Activity) {
                    if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
                        mProgressDialog.dismiss();
                } else //if the Context used wasnt an Activity, then dismiss it too
                    mProgressDialog.dismiss();
            }
            // mProgressDialog = null;
        }
    }
}
