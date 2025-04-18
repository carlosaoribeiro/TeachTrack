package com.carlosribeiro.teachtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.R;
import com.carlosribeiro.teachtrack.model.Aluno;

import java.util.ArrayList;
import java.util.List;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder> {

    private List<Aluno> listaAlunos;
    private List<Aluno> listaAlunosFiltrada;

    // 🔥 NOVO: listener para long click
    public interface OnAlunoLongClickListener {
        void onAlunoLongClick(Aluno aluno);
    }

    private OnAlunoLongClickListener longClickListener;

    public void setOnAlunoLongClickListener(OnAlunoLongClickListener listener) {
        this.longClickListener = listener;
    }

    public AlunoAdapter(List<Aluno> lista) {
        this.listaAlunos = lista;
        this.listaAlunosFiltrada = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aluno, parent, false);
        return new AlunoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoViewHolder holder, int position) {
        Aluno aluno = listaAlunosFiltrada.get(position);
        holder.txtNome.setText(aluno.getNome() + " " + aluno.getSobrenome());
        holder.txtEmail.setText(aluno.getEmail());

        // 🔥 NOVO: long click
        holder.itemView.setOnLongClickListener(view -> {
            if (longClickListener != null) {
                longClickListener.onAlunoLongClick(aluno);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaAlunosFiltrada.size();
    }

    public void filtrar(String texto) {
        listaAlunosFiltrada.clear();
        if (texto.isEmpty()) {
            listaAlunosFiltrada.addAll(listaAlunos);
        } else {
            for (Aluno aluno : listaAlunos) {
                String nomeCompleto = (aluno.getNome() + " " + aluno.getSobrenome()).toLowerCase();
                if (nomeCompleto.contains(texto.toLowerCase())) {
                    listaAlunosFiltrada.add(aluno);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class AlunoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEmail;

        public AlunoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.autoaluno);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}
