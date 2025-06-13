package sk.upjs.ics.android.calio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class JedloAdapter extends ArrayAdapter<Jedlo> {

    public JedloAdapter(@NonNull Context context, @NonNull List<Jedlo> objects) {
        super(context, 0, objects);
    }

    static class ViewHolder {
        TextView textNazov;
        TextView textKcal;
        TextView textG;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Jedlo jedlo = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_jedlo, parent, false);
            holder = new ViewHolder();
            holder.textNazov = convertView.findViewById(R.id.textNazov);
            holder.textKcal = convertView.findViewById(R.id.textKcal);
            holder.textG = convertView.findViewById(R.id.textG);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (jedlo != null) {
            holder.textNazov.setText(jedlo.nazov != null ? jedlo.nazov : "Neznáme jedlo");
            holder.textKcal.setText(jedlo.kcal + " kcal");
            holder.textG.setText(" / " + jedlo.gramaz + " g");
        }

        return convertView;
    }
}
