package com.carlosribeiro.teachtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.R;
import com.carlosribeiro.teachtrack.model.Aula;
import com.carlosribeiro.teachtrack.model.ItemAgenda;

import java.util.List;

public class AgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ItemAgenda> itens;
    private OnAulaClickListener listener;

    public AgendaAdapter(List<ItemAgenda> itens) {
        this.itens = itens;
    }

    public interface OnAulaClickListener {
        void onEditar(Aula aula);
        void onExcluir(Aula aula);
    }

    public void setOnAulaClickListener(OnAulaClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return itens.get(position).getTipo();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemAgenda.TIPO_CABECALHO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_agenda_cabecalho, parent, false);
            return new CabecalhoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_agenda_aula, parent, false);
            return new AulaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemAgenda item = itens.get(position);

        if (item.getTipo() == ItemAgenda.TIPO_CABECALHO) {
            CabecalhoViewHolder vh = (CabecalhoViewHolder) holder;
            vh.txtData.setText(item.getData());
        } else {
            AulaViewHolder vh = (AulaViewHolder) holder;
            Aula aula = item.getAula();

            vh.txtNome.setText(aula.getAluno());

            // 🔥 Merge aqui: exibir formato "data - hora"
            String dataHoraFormatada = aula.getData() + " - " + aula.getHora();
            vh.txtHora.setText(dataHoraFormatada);

            vh.txtTipo.setText(aula.getTipo());

            vh.btnEditar.setOnClickListener(v -> {
                if (listener != null) listener.onEditar(aula);
            });

            vh.btnExcluir.setOnClickListener(v -> {
                if (listener != null) listener.onExcluir(aula);
            });
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    // ViewHolders
    static class CabecalhoViewHolder extends RecyclerView.ViewHolder {
        TextView txtData;
        public CabecalhoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtData = itemView.findViewById(R.id.txtCabecalhoData);
        }
    }

    static class AulaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtHora, txtTipo, btnEditar, btnExcluir;
        public AulaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeAluno);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
