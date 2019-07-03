package com.example.wmell.registerproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wmell.registerproject.model.LoginResponse;
import com.example.wmell.registerproject.service.ApiManager;
import com.example.wmell.registerproject.service.ServerCallbackLogin;
import com.example.wmell.registerproject.service.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView mUserEmailTextView;
    private TextView mUserPasswordTextView;
    private EditText mUserEmail;
    private EditText mUserPassword;
    private Button mLogin;
    private ProgressBar mProgressBar;

    public static String USER_PREFERENCES = "USER_PREFERENCES";
    public static String USER_TOKEN = "USER_TOKEN";
    public static String IS_USER_LOGIN = "IS_USER_LOGIN";
    public static String USER_EMAIL = "USER_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE).getBoolean(IS_USER_LOGIN, false)) {
          startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        mUserEmailTextView = findViewById(R.id.tv_email);
        mUserPasswordTextView = findViewById(R.id.tv_password);
        mUserEmail = findViewById(R.id.et_email);
        mUserPassword = findViewById(R.id.et_password);
        mLogin = findViewById(R.id.bt_login);
        mLogin.setOnClickListener(loginListener);
        mProgressBar = findViewById(R.id.pb_id);
    }



    private View.OnClickListener loginListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mUserEmail.getText().toString().isEmpty() || mUserPassword.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }else{
                showSpinner();
                checkAccess(new ServerCallbackLogin() {
                    @Override
                    public void onSuccess(String token) {
                        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(USER_TOKEN, token);
                        editor.putBoolean(IS_USER_LOGIN, true);
                        editor.putString(USER_EMAIL, mUserEmail.getText().toString().trim());
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        hideSpinner();
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    public void checkAccess(final ServerCallbackLogin serverCallbackLogin) {
        ServiceApi service = ApiManager.getService();

        Call<LoginResponse> call = service.login(mUserEmail.getText().toString().trim(), mUserPassword.getText().toString().trim());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    LoginResponse loginResponse = response.body();
                    serverCallbackLogin.onSuccess(loginResponse.getToken());
                }else{
                    serverCallbackLogin.onFail(null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                serverCallbackLogin.onFail(t);
            }
        });
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
        mUserEmailTextView.setVisibility(View.VISIBLE);
        mUserEmail.setVisibility(View.VISIBLE);
        mUserPasswordTextView.setVisibility(View.VISIBLE);
        mUserPassword.setVisibility(View.VISIBLE);
        mLogin.setVisibility(View.VISIBLE);
        mUserEmail.setText("");
        mUserPassword.setText("");
    }

    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
        mUserEmailTextView.setVisibility(View.GONE);
        mUserEmail.setVisibility(View.GONE);
        mUserPasswordTextView.setVisibility(View.GONE);
        mUserPassword.setVisibility(View.GONE);
        mLogin.setVisibility(View.GONE);

    }

}
