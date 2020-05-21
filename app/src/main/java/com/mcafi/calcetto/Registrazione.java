package com.mcafi.calcetto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mcafi.calcetto.ui.main.SectionsPagerAdapter;

public class Registrazione extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuthReg = FirebaseAuth.getInstance();

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuthReg.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(Registrazione.this,Logout.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrazione);
        Button Registrazione= findViewById(R.id.ButtonReg);
        TextView giaReg =findViewById(R.id.giaRegistrato);
        Registrazione.setOnClickListener(this);
        giaReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ButtonReg:{
                EditText usernameReg= findViewById(R.id.usernameReg);
                String username = usernameReg.getText().toString();
                EditText emailReg= findViewById(R.id.emailReg);
                String email = emailReg.getText().toString();
                EditText passwordReg= findViewById(R.id.passwordReg);
                String password = passwordReg.getText().toString();
                mAuthReg.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Reg", "createUserWithEmail:success");
                            FirebaseUser user = mAuthReg.getCurrentUser();
                            startActivity(new Intent(Registrazione.this,Logout.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Reg", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
                break;
            }
            case R.id.giaRegistrato:{
                startActivity(new Intent(Registrazione.this,Login.class));
                break;
            }
        }

    }
}
