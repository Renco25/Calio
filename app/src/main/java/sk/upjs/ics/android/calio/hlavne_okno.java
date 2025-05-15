package sk.upjs.ics.android.calio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class hlavne_okno extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hlavne_okno);

        // Nájdi tlačidlo podľa ID
        Button btnPokracovat = findViewById(R.id.button);

        // Nastav listener na kliknutie
        btnPokracovat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Presmerovanie na Login aktivitu
                Intent intent = new Intent(hlavne_okno.this, Login.class);
                startActivity(intent);
                finish(); // Zavrie túto aktivitu, ak už nechceš vrátiť späť
            }
        });
    }
}
