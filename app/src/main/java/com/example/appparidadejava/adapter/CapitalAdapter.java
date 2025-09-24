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
import java.util.ArrayList;
import java.util.List;

public class CapitalAdapter extends RecyclerView.Adapter<CapitalAdapter.ViewHolder> {

    private List<Capital> listaExibida;
    private List<Capital> listaCompleta;
    private Context context;

    public CapitalAdapter(Context context, List<Capital> lista) {
        this.context = context;
        this.listaExibida = lista;
        this.listaCompleta = new ArrayList<>(lista);
    }

    public void updateData(List<Capital> novaLista) {
        listaExibida.clear();
        listaExibida.addAll(novaLista);
        listaCompleta.clear();
        listaCompleta.addAll(novaLista);
        notifyDataSetChanged();
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
        Capital capitalAtual = listaExibida.get(position);
        holder.txtNome.setText(capitalAtual.getNome());
        holder.txtEstado.setText(capitalAtual.getEstado());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("CAPITAL_SELECIONADA", capitalAtual);

            // ALTERAÇÃO: Envia o tempo de início para a próxima tela
            intent.putExtra("startTime", System.currentTimeMillis());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaExibida != null ? listaExibida.size() : 0;
    }

    public void filter(String text) {
        listaExibida.clear();
        if (text.isEmpty()) {
            listaExibida.addAll(listaCompleta);
        } else {
            text = text.toLowerCase();
            for (Capital item : listaCompleta) {
                if (item.getNome().toLowerCase().contains(text) || item.getEstado().toLowerCase().contains(text)) {
                    listaExibida.add(item);
                }
            }
        }
        notifyDataSetChanged();
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