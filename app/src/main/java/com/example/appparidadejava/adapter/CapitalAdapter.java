package com.example.appparidadejava.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appparidadejava.DetailActivity;
import com.example.appparidadejava.R;
import com.example.appparidadejava.model.Capital;

import java.util.List;

public class CapitalAdapter extends RecyclerView.Adapter<CapitalAdapter.ViewHolder> {

    private List<Capital> lista;
    private Context context;

    public CapitalAdapter(Context context, List<Capital> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_capital, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Capital c = lista.get(position);
        holder.txtNome.setText(c.getNome());
        holder.txtEstado.setText(c.getEstado());

        // Ao clicar no item, abre DetailActivity com extras
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("nome", c.getNome());
            intent.putExtra("estado", c.getEstado());
            intent.putExtra("latitude", c.getLatitude());
            intent.putExtra("longitude", c.getLongitude());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}
