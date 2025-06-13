package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class AddJedlo extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listViewJedla;
    private SearchView searchView;
    private ArrayList<Jedlo> jedlaList = new ArrayList<>();
    private ArrayList<Jedlo> filteredList = new ArrayList<>();
    private JedloAdapter adapter;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jedlo);

        listViewJedla = findViewById(R.id.listViewJedla);
        searchView = findViewById(R.id.searchView);

        adapter = new JedloAdapter(this, filteredList);
        listViewJedla.setAdapter(adapter);

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser != null ? currentUser.getUid() : null;

        databaseReference = FirebaseDatabase
                .getInstance("https://calio-cc034-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("jedla");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jedlaList.clear();
                for (DataSnapshot jedloSnapshot : snapshot.getChildren()) {
                    Jedlo jedlo = jedloSnapshot.getValue(Jedlo.class);
                    if (jedlo != null) {
                        jedlaList.add(jedlo);
                    }
                }
                updateFilteredList("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Chyba: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listViewJedla.setOnItemClickListener((parent, view, position, id) -> {
            Jedlo vybraneJedlo = filteredList.get(position);

            if (userId != null) {
                DatabaseReference userJedlaRef = FirebaseDatabase
                        .getInstance("https://calio-cc034-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("uzivatelia")
                        .child(userId)
                        .child("vybrate_jedla");

                userJedlaRef.push().setValue(vybraneJedlo)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddJedlo.this, "Jedlo uložené pre používateľa.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddJedlo.this, "Chyba pri ukladaní jedla.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            Intent intent = new Intent();
            intent.putExtra("vybrane_jedlo", vybraneJedlo);
            setResult(RESULT_OK, intent);
            finish();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateFilteredList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateFilteredList(newText);
                return true;
            }
        });
    }

    private void updateFilteredList(String query) {
        filteredList.clear();
        String lowerQuery = query.toLowerCase();

        for (Jedlo jedlo : jedlaList) {
            if (jedlo.getNazov().toLowerCase().contains(lowerQuery)) {
                filteredList.add(jedlo);
            }
        }

        adapter.notifyDataSetChanged();
    }
}
