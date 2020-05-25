package com.mcafi.calcetto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mcafi.calcetto.model.User;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuthReg = FirebaseAuth.getInstance();
    private FirebaseFirestore db = initializeDatabase();
    private FirebaseUser firebaseUser = mAuthReg.getCurrentUser();
    private final DocumentReference userRef = db.collection("utenti").document(firebaseUser.getUid());
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final TextView nameText = findViewById(R.id.nameText);
        final TextView usernameText = findViewById(R.id.usernameText);
        TextView saveSettings = findViewById(R.id.saveSettings);
        saveSettings.setOnClickListener(this);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                nameText.setText(user.getName());
                usernameText.setText(user.getUsername());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        TextView nameText = findViewById(R.id.nameText);
        TextView usernameText = findViewById(R.id.usernameText);
        Map<String, Object> user = new HashMap<>();
        user.put("name", nameText.getText().toString());
        user.put("username", usernameText.getText().toString());
        userRef.update(user);
        Toast.makeText(getApplicationContext(), "Modifiche salvate!", Toast.LENGTH_LONG);
    }

    private FirebaseFirestore initializeDatabase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        return db;
    }
}
