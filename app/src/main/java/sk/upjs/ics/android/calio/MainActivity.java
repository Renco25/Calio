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

    ArrayList<Jedlo> vybraneJedla = new ArrayList<>();
    DatabaseReference userJedlaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        kalorieSpoluTextView = findViewById(R.id.kalorie_spolu);
        jedlaContainer = findViewById(R.id.jedla_container);
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        } else {
            textView.setText(user.getEmail());
            userJedlaRef = FirebaseDatabase
                    .getInstance("https://calio-cc034-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("uzivatelia")
                    .child(user.getUid())
                    .child("vybrate_jedla");

            nacitajVybrateJedla();
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
    }

    private void nacitajVybrateJedla() {
        userJedlaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vybraneJedla.clear();
                for (DataSnapshot jedloSnapshot : snapshot.getChildren()) {
                    Jedlo j = jedloSnapshot.getValue(Jedlo.class);
                    if (j != null) {
                        vybraneJedla.add(j);
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
        jedlaContainer.removeAllViews(); // vyčisti predchádzajúce položky
        int celkoveKalorie = 0;

        for (int i = 0; i < vybraneJedla.size(); i++) {
            Jedlo j = vybraneJedla.get(i);
            View itemView = LayoutInflater.from(this).inflate(R.layout.jedlo_item, jedlaContainer, false);

            TextView poradieText = itemView.findViewById(R.id.poradieTextView);
            TextView nazovText = itemView.findViewById(R.id.nazovJedlaTextView);
            TextView kalorieText = itemView.findViewById(R.id.kalorieTextView);

            poradieText.setText((i + 1) + ".");
            nazovText.setText(j.nazov);
            kalorieText.setText(j.kcal + " kcal");

            jedlaContainer.addView(itemView);
            celkoveKalorie += j.kcal;
        }

        kalorieSpoluTextView.setText(celkoveKalorie + "/1800 kcal");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            nacitajVybrateJedla();
        }
    }
}
