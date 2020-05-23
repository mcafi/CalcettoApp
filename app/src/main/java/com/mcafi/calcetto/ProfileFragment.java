package com.mcafi.calcetto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment  implements View.OnClickListener{

    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){

        v = inflater.inflate(R.layout.fragment_profile, viewGroup, false);
        Button Logout = v.findViewById(R.id.logout);
        Logout.setOnClickListener(this);
        return v;
    }



    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

}
