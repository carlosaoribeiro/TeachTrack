package com.carlosribeiro.teachtrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosribeiro.teachtrack.model.Aluno;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class AgendaActivity extends AppCompatActivity {

    private AutoCompleteTextView autoAluno;
    private EditText editEmailAluno, editDataAula, editHoraDiaria;
    private RadioGroup radioTipoAluno;
    private RadioButton radioMensal, radioDiario;
    private TextView txtLabelRecorrencia, labelHoraDiaria;
    private LinearLayout layoutDataAula;
    private TableLayout tabelaRecorrencia;
    private EditText editHoraSeg, editHoraTer, editHoraQua, editHoraQui, editHoraSex;
    private Button btnSalvarAula;

    private FirebaseFirestore db;
    private final Calendar calendario = Calendar.getInstance();
    private final Map<String, Aluno> mapaAlunosPorNome = new HashMap<>();
    private String aulaId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        db = FirebaseFirestore.getInstance();

        autoAluno = findViewById(R.id.autoAluno);
        editEmailAluno = findViewById(R.id.editEmailAluno);
        editDataAula = findViewById(R.id.editDataAula);
        layoutDataAula = findViewById(R.id.layoutDataAula);
        editHoraDiaria = findViewById(R.id.editHoraDiaria);
        labelHoraDiaria = findViewById(R.id.labelHoraDiaria);
        radioTipoAluno = findViewById(R.id.radioTipoAluno);
        radioDiario = findViewById(R.id.radioDiario);
        radioMensal = findViewById(R.id.radioMensal);
        txtLabelRecorrencia = findViewById(R.id.txtLabelRecorrencia);
        tabelaRecorrencia = findViewById(R.id.tabelaRecorrencia);
        editHoraSeg = findViewById(R.id.editHoraSeg);
        editHoraTer = findViewById(R.id.editHoraTer);
        editHoraQua = findViewById(R.id.editHoraQua);
        editHoraQui = findViewById(R.id.editHoraQui);
        editHoraSex = findViewById(R.id.editHoraSex);
        btnSalvarAula = findViewById(R.id.btnSalvarAula);

        configurarDatePicker();
        configurarTimePickers();
        configurarTipoAluno();
        carregarAlunos(); // 🔄 Agora com listener em tempo real!

        aulaId = getIntent().getStringExtra("aulaId");
        if (aulaId != null) preencherCamposEdicao();

        btnSalvarAula.setOnClickListener(v -> salvarAula());
    }

    private void configurarDatePicker() {
        editDataAula.setOnClickListener(v -> {
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(year, month, dayOfMonth);
                editDataAula.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(calendario.getTime()));
            }, ano, mes, dia).show();
        });
    }

    private void configurarTimePickers() {
        for (EditText campo : Arrays.asList(editHoraDiaria, editHoraSeg, editHoraTer, editHoraQua, editHoraQui, editHoraSex)) {
            campo.setOnClickListener(v -> {
                Calendar cal = Calendar.getInstance();
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                int minuto = cal.get(Calendar.MINUTE);

                new TimePickerDialog(this, (view, h, m) -> {
                    campo.setText(String.format(Locale.US, "%02d:%02d", h, m));
                }, hora, minuto, true).show();
            });
        }
    }

    private void configurarTipoAluno() {
        TextView labelDataAula = findViewById(R.id.labelDataAula);

        radioTipoAluno.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isMensal = checkedId == R.id.radioMensal;
            boolean isDiario = checkedId == R.id.radioDiario;

            txtLabelRecorrencia.setVisibility(isMensal ? View.VISIBLE : View.GONE);
            tabelaRecorrencia.setVisibility(isMensal ? View.VISIBLE : View.GONE);

            labelHoraDiaria.setVisibility(isDiario ? View.VISIBLE : View.GONE);
            editHoraDiaria.setVisibility(isDiario ? View.VISIBLE : View.GONE);

            labelDataAula.setVisibility(isDiario ? View.VISIBLE : View.GONE);
            editDataAula.setVisibility(isDiario ? View.VISIBLE : View.GONE);
        });
    }

    // 🔁 Atualizado para escutar alterações em tempo real
    private void carregarAlunos() {
        db.collection("alunos")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<String> nomes = new ArrayList<>();
                    mapaAlunosPorNome.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Aluno aluno = doc.toObject(Aluno.class);
                        aluno.setId(doc.getId());
                        String nomeCompleto = aluno.getNome() + " " + aluno.getSobrenome();
                        nomes.add(nomeCompleto);
                        mapaAlunosPorNome.put(nomeCompleto, aluno);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nomes);
                    autoAluno.setAdapter(adapter);
                    autoAluno.setThreshold(1);

                    autoAluno.setOnItemClickListener((parent, view, position, id) -> {
                        Aluno aluno = mapaAlunosPorNome.get(parent.getItemAtPosition(position).toString());
                        if (aluno != null) editEmailAluno.setText(aluno.getEmail());
                    });
                });
    }

    private void preencherCamposEdicao() {
        autoAluno.setText(getIntent().getStringExtra("aluno"));
        editEmailAluno.setText(getIntent().getStringExtra("email"));
        String tipo = getIntent().getStringExtra("tipo");

        if ("Mensal".equals(tipo)) {
            radioMensal.setChecked(true);
            Map<String, EditText> campos = new HashMap<>();
            campos.put("segunda", editHoraSeg);
            campos.put("terca", editHoraTer);
            campos.put("quarta", editHoraQua);
            campos.put("quinta", editHoraQui);
            campos.put("sexta", editHoraSex);

            for (Map.Entry<String, EditText> entry : campos.entrySet()) {
                String valor = getIntent().getStringExtra("horario_" + entry.getKey());
                if (valor != null) entry.getValue().setText(valor);
            }

        } else {
            radioDiario.setChecked(true);
            editDataAula.setText(getIntent().getStringExtra("data"));
            editHoraDiaria.setText(getIntent().getStringExtra("hora"));
        }
    }

    private void salvarAula() {
        String nomeAluno = autoAluno.getText().toString().trim();
        String emailAluno = editEmailAluno.getText().toString().trim();

        if (TextUtils.isEmpty(nomeAluno)) {
            mostrarDialogo("Erro", "Informe o nome do aluno.");
            return;
        }

        if (radioMensal.isChecked()) {
            Map<String, String> horarios = new HashMap<>();

            if (!TextUtils.isEmpty(editHoraSeg.getText())) horarios.put("segunda", editHoraSeg.getText().toString());
            if (!TextUtils.isEmpty(editHoraTer.getText())) horarios.put("terca", editHoraTer.getText().toString());
            if (!TextUtils.isEmpty(editHoraQua.getText())) horarios.put("quarta", editHoraQua.getText().toString());
            if (!TextUtils.isEmpty(editHoraQui.getText())) horarios.put("quinta", editHoraQui.getText().toString());
            if (!TextUtils.isEmpty(editHoraSex.getText())) horarios.put("sexta", editHoraSex.getText().toString());

            if (horarios.isEmpty()) {
                mostrarDialogo("Erro", "Informe ao menos um horário semanal.");
                return;
            }

            Calendar hoje = Calendar.getInstance();
            hoje.set(Calendar.HOUR_OF_DAY, 0);
            hoje.set(Calendar.MINUTE, 0);
            hoje.set(Calendar.SECOND, 0);
            hoje.set(Calendar.MILLISECOND, 0);

            Map<String, Integer> mapaIndices = new HashMap<>();
            mapaIndices.put("segunda", Calendar.MONDAY);
            mapaIndices.put("terca", Calendar.TUESDAY);
            mapaIndices.put("quarta", Calendar.WEDNESDAY);
            mapaIndices.put("quinta", Calendar.THURSDAY);
            mapaIndices.put("sexta", Calendar.FRIDAY);

            List<Map<String, Object>> aulasParaSalvar = new ArrayList<>();

            for (int i = 0; i < 28; i++) {
                Calendar data = (Calendar) hoje.clone();
                data.add(Calendar.DAY_OF_YEAR, i);

                for (Map.Entry<String, String> entry : horarios.entrySet()) {
                    String dia = entry.getKey();
                    String hora = entry.getValue();
                    int diaSemana = mapaIndices.get(dia);

                    if (data.get(Calendar.DAY_OF_WEEK) == diaSemana) {
                        Map<String, Object> aula = new HashMap<>();
                        aula.put("aluno", nomeAluno);
                        aula.put("email", emailAluno);
                        aula.put("tipo", "Mensal");
                        aula.put("data", new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data.getTime()));
                        aula.put("hora", hora);
                        aula.put("diaSemana", dia);
                        aulasParaSalvar.add(aula);
                    }
                }
            }

            for (Map<String, Object> aula : aulasParaSalvar) {
                db.collection("aulas").add(aula);
            }

            mostrarDialogo("Sucesso", "Aulas mensais salvas para as próximas 4 semanas!");

        } else {
            String dataAula = editDataAula.getText().toString().trim();
            String horaDiaria = editHoraDiaria.getText().toString().trim();

            if (TextUtils.isEmpty(dataAula) || TextUtils.isEmpty(horaDiaria)) {
                mostrarDialogo("Erro", "Informe a data e o horário da aula.");
                return;
            }

            Map<String, Object> dados = new HashMap<>();
            dados.put("aluno", nomeAluno);
            dados.put("email", emailAluno);
            dados.put("tipo", "Diário");
            dados.put("data", dataAula);
            dados.put("hora", horaDiaria);

            if (aulaId != null) {
                db.collection("aulas").document(aulaId)
                        .set(dados)
                        .addOnSuccessListener(aVoid -> mostrarDialogo("Sucesso", "Aula atualizada com sucesso!"))
                        .addOnFailureListener(e -> mostrarDialogo("Erro", "Falha ao atualizar aula: " + e.getMessage()));
            } else {
                db.collection("aulas").add(dados)
                        .addOnSuccessListener(doc -> mostrarDialogo("Sucesso", "Aula salva com sucesso!"))
                        .addOnFailureListener(e -> mostrarDialogo("Erro", "Erro ao salvar aula: " + e.getMessage()));
            }
        }
    }

    private void mostrarDialogo(String titulo, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (titulo.equals("Sucesso")) finish();
                })
                .show();
    }
}
