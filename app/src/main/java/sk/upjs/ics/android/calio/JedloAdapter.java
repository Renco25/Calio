package sk.upjs.ics.android.calio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class JedloAdapter extends ArrayAdapter<Jedlo> {

    public JedloAdapter(@NonNull Context context, @NonNull List<Jedlo> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Jedlo jedlo = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_jedlo, parent, false);
        }

        TextView textNazov = convertView.findViewById(R.id.textNazov);
        TextView textKcal = convertView.findViewById(R.id.textKcal);
        TextView textG = convertView.findViewById(R.id.textG);



        textNazov.setText(jedlo.nazov);
        textKcal.setText(jedlo.kcal + " kcal");
        textG.setText(" / " +jedlo.gramaz + " g");

        return convertView;
    }
}