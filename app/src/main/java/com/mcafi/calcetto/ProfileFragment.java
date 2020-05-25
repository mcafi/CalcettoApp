package com.mcafi.calcetto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mcafi.calcetto.model.User;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment implements View.OnClickListener  {

    private View v;
    private FirebaseAuth mAuthReg = FirebaseAuth.getInstance();
    private FirebaseFirestore db = initializeDatabase();
    private FirebaseUser firebaseUser;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_profile, viewGroup, false);
        firebaseUser = mAuthReg.getCurrentUser();

        final TextView nameText = v.findViewById(R.id.nameText);
        final TextView usernameText = v.findViewById(R.id.usernameText);
        final TextView emailText = v.findViewById(R.id.emailText);

        final DocumentReference userRef = db.collection("utenti").document(firebaseUser.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                nameText.setText(user.getName());
                usernameText.setText(user.getUsername());
                emailText.setText(user.getEmail());
            }
        });

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    nameText.setText(user.getName());
                    usernameText.setText(user.getUsername());
                    emailText.setText(user.getEmail());
                }
            }
        });

        Button logout = v.findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);
        FloatingActionButton settingsButton = v.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);
        return v;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.logoutButton):
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case (R.id.settingsButton):
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
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
