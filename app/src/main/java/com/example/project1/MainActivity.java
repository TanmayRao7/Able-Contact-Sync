package com.example.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    //Variables initialized
    Button sync_contact;
    Button delete_contact;
    Button add_contact;
    ProgressBar progressBar;
    FirebaseFirestore db;
    Map<String,Object> user;
    Date date;
    Intent intent;


    //Method To save contacts in device
    public void saveNewContact(String Name,String Phone ){

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        cpo.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        cpo.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        Name)
                .build());

        cpo.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        Phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());


        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public void delete_New_Contact(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Title Change for App
        setTitle("Able Contact Sync");
        // Set Default Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Requested Permission to Write Contacts
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CONTACTS}, PackageManager.PERMISSION_GRANTED);
        // Variables initialized
        sync_contact = findViewById(R.id.sync_button);
        delete_contact = findViewById(R.id.delete_button);
        add_contact = findViewById(R.id.add_button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        // Database instance called
        db = FirebaseFirestore.getInstance();
        // Class used to get Current date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        date = new Date();





        // Add Contact Method for testing and adding new contacts manually
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Add Contact Button Clicked", Toast.LENGTH_SHORT).show();
                /*
                db = FirebaseFirestore.getInstance();
                // Created a Hash map for passing data onto the firestore
                user = new HashMap<>();
                user.put("Name","User 2");
                user.put("Contact","0284030911");
                user.put("Date",format.format(date));

                // Adding data to the firebase
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                 */

            }
        });


        // Sync Contact Method Implemented
        sync_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Progress bar initialized onclick
                progressBar.setVisibility(View.VISIBLE);
                // Get Current Names and Contacts user added to the Firebase today
                db.collection("users")
                        .whereEqualTo("Date",format.format(date))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                int count = 0;
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                        // Storing Name and Contact in strings
                                        String Name = (String) documentSnapshot.get("Name");
                                        String Contact = (String) documentSnapshot.get("Contact");
                                        //Log.d("Tanmay User",Name+" => "+Contact );
                                        // Calling Save contact method defined previously
                                        saveNewContact(Name,Contact);
                                        //Toast.makeText(MainActivity.this, "Success "+count, Toast.LENGTH_SHORT).show();
                                        count+=1;
                                    }
                                    // Disable the progress bar after adding contacts
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        // Delete Contacts Onclick
        delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Delete Button Pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}