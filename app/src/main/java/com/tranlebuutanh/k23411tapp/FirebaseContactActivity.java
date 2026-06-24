package com.tranlebuutanh.k23411tapp;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tranlebuutanh.dals.FirebaseContactDAO;
import com.tranlebuutanh.models.FirebaseContact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseContactActivity extends AppCompatActivity {

    private TextView txtConnectionStatus;
    private View viewStatusIndicator;
    private EditText edtContactId, edtContactName, edtContactPhone, edtContactEmail;
    private Button btnAdd, btnUpdate, btnDelete, btnClear;
    private ListView lvContacts;

    private DatabaseReference mDatabase;
    private DatabaseReference connectedRef;
    private ValueEventListener firebaseListener;
    private ValueEventListener connectivityListener;

    private ArrayList<FirebaseContact> contactList;
    private ArrayAdapter<FirebaseContact> adapter;

    private boolean isCurrentlyOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_contact);

        initViews();
        initData();
        setupFirebase();
        setupEvents();
    }

    private void initViews() {
        txtConnectionStatus = findViewById(R.id.txtConnectionStatus);
        viewStatusIndicator = findViewById(R.id.viewStatusIndicator);
        edtContactId = findViewById(R.id.edtContactId);
        edtContactName = findViewById(R.id.edtContactName);
        edtContactPhone = findViewById(R.id.edtContactPhone);
        edtContactEmail = findViewById(R.id.edtContactEmail);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);
        lvContacts = findViewById(R.id.lvContacts);
    }

    private void initData() {
        contactList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        lvContacts.setAdapter(adapter);
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference("contacts");
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    }

    private void setupEvents() {
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseContact selected = contactList.get(position);
                edtContactId.setText(selected.getId());
                edtContactName.setText(selected.getName());
                edtContactPhone.setText(selected.getPhone());
                edtContactEmail.setText(selected.getEmail());
            }
        });

        btnAdd.setOnClickListener(v -> addContact());
        btnUpdate.setOnClickListener(v -> updateContact());
        btnDelete.setOnClickListener(v -> deleteContact());
        btnClear.setOnClickListener(v -> clearForm());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus(isOnline());

        connectivityListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class);
                updateStatus(connected);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        connectedRef.addValueEventListener(connectivityListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectedRef != null && connectivityListener != null) {
            connectedRef.removeEventListener(connectivityListener);
        }
        detachFirebaseListener();
    }

    private void updateStatus(boolean online) {
        isCurrentlyOnline = online;
        runOnUiThread(() -> {
            if (online) {
                txtConnectionStatus.setText("Status: ONLINE (Firebase Mode)");
                viewStatusIndicator.setBackgroundColor(Color.parseColor("#28A745"));
                syncPendingToFirebase();
                attachFirebaseListener();
            } else {
                txtConnectionStatus.setText("Status: OFFLINE (Local DB Mode)");
                viewStatusIndicator.setBackgroundColor(Color.parseColor("#DC3545"));
                detachFirebaseListener();
                loadLocalData();
            }
        });
    }

    private void syncPendingToFirebase() {
        ArrayList<FirebaseContact> pendingList = FirebaseContactDAO.getPendingContacts(this);
        if (pendingList.isEmpty()) return;

        Toast.makeText(this, "Syncing " + pendingList.size() + " pending contacts to Firebase...", Toast.LENGTH_SHORT).show();

        for (FirebaseContact contact : pendingList) {
            mDatabase.child(contact.getId()).setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        FirebaseContactDAO.markSynced(FirebaseContactActivity.this, contact.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FirebaseContactActivity.this, "Sync failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void attachFirebaseListener() {
        if (firebaseListener == null) {
            firebaseListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isCurrentlyOnline) return;

                    ArrayList<FirebaseContact> firebaseList = new ArrayList<>();
                    FirebaseContactDAO.clearAll(FirebaseContactActivity.this);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FirebaseContact contact = snapshot.getValue(FirebaseContact.class);
                        if (contact != null) {
                            if (contact.getId() == null) {
                                contact.setId(snapshot.getKey());
                            }
                            contact.setSyncStatus(FirebaseContact.SYNCED);
                            firebaseList.add(contact);
                            FirebaseContactDAO.saveContact(FirebaseContactActivity.this, contact);
                        }
                    }

                    contactList.clear();
                    contactList.addAll(firebaseList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(FirebaseContactActivity.this, "Firebase Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            mDatabase.addValueEventListener(firebaseListener);
        }
    }

    private void detachFirebaseListener() {
        if (mDatabase != null && firebaseListener != null) {
            mDatabase.removeEventListener(firebaseListener);
            firebaseListener = null;
        }
    }

    private void loadLocalData() {
        ArrayList<FirebaseContact> localList = FirebaseContactDAO.getContacts(this);
        contactList.clear();
        contactList.addAll(localList);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Loaded " + localList.size() + " contacts from local SQLite", Toast.LENGTH_SHORT).show();
    }

    private void addContact() {
        String id = edtContactId.getText().toString().trim();
        String name = edtContactName.getText().toString().trim();
        String phone = edtContactPhone.getText().toString().trim();
        String email = edtContactEmail.getText().toString().trim();

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCurrentlyOnline) {
            FirebaseContact contact = new FirebaseContact(id, name, phone, email, FirebaseContact.SYNCED);
            mDatabase.child(id).setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Contact added to Firebase", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            FirebaseContact contact = new FirebaseContact(id, name, phone, email, FirebaseContact.PENDING);
            FirebaseContactDAO.saveContact(this, contact);
            loadLocalData();
            Toast.makeText(this, "Saved offline - will sync when online", Toast.LENGTH_SHORT).show();
            clearForm();
        }
    }

    private void updateContact() {
        String id = edtContactId.getText().toString().trim();
        String name = edtContactName.getText().toString().trim();
        String phone = edtContactPhone.getText().toString().trim();
        String email = edtContactEmail.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Please select a contact to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCurrentlyOnline) {
            FirebaseContact contact = new FirebaseContact(id, name, phone, email, FirebaseContact.SYNCED);
            mDatabase.child(id).setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            FirebaseContact contact = new FirebaseContact(id, name, phone, email, FirebaseContact.PENDING);
            FirebaseContactDAO.saveContact(this, contact);
            loadLocalData();
            Toast.makeText(this, "Updated offline - will sync when online", Toast.LENGTH_SHORT).show();
            clearForm();
        }
    }

    private void deleteContact() {
        String id = edtContactId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Please select a contact to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCurrentlyOnline) {
            mDatabase.child(id).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Cannot delete offline", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        edtContactId.setText("");
        edtContactName.setText("");
        edtContactPhone.setText("");
        edtContactEmail.setText("");
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
