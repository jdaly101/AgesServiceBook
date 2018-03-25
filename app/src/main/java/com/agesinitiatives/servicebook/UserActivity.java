package com.agesinitiatives.servicebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentSnapshot userDocument;
    private String TAG = "UserActivity";

    /* UI components */
    protected EditText editUserName;
    protected EditText editUserParish;
    protected Spinner archdioceseSpinner;
    protected Spinner personaSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        editUserName = findViewById(R.id.editUserName);
        editUserParish = findViewById(R.id.editUserParish);

        archdioceseSpinner = findViewById(R.id.archdiocese_spinner);
        ArrayAdapter<CharSequence> archdioceseAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_archdiocese,
                R.layout.custom_spinner_item);
        archdioceseAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        archdioceseSpinner.setAdapter(archdioceseAdapter);

        personaSpinner = findViewById(R.id.persona_spinner);
        ArrayAdapter<CharSequence> personaAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_persona,
                R.layout.custom_spinner_item);
        personaAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        personaSpinner.setAdapter(personaAdapter);

        db.collection("users")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // TODO: Check if more than one record is returned
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                setDocument(document);
                            }
                        } else {
                            Log.i(TAG, "Error retrieving document", task.getException());
                        }
                    }
                });

    }

    public void saveUserInfo(View view) {
        Log.i(TAG, "Saving user info...");
        Map<String, Object> userRecord = new HashMap<>();
        userRecord.put("userId", user.getUid());

        String userName = editUserName.getText().toString();
        if (userName.equals("") == false) {
            userRecord.put("displayName", userName);
        }

        String parishName = editUserParish.getText().toString();
        if (parishName.equals("") == false) {
            userRecord.put("parish", parishName);
        }

        String archdiocese = archdioceseSpinner.getSelectedItem().toString();
        userRecord.put("archdiocese", archdiocese);

        String persona = personaSpinner.getSelectedItem().toString();
        userRecord.put("persona", persona);

        if (userDocument == null) {
            db.collection("users")
                    .add(userRecord)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Document added");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error adding document", e);
                        }
                    });
        } else {
            db.collection("users")
                    .document(userDocument.getId())
                    .set(userRecord)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document added or updated");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    private void setDocument(DocumentSnapshot document) {
        Log.d(TAG, "Setting document: " + document.getId());
        userDocument = document;
        editUserName = findViewById(R.id.editUserName);
        if (userDocument.getData().get("displayName") != null) {
            editUserName.setText(userDocument.getData().get("displayName").toString());
        }
        editUserParish = findViewById(R.id.editUserParish);
        if (userDocument.getData().get("parish") != null) {
            editUserParish.setText(userDocument.getData().get("parish").toString());
        }

        String[] archdioceseArray = getResources().getStringArray(R.array.user_archdiocese);
        String[] personaArray = getResources().getStringArray(R.array.user_persona);

        int archdioceseIndex = java.util.Arrays.asList(archdioceseArray)
                .indexOf(userDocument.getData().get("archdiocese").toString());
        archdioceseSpinner.setSelection(archdioceseIndex);

        int personaIndex = java.util.Arrays.asList(personaArray)
                .indexOf(userDocument.getData().get("persona").toString());
        personaSpinner.setSelection(personaIndex);
    }
}
