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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class AgendaActivity extends AppCompatActivity {

    private AutoCompleteTextView autoAluno;
    private EditText editEmailAluno, editDataAula, editHoraDiaria, editTema;
    private Switch switchTipoAluno;
    private TextView txtLabelRecorrencia, labelHoraDiaria, labelDataAula;
    private LinearLayout layoutDataAula;
    private LinearLayout tabelaRecorrencia;

    private Switch switchSeg, switchTer, switchQua, switchQui, switchSex;
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
        editTema = findViewById(R.id.editTema);

        labelHoraDiaria = findViewById(R.id.labelHoraDiaria);
        labelDataAula = findViewById(R.id.labelDataAula);
        switchTipoAluno = findViewById(R.id.switchTipoAluno);

        txtLabelRecorrencia = findViewById(R.id.txtLabelRecorrencia);
        tabelaRecorrencia = findViewById(R.id.tabelaRecorrencia);

        switchSeg = findViewById(R.id.switchSeg);
        switchTer = findViewById(R.id.switchTer);
        switchQua = findViewById(R.id.switchQua);
        switchQui = findViewById(R.id.switchQui);
        switchSex = findViewById(R.id.switchSex);

        editHoraSeg = findViewById(R.id.editHoraSeg);
        editHoraTer = findViewById(R.id.editHoraTer);
        editHoraQua = findViewById(R.id.editHoraQua);
        editHoraQui = findViewById(R.id.editHoraQui);
        editHoraSex = findViewById(R.id.editHoraSex);

        btnSalvarAula = findViewById(R.id.btnSalvarAula);

        configurarDatePicker();
        configurarTimePickers();
        configurarTipoAluno();
        configurarSwitchesDias();
        carregarAlunos();

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
        List<EditText> campos = Arrays.asList(
                editHoraDiaria, editHoraSeg, editHoraTer, editHoraQua, editHoraQui, editHoraSex
        );

        for (EditText campo : campos) {
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
        switchTipoAluno.setOnCheckedChangeListener((buttonView, isChecked) -> {
            txtLabelRecorrencia.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            tabelaRecorrencia.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            labelHoraDiaria.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
            editHoraDiaria.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
            layoutDataAula.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void configurarSwitchesDias() {
        configurarToggleDia(switchSeg, editHoraSeg);
        configurarToggleDia(switchTer, editHoraTer);
        configurarToggleDia(switchQua, editHoraQua);
        configurarToggleDia(switchQui, editHoraQui);
        configurarToggleDia(switchSex, editHoraSex);
    }

    private void configurarToggleDia(Switch sw, EditText campoHora) {
        campoHora.setEnabled(sw.isChecked());
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            campoHora.setEnabled(isChecked);
            if (!isChecked) campoHora.setText("");
        });
    }

    private void carregarAlunos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("alunos")
                .whereEqualTo("userId", userId)
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
            switchTipoAluno.setChecked(true);
        } else {
            switchTipoAluno.setChecked(false);
            editDataAula.setText(getIntent().getStringExtra("data"));
            editHoraDiaria.setText(getIntent().getStringExtra("hora"));
        }
    }

    private void salvarAula() {
        String nomeAluno = autoAluno.getText().toString().trim();
        String emailAluno = editEmailAluno.getText().toString().trim();
        String tema = editTema.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (TextUtils.isEmpty(nomeAluno)) {
            mostrarDialogo("Erro", "Informe o nome do aluno.");
            return;
        }

        if (switchTipoAluno.isChecked()) {
            salvarMensal(nomeAluno, emailAluno, tema, userId);
        } else {
            salvarDiario(nomeAluno, emailAluno, tema, userId);
        }
    }

    private void salvarMensal(String nomeAluno, String emailAluno, String tema, String userId) {
        Map<String, String> horarios = new HashMap<>();
        if (switchSeg.isChecked() && !TextUtils.isEmpty(editHoraSeg.getText())) horarios.put("segunda", editHoraSeg.getText().toString());
        if (switchTer.isChecked() && !TextUtils.isEmpty(editHoraTer.getText())) horarios.put("terca", editHoraTer.getText().toString());
        if (switchQua.isChecked() && !TextUtils.isEmpty(editHoraQua.getText())) horarios.put("quarta", editHoraQua.getText().toString());
        if (switchQui.isChecked() && !TextUtils.isEmpty(editHoraQui.getText())) horarios.put("quinta", editHoraQui.getText().toString());
        if (switchSex.isChecked() && !TextUtils.isEmpty(editHoraSex.getText())) horarios.put("sexta", editHoraSex.getText().toString());

        if (horarios.isEmpty()) {
            mostrarDialogo("Erro", "Ative e informe ao menos um horário semanal.");
            return;
        }

        Calendar hoje = Calendar.getInstance();
        Map<String, Integer> mapaIndices = new HashMap<>();
        mapaIndices.put("segunda", Calendar.MONDAY);
        mapaIndices.put("terca", Calendar.TUESDAY);
        mapaIndices.put("quarta", Calendar.WEDNESDAY);
        mapaIndices.put("quinta", Calendar.THURSDAY);
        mapaIndices.put("sexta", Calendar.FRIDAY);

        for (int i = 0; i < 28; i++) {
            Calendar data = (Calendar) hoje.clone();
            data.add(Calendar.DAY_OF_YEAR, i);

            for (Map.Entry<String, String> entry : horarios.entrySet()) {
                if (data.get(Calendar.DAY_OF_WEEK) == mapaIndices.get(entry.getKey())) {
                    String dataFormatada = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data.getTime());
                    Map<String, Object> aula = new HashMap<>();
                    aula.put("aluno", nomeAluno);
                    aula.put("email", emailAluno);
                    aula.put("tema", tema);
                    aula.put("tipo", "Mensal");
                    aula.put("data", dataFormatada);
                    aula.put("hora", entry.getValue());
                    aula.put("userId", userId);
                    db.collection("aulas").add(aula);
                }
            }
        }

        mostrarDialogo("Sucesso", "Aulas mensais salvas!");
    }

    private void salvarDiario(String nomeAluno, String emailAluno, String tema, String userId) {
        String dataAula = editDataAula.getText().toString().trim();
        String horaDiaria = editHoraDiaria.getText().toString().trim();

        if (TextUtils.isEmpty(dataAula) || TextUtils.isEmpty(horaDiaria)) {
            mostrarDialogo("Erro", "Informe a data e o horário.");
            return;
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("aluno", nomeAluno);
        dados.put("email", emailAluno);
        dados.put("tema", tema);
        dados.put("tipo", "Diário");
        dados.put("data", dataAula);
        dados.put("hora", horaDiaria);
        dados.put("userId", userId);

        db.collection("aulas").add(dados)
                .addOnSuccessListener(doc -> mostrarDialogo("Sucesso", "Aula diária salva!"))
                .addOnFailureListener(e -> mostrarDialogo("Erro", "Falha ao salvar aula."));
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
