package com.testechnique.minifacebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.testechnique.minifacebook.models.SignupResponse;
import com.testechnique.minifacebook.models.User;
import com.testechnique.minifacebook.service.APIInterface;
import com.testechnique.minifacebook.service.RetrofitClient;
import com.testechnique.minifacebook.storage.SharedPrefManager;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {


    @BindView(R.id.input_name)  EditText _nameText;
    @BindView(R.id.input_email)  EditText _emailText;
    @BindView(R.id.input_password)  EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)  EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)  Button _signupButton;


    private APIInterface apiInterface;
    private Call call;
    private SignupResponse signupResponse;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //boolean isLoggedIn = accessToken != null && !accessToken.isExpired();



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               String email = _emailText.getText().toString(),
                        password = _passwordText.getText().toString();
                Toast.makeText(SignupActivity.this,"onSuccess",Toast.LENGTH_LONG).show();

                Log.d("userId", Profile.getCurrentProfile().getId());

                SharedPrefManager.getInstance(SignupActivity.this).saveUsers(new User(email,password));


              /*  FacebookSdk.sdkInitialize(SignupActivity.this);
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(
                            Profile oldProfile,
                            Profile currentProfile) {

                        Log.i("profilTracker",currentProfile.getFirstName());
                    }
                };*/

                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/{802020020268488}/albums",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                Toast.makeText(SignupActivity.this,response.toString(),Toast.LENGTH_LONG).show();

                            }
                        }
                ).executeAsync();

                if (SharedPrefManager.getInstance(SignupActivity.this).isLoggedIn()) {

                    intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("userId",Profile.getCurrentProfile().getId());
                    startActivity(intent);

                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignupActivity.this,"onCancel",Toast.LENGTH_LONG).show();

                Log.i("onCancel","onCancel");

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(SignupActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();

                Log.i("onError","onError");
            }
        });


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();

            }
        });


    }

    public void signup() {

        if (!validate()) {

            return;
        }
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        apiInterface = RetrofitClient.getClient().create(APIInterface.class);

        call = apiInterface.signup(name, email,password);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                signupResponse = (SignupResponse) response.body();
                if(signupResponse.isMessage()){

                    final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Creating Account...");
                    progressDialog.show();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    progressDialog.dismiss();
                                    Toast.makeText(SignupActivity.this,"Account created successfully",Toast.LENGTH_LONG).show();
                                    LoginManager.getInstance().logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile"));

                                }
                            }, 3000);
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(SignupActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }





    public boolean validate() {
        boolean valid = true;
        String name = _nameText.getText().toString(),email = _emailText.getText().toString(),
                password = _passwordText.getText().toString(),reEnterPassword = _reEnterPasswordText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }



        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            _passwordText.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}