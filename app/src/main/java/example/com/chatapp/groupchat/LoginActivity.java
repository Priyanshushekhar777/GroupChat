package example.com.chatapp.groupchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity
{

    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink; //ForgetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneloginIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneloginIntent);
            }
        });

    }

    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter your email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }

        else{
            loadingBar.setTitle("signin ...");
            loadingBar.setMessage("Please wait while we are signing your account...");
            loadingBar.setCanceledOnTouchOutside(true);

            loadingBar.show();

           mAuth.signInWithEmailAndPassword(email,password)
                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful())
                          {
                              String currentUserId = mAuth.getCurrentUser().getUid();
                              //String deviceToken = FirebaseInstanceId.getInstance().getToken();



                              UsersRef.child(currentUserId).child("device_token")
                                      .setValue("")
                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task)
                                          {
                                            if (task.isSuccessful())
                                            {
                                                SendUserToMainActivity();
                                                Toast.makeText(LoginActivity.this, "Loggedin successful...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                          }
                                      });

                          }
                          
                          else 
                          {
                              String message = task.getException().toString();
                              Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                              loadingBar.dismiss();
                          }
                       }
                   });
        }
    }

    private void InitializeFields()
    {
        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        //ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
    }

    private void SendUserToMainActivity()
    {
        Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent loginIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(loginIntent);
    }
}
