package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView vybraneJedloTextView;
    TextView kalorieSpoluTextView;
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
        vybraneJedloTextView = findViewById(R.id.vybrane_jedlo);
        kalorieSpoluTextView = findViewById(R.id.kalorie_spolu);
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
        StringBuilder builder = new StringBuilder();
        int celkoveKalorie = 0;

        for (int i = 0; i < vybraneJedla.size(); i++) {
            Jedlo j = vybraneJedla.get(i);
            builder.append(i + 1).append(". ")
                    .append(j.nazov).append(" - ")
                    .append(j.kcal).append(" kcal\n");

            celkoveKalorie += j.kcal;
        }

        vybraneJedloTextView.setText(builder.toString());
        kalorieSpoluTextView.setText("Kalórie spolu: " + celkoveKalorie);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            nacitajVybrateJedla();
        }
    }
}
