package com.carlosribeiro.teachtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.carlosribeiro.teachtrack.R;
import com.carlosribeiro.teachtrack.model.Aula;

import java.text.SimpleDateFormat;
import java.util.*;

public class AulaAdapter extends RecyclerView.Adapter<AulaAdapter.AulaViewHolder> {

    private final List<Aula> listaAulas;

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
        holder.txtAluno.setText(aula.getAluno());
        holder.txtTipo.setText("Tipo: " + aula.getTipo());
        holder.tabelaHorarios.removeAllViews();

        if ("Mensal".equals(aula.getTipo())) {
            Map<String, String> mapa = aula.getHorariosSemana();
            List<String> ordem = Arrays.asList("segunda", "terca", "quarta", "quinta", "sexta");

            if (mapa != null && !mapa.isEmpty()) {
                for (String dia : ordem) {
                    if (mapa.containsKey(dia)) {
                        TableRow linha = new TableRow(holder.itemView.getContext());

                        TextView txtDia = new TextView(holder.itemView.getContext());
                        txtDia.setText(capitalize(dia));
                        txtDia.setPadding(0, 0, 48, 0);

                        TextView txtHora = new TextView(holder.itemView.getContext());
                        String hora = mapa.get(dia);
                        txtHora.setText(hora);
                        txtHora.setPadding(0, 0, 48, 0);

                        TextView txtData = new TextView(holder.itemView.getContext());
                        txtData.setText(getProximaData(dia));

                        linha.addView(txtDia);
                        linha.addView(txtHora);
                        linha.addView(txtData);
                        holder.tabelaHorarios.addView(linha);
                    }
                }
            }
        } else {
            TableRow linha = new TableRow(holder.itemView.getContext());

            TextView txtData = new TextView(holder.itemView.getContext());
            txtData.setText(aula.getData());
            txtData.setPadding(0, 0, 48, 0);

            TextView txtHora = new TextView(holder.itemView.getContext());
            txtHora.setText(aula.getHora());

            linha.addView(txtData);
            linha.addView(txtHora);
            holder.tabelaHorarios.addView(linha);
        }
    }

    @Override
    public int getItemCount() {
        return listaAulas.size();
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

    static class AulaViewHolder extends RecyclerView.ViewHolder {
        TextView txtAluno, txtTipo;
        TableLayout tabelaHorarios;

        public AulaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAluno = itemView.findViewById(R.id.txtAluno);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            tabelaHorarios = itemView.findViewById(R.id.tabelaDiasHorarios);
        }
    }
}
