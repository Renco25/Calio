package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    TextView kalorieSpoluTextView;
    LinearLayout jedlaContainer;
    FirebaseUser user;
    ImageView progressBarImageView;

    ArrayList<Jedlo> vybraneJedla = new ArrayList<>();
    ArrayList<String> jedloKeys = new ArrayList<>();

    DatabaseReference userJedlaRef;
    DatabaseReference userRef;

    int kalorickyLimit = 1800; // default, ak nie je zadaný

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        kalorieSpoluTextView = findViewById(R.id.kalorie_spolu);
        jedlaContainer = findViewById(R.id.jedla_container);
        progressBarImageView = findViewById(R.id.progressBarImageView);
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        } else {
            userRef = FirebaseDatabase
                    .getInstance("https://calio-cc034-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("uzivatelia")
                    .child(user.getUid());

            userJedlaRef = userRef.child("vybrate_jedla");

            nacitajKalorickyLimit();
        }

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddJedlo.class);
            startActivityForResult(intent, 1);
        });

        ImageView logoImage = findViewById(R.id.logo);
        logoImage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Profile_Activity.class);
            startActivity(intent);
        });
    }

    private void nacitajKalorickyLimit() {
        userRef.child("kaloricky_limit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer limit = snapshot.getValue(Integer.class);
                if (limit != null) {
                    kalorickyLimit = limit;
                }
                nacitajVybrateJedla();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Nepodarilo sa načítať limit", Toast.LENGTH_SHORT).show();
                nacitajVybrateJedla();
            }
        });
    }

    private void nacitajVybrateJedla() {
        userJedlaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vybraneJedla.clear();
                jedloKeys.clear();

                for (DataSnapshot jedloSnapshot : snapshot.getChildren()) {
                    Jedlo j = jedloSnapshot.getValue(Jedlo.class);
                    if (j != null) {
                        vybraneJedla.add(j);
                        jedloKeys.add(jedloSnapshot.getKey());
                    }
                }
                zobrazVybraneJedla();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Chyba pri načítavaní jedál: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void zobrazVybraneJedla() {
        jedlaContainer.removeAllViews();
        int celkoveKalorie = 0;

        for (int i = 0; i < vybraneJedla.size(); i++) {
            Jedlo j = vybraneJedla.get(i);
            String key = jedloKeys.get(i);

            View itemView = LayoutInflater.from(this).inflate(R.layout.jedlo_item, jedlaContainer, false);

            TextView nazovText = itemView.findViewById(R.id.nazovJedlaTextView);
            TextView kalorieText = itemView.findViewById(R.id.kalorieTextView);
            ImageButton deleteButton = itemView.findViewById(R.id.deleteButton);

            nazovText.setText(j.nazov);
            kalorieText.setText(j.kcal + " kcal");

            deleteButton.setOnClickListener(v -> {
                userJedlaRef.child(key).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Jedlo vymazané", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Chyba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            jedlaContainer.addView(itemView);
            celkoveKalorie += j.kcal;
        }

        kalorieSpoluTextView.setText(celkoveKalorie + "/" + kalorickyLimit + " kcal");

        // 🌡️ Nastavenie správneho progress baru podľa kalórií
        if (celkoveKalorie == 0) {
            progressBarImageView.setImageResource(R.drawable.light_progressbar);
        } else if (celkoveKalorie < kalorickyLimit / 2) {
            progressBarImageView.setImageResource(R.drawable.light_progressbar);
        } else if (celkoveKalorie >= kalorickyLimit / 2 && celkoveKalorie <= kalorickyLimit - 150) {
            progressBarImageView.setImageResource(R.drawable.medium_progressbar);
        } else if (Math.abs(celkoveKalorie - kalorickyLimit) <= 150) {
            progressBarImageView.setImageResource(R.drawable.progressbar);
        } else if (celkoveKalorie > kalorickyLimit + 150) {
            progressBarImageView.setImageResource(R.drawable.prekroceny_progressbar);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            nacitajVybrateJedla();
        }
    }
}
