package com.msch.bicyclebook;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class SettingsScreen extends Activity {
    Button downloadBtn;

    public void downloadRoutes(View v) {
    downloadBtn = findViewById(R.id.downloadBtn);
    FirebaseStorage storage;
    final StorageReference storageReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference().child(user.getUid() + "/");

    final File rootPath = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + "com.msch.bicyclebook" + "/savedRoutes/" );

        storageReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
        @Override
        public void onSuccess(ListResult listResult) {
            for (StorageReference item : listResult.getItems()) {
                StorageReference resultStorage = storageReference.child(item.getName());
                Toast.makeText(getApplicationContext(),item.getName(),Toast.LENGTH_SHORT).show();
                final File localFile = new File(rootPath + "/" + item.getName());
                resultStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("firebase","local file created");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "local file WASN'T created" + e.toString());
                    }
                });
            }
        }
    });
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_tab);
    }
}
