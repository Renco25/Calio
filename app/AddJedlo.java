package sk.upjs.ics.android.calio;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddJedlo extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jedlo);

        // Referencia na uzol "jedla"
        databaseReference = FirebaseDatabase.getInstance().getReference("jedla");

        // Vytvorenie objektu typu Jedlo
        Jedlo jedlo = new Jedlo("Kuracie prsia", 165, 31, 0, 3.6);

        // Vygenerovanie jedinečného ID
        String id = databaseReference.push().getKey();

        // Zápis do databázy
        if (id != null) {
            databaseReference.child(id).setValue(jedlo)
                .addOnSuccessListener(unused ->
                    Toast.makeText(getApplicationContext(), "Úspešne uložené", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                    Toast.makeText(getApplicationContext(), "Chyba pri ukladaní", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getApplicationContext(), "Chyba: ID je null", Toast.LENGTH_SHORT).show();
        }
    }
}
