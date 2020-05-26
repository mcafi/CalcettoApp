package com.mcafi.calcetto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcafi.calcetto.model.User;

import java.io.InputStream;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View v;
    private FirebaseAuth mAuthReg = FirebaseAuth.getInstance();
    private FirebaseFirestore db = initializeDatabase();
    private FirebaseUser firebaseUser;
    private User user;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    private ImageView profileIcon;
    private  StorageReference storageRef = storage.getReference();
    private StorageReference immagini;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, viewGroup, false);
        firebaseUser = mAuthReg.getCurrentUser();
        immagini = storageRef.child("immagini_profilo/"+firebaseUser.getUid());

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
        final Button updateImage = v.findViewById(R.id.editPicturebutton);
        updateImage.setOnClickListener(this);
        FloatingActionButton settingsButton = v.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);
        profileIcon = v.findViewById(R.id.profileIcon);




        final long ONE_MEGABYTE = 3*1024 * 1024;
        immagini.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                profileIcon.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        return v;


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.logoutButton):
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case (R.id.settingsButton):
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case (R.id.editPicturebutton):
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profileIcon.setImageURI(imageUri);


            UploadTask uploadTask = immagini.putFile(imageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(),"errore in upload",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Toast.makeText(getContext(),"upload effettuato",Toast.LENGTH_LONG).show();
                }
            });
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
