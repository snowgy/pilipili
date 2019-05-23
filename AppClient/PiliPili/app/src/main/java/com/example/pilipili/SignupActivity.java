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

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Signup Activity */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    // public static String baseURL = "http://10.20.48.113:8080";
    private static String baseURL = "http://10.20.35.198:8080";

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.input_reEnterPassword)
    EditText reEnterPassword;
    @BindView(R.id.btn_signup)
    Button signupButton;
    @BindView(R.id.link_login)
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    /**
     * <p>Implement user login</p>
     * <p>1. Read User Input </p>
     * <p>2. Call Remote signup Interface </p>
     * <p>3. Get Response and Go to Main activity </p>
     */
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Signup Failed");
            return;
        }

        signupButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Theme_AppCompat_DayNight);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        String url = baseURL + "/userSignup";
        String userNameValue = userName.getText().toString();
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
            onSignupFailed(res.get("message").toString());
            hideProgress(progressDialog);
            return;
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        hideProgress(progressDialog);
                    }
                }, 3000);
    }
    /**
     * Specify the behavior when signup fail
     * @param message the message which will be displayed to user
     */
    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }
    /**
     * Specify the behavior when signup success
     */
    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * Check the user input
     * @return Boolean: whether the input is valid
     */
    public boolean validate() {
        boolean valid = true;

        String nameValue = userName.getText().toString();
        String passwordValue= password.getText().toString();
        String reEnterPasswordValue = reEnterPassword.getText().toString();

        if (nameValue.isEmpty() || nameValue.length() < 3) {
            userName.setError("at least 3 characters");
            valid = false;
        } else {
            userName.setError(null);
        }

        if (passwordValue.isEmpty() || passwordValue.length() < 4 || passwordValue.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (reEnterPasswordValue.isEmpty() || reEnterPasswordValue.length() < 4 || reEnterPasswordValue.length() > 10 || !(reEnterPasswordValue.equals(passwordValue))) {
            reEnterPassword.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPassword.setError(null);
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

