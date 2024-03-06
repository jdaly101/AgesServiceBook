package com.agesinitiatives.servicebook;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    private boolean hasInfoDoc = false;
    private FirebaseUser user;
    private DocumentSnapshot userDocument;
    private String userDocId;
    private String TAG = "UserActivity";

    /* UI components */
    protected EditText editUserName;
    protected EditText editUserParish;
    protected Spinner archdioceseSpinner;
    protected Spinner personaSpinner;
    protected Button saveButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        editUserName = findViewById(R.id.editUserName);
        editUserParish = findViewById(R.id.editUserParish);
        saveButton = findViewById(R.id.userSaveButton);

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

        progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                hasInfoDoc = true;
                                setDocument(document);
                            }
                        } else {

                        }
                    }
                });

    }

    public void setDocById(String docId) {
        Log.i(TAG, "SetDocById: " + docId);
        db.collection("users")
                .whereEqualTo("userId", docId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        for (DocumentSnapshot document : task.getResult()) {
                            setDocument(document);
                        }
                    }
                });
    }

    public void saveUserInfo(View view) {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
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

        if (hasInfoDoc == false) {
            db.collection("users")
                    .add(userRecord)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            hasInfoDoc = true;
                            userDocId = documentReference.getId();
                            setDocById(userDocId);
                            progressBar.setVisibility(View.GONE);
                            saveButton.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            saveButton.setEnabled(true);
                        }
                    });

        } else {
            db.collection("users")
                    .document(userDocId)
                    .update(userRecord)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            saveButton.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            saveButton.setEnabled(true);
                        }
                    });
        }
    }

    private void setDocument(DocumentSnapshot document) {
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
