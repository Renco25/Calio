package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    TextView vybraneJedloTextView;
    FirebaseUser user;

    ArrayList<Jedlo> vybraneJedla = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        vybraneJedloTextView = findViewById(R.id.vybrane_jedlo);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddJedlo.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Jedlo jedlo = (Jedlo) data.getSerializableExtra("vybrane_jedlo");
            if (jedlo != null) {
                vybraneJedla.add(jedlo);
                zobrazVybraneJedla();
                Toast.makeText(this, "Pridané: " + jedlo.nazov, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void zobrazVybraneJedla() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < vybraneJedla.size(); i++) {
            Jedlo j = vybraneJedla.get(i);
            builder.append(i + 1).append(". ")
                    .append(j.nazov).append(" - ")
                    .append(j.kcal).append(" kcal\n");
        }
        vybraneJedloTextView.setText(builder.toString());
    }
}
