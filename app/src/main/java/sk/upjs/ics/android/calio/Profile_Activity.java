package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Profile_Activity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText editLimit;
    private Button saveBtn;
    private TextView emailText;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editLimit = findViewById(R.id.edit_limit);
        saveBtn = findViewById(R.id.save_limit_btn);
        emailText = findViewById(R.id.email_text);
        Button button2 = findViewById(R.id.button2);

        // Tlačidlo späť
        button2.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        if (user != null) {
            String email = user.getEmail();
            emailText.setText(email);

            // Klik na email zobrazí Toast
            emailText.setOnClickListener(v ->
                    Toast.makeText(Profile_Activity.this, "Email: " + email, Toast.LENGTH_SHORT).show()
            );

            userRef = FirebaseDatabase
                    .getInstance("https://calio-cc034-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("uzivatelia")
                    .child(user.getUid());

            // Načítaj existujúci limit
            userRef.child("kaloricky_limit").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer limit = snapshot.getValue(Integer.class);
                    if (limit != null) {
                        editLimit.setText(String.valueOf(limit));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile_Activity.this, "Nepodarilo sa načítať limit", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Uloženie limitu
        saveBtn.setOnClickListener(view -> {
            String limitText = editLimit.getText().toString().trim();
            if (!limitText.isEmpty()) {
                try {
                    int newLimit = Integer.parseInt(limitText);
                    userRef.child("kaloricky_limit").setValue(newLimit)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Profile_Activity.this, "Limit uložený", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Profile_Activity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(Profile_Activity.this, "Chyba: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                } catch (NumberFormatException e) {
                    Toast.makeText(Profile_Activity.this, "Neplatný formát čísla", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Profile_Activity.this, "Zadaj limit", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
