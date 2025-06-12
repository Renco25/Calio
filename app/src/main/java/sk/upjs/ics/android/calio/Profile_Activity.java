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

    FirebaseAuth auth;
    FirebaseUser user;
    EditText editLimit;
    Button saveBtn;
    TextView emailText;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        editLimit = findViewById(R.id.edit_limit);
        saveBtn = findViewById(R.id.save_limit_btn);
        emailText = findViewById(R.id.email_text);

        if (user != null) {
            emailText.setText(user.getEmail());

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

        saveBtn.setOnClickListener(view -> {
            String limitText = editLimit.getText().toString().trim();
            if (!limitText.isEmpty()) {
                int newLimit = Integer.parseInt(limitText);
                userRef.child("kaloricky_limit").setValue(newLimit)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Profile_Activity.this, "Limit uložený", Toast.LENGTH_SHORT).show();
                            // Späť do MainActivity
                            Intent intent = new Intent(Profile_Activity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(Profile_Activity.this, "Chyba: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(Profile_Activity.this, "Zadaj limit", Toast.LENGTH_SHORT).show();
            }
        });
    }
}