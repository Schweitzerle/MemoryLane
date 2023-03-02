package com.example.memorylane.FragmentsMain;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.example.memorylane.Database.FirebaseDatabaseInstance;
import com.example.memorylane.Database.UserSession;
import com.example.memorylane.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;


public class SignaturePadFragment extends DialogFragment {

    SignaturePad signaturePad;
    MaterialButton resetButton, confirmButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signature_pad, container, false);
        signaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        resetButton = view.findViewById(R.id.reset_button);
        confirmButton = view.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                // Save the signature to cloud storage and update the user object in the real-time database
                saveSignatureToFirebase(signatureBitmap);
                dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });
        return view;
    }

    private void saveSignatureToFirebase(Bitmap signatureBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        String signature = Base64.encodeToString(data, Base64.DEFAULT);
        DatabaseReference signatureRef = FirebaseDatabaseInstance.getInstance().getFirebaseDatabase().getReference("Users").child(UserSession.getInstance().getCurrentUser().getUid()).child("signatureUrl");
        signatureRef.setValue(signature);
    }
}
