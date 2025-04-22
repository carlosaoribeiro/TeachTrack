package com.carlosribeiro.teachtrack.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosribeiro.teachtrack.R;
import com.carlosribeiro.teachtrack.model.Aula;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class AulaAdapter extends RecyclerView.Adapter<AulaAdapter.AulaViewHolder> {

    private final List<Aula> listaAulas;
    private OnAulaLongClickListener longClickListener;

    public interface OnAulaLongClickListener {
        void onAulaLongClick(Aula aula);
    }

    public void setOnAulaLongClickListener(OnAulaLongClickListener listener) {
        this.longClickListener = listener;
    }

    public AulaAdapter(List<Aula> listaAulas) {
        this.listaAulas = listaAulas;
    }

    @NonNull
    @Override
    public AulaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aula, parent, false);
        return new AulaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AulaViewHolder holder, int position) {
        Aula aula = listaAulas.get(position);
        Context context = holder.itemView.getContext();

        holder.txtAluno.setText(aula.getAluno());
        holder.txtTipo.setText("Tipo: " + aula.getTipo());
        holder.tabelaHorarios.removeAllViews();

        holder.btnEditar.setOnClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onAulaLongClick(aula);
            }
        });

        holder.btnExcluir.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Deseja realmente excluir esta aula?")
                    .setPositiveButton("Sim", (dialog, which) -> excluirAula(aula, holder.getAdapterPosition(), context))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        if ("Mensal".equals(aula.getTipo())) {
            Map<String, String> mapa = aula.getHorariosSemana();

            if (mapa != null) {
                List<String> ordemReal = new ArrayList<>(mapa.keySet());
                ordemReal.sort(Comparator.comparing(dia -> getDataObjeto(getProximaData(dia))));

                for (String dia : ordemReal) {
                    TableRow linha = new TableRow(context);

                    TextView txtDia = new TextView(context);
                    txtDia.setText(capitalize(dia));
                    txtDia.setPadding(0, 0, 48, 0);
                    txtDia.setTextColor(Color.WHITE);

                    TextView txtHora = new TextView(context);
                    txtHora.setText(mapa.get(dia));
                    txtHora.setPadding(0, 0, 48, 0);
                    txtHora.setTextColor(Color.WHITE);

                    TextView txtData = new TextView(context);
                    txtData.setText(getProximaData(dia));
                    txtData.setTextColor(Color.WHITE);

                    linha.addView(txtDia);
                    linha.addView(txtHora);
                    linha.addView(txtData);
                    holder.tabelaHorarios.addView(linha);
                }
            }
        } else {
            TableRow linha = new TableRow(context);

            TextView txtData = new TextView(context);
            txtData.setText(aula.getData());
            txtData.setPadding(0, 0, 48, 0);
            txtData.setTextColor(Color.WHITE);

            TextView txtHora = new TextView(context);
            txtHora.setText(aula.getHora());
            txtHora.setTextColor(Color.WHITE);

            linha.addView(txtData);
            linha.addView(txtHora);
            holder.tabelaHorarios.addView(linha);
        }
    }

    private void excluirAula(Aula aula, int posicao, Context context) {
        FirebaseFirestore.getInstance().collection("aulas")
                .document(aula.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaAulas.remove(posicao);
                    notifyItemRemoved(posicao);
                    Toast.makeText(context, "Aula excluída com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erro ao excluir aula: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String capitalize(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private String getProximaData(String diaSemana) {
        Map<String, Integer> dias = new HashMap<>();
        dias.put("domingo", Calendar.SUNDAY);
        dias.put("segunda", Calendar.MONDAY);
        dias.put("terca", Calendar.TUESDAY);
        dias.put("quarta", Calendar.WEDNESDAY);
        dias.put("quinta", Calendar.THURSDAY);
        dias.put("sexta", Calendar.FRIDAY);
        dias.put("sabado", Calendar.SATURDAY);

        Calendar hoje = Calendar.getInstance();
        int hojeDia = hoje.get(Calendar.DAY_OF_WEEK);
        int alvo = dias.getOrDefault(diaSemana.toLowerCase(), Calendar.MONDAY);

        int diferenca = (alvo - hojeDia + 7) % 7;
        if (diferenca == 0) diferenca = 7;

        hoje.add(Calendar.DAY_OF_MONTH, diferenca);
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(hoje.getTime());
    }

    private Date getDataObjeto(String dataStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    @Override
    public int getItemCount() {
        return listaAulas.size();
    }

    static class AulaViewHolder extends RecyclerView.ViewHolder {
        TextView txtAluno, txtTipo, btnEditar;
        ImageButton btnExcluir;
        TableLayout tabelaHorarios;

        public AulaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAluno = itemView.findViewById(R.id.txtAluno);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            tabelaHorarios = itemView.findViewById(R.id.tabelaDiasHorarios);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
