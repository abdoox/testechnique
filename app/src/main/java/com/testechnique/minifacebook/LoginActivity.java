package com.testechnique.minifacebook;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.testechnique.minifacebook.models.LoginResponse;
import com.testechnique.minifacebook.models.User;
import com.testechnique.minifacebook.service.APIInterface;
import com.testechnique.minifacebook.service.RetrofitClient;
import com.testechnique.minifacebook.storage.SharedPrefManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.input_email)  EditText _emailText;
    @BindView(R.id.input_password)  EditText _passwordText;
    @BindView(R.id.btn_login)  Button _loginButton;
    @BindView(R.id.link_signup)  TextView _signupLink;

    private String email,password;
    private APIInterface apiInterface;
    private Call call;
    private LoginResponse loginResponse;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));

            }
        });
    }

    public void login() {

        if (!validate()) {
            return;
        }
         email = _emailText.getText().toString();
         password = _passwordText.getText().toString();

        apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        call = apiInterface.login(email,password);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                loginResponse = (LoginResponse) response.body();



                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    SharedPrefManager.getInstance(LoginActivity.this).saveUsers(new User(loginResponse.getEmail(),loginResponse.getPassword()));
                                    progressDialog.dismiss();
                                    if(loginResponse.isMessage()){
                                    intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);}
                                    else{
                                        Toast.makeText(LoginActivity.this,"Incorrect email or password",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }, 3000);





            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });







    }



    @Override
    public void onBackPressed() {

       finish();
    }




    public boolean validate() {
        boolean valid = true;

         email = _emailText.getText().toString();
         password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 ) {
            _passwordText.setError("at least 6 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

                intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
    }



}
