package com.carlosribeiro.teachtrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.carlosribeiro.teachtrack.model.Aluno;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.*;

public class AgendaActivity extends AppCompatActivity {

    private AutoCompleteTextView autoAluno;
    private EditText editEmailAluno, editDataAula, editHoraDiario;
    private RadioGroup radioTipoAluno;
    private RadioButton radioMensal, radioDiario;
    private TextView txtLabelRecorrencia;
    private TableLayout tabelaRecorrencia;
    private EditText editHoraSeg, editHoraTer, editHoraQua, editHoraQui, editHoraSex;
    private Button btnSalvarAula;

    private FirebaseFirestore db;
    private final Calendar calendario = Calendar.getInstance();
    private final Map<String, Aluno> mapaAlunosPorNome = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        db = FirebaseFirestore.getInstance();

        autoAluno = findViewById(R.id.autoAluno);
        editEmailAluno = findViewById(R.id.editEmailAluno);
        editDataAula = findViewById(R.id.editDataAula);
        editHoraDiario = findViewById(R.id.editHoraDiario);
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
        configurarRadioButtons();
        configurarHora(editHoraDiario);
        configurarHora(editHoraSeg);
        configurarHora(editHoraTer);
        configurarHora(editHoraQua);
        configurarHora(editHoraQui);
        configurarHora(editHoraSex);
        carregarAlunos();
        btnSalvarAula.setOnClickListener(v -> salvarAula());
    }

    private void configurarDatePicker() {
        editDataAula.setOnClickListener(v -> {
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                editDataAula.setText(sdf.format(calendario.getTime()));
            }, ano, mes, dia);

            dialog.show();
        });
    }

    private void configurarHora(EditText campo) {
        campo.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int hora = cal.get(Calendar.HOUR_OF_DAY);
            int minuto = cal.get(Calendar.MINUTE);

            TimePickerDialog picker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                campo.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute1));
            }, hora, minuto, true);
            picker.show();
        });
    }

    private void configurarRadioButtons() {
        radioTipoAluno.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMensal) {
                tabelaRecorrencia.setVisibility(View.VISIBLE);
                txtLabelRecorrencia.setVisibility(View.VISIBLE);
                editHoraDiario.setVisibility(View.GONE);
            } else {
                tabelaRecorrencia.setVisibility(View.GONE);
                txtLabelRecorrencia.setVisibility(View.GONE);
                editHoraDiario.setVisibility(View.VISIBLE);
            }
        });
    }

    private void carregarAlunos() {
        db.collection("alunos")
                .get()
                .addOnSuccessListener(result -> {
                    List<String> nomes = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : result) {
                        Aluno aluno = doc.toObject(Aluno.class);
                        aluno.setId(doc.getId());

                        String nome = aluno.getNome() != null ? aluno.getNome() : "";
                        String sobrenome = aluno.getSobrenome() != null ? aluno.getSobrenome() : "";
                        String nomeCompleto = nome + " " + sobrenome;

                        nomes.add(nomeCompleto.trim());
                        mapaAlunosPorNome.put(nomeCompleto.trim(), aluno);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, nomes);
                    autoAluno.setAdapter(adapter);
                    autoAluno.setThreshold(1);

                    autoAluno.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) autoAluno.showDropDown();
                    });

                    autoAluno.setOnItemClickListener((parent, view, position, id) -> {
                        String nomeSelecionado = parent.getItemAtPosition(position).toString();
                        Aluno alunoSelecionado = mapaAlunosPorNome.get(nomeSelecionado);
                        if (alunoSelecionado != null) {
                            editEmailAluno.setText(alunoSelecionado.getEmail());
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar alunos", Toast.LENGTH_SHORT).show());
    }

    private void salvarAula() {
        String nomeAluno = autoAluno.getText().toString().trim();
        String emailAluno = editEmailAluno.getText().toString().trim();
        String dataAula = editDataAula.getText().toString().trim();

        if (TextUtils.isEmpty(nomeAluno) || TextUtils.isEmpty(dataAula)) {
            Toast.makeText(this, "Preencha os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("aluno", nomeAluno);
        dados.put("email", emailAluno);

        if (radioMensal.isChecked()) {
            dados.put("tipo", "Mensal");
            Map<String, String> horarios = new HashMap<>();
            if (!TextUtils.isEmpty(editHoraSeg.getText())) horarios.put("segunda", editHoraSeg.getText().toString());
            if (!TextUtils.isEmpty(editHoraTer.getText())) horarios.put("terca", editHoraTer.getText().toString());
            if (!TextUtils.isEmpty(editHoraQua.getText())) horarios.put("quarta", editHoraQua.getText().toString());
            if (!TextUtils.isEmpty(editHoraQui.getText())) horarios.put("quinta", editHoraQui.getText().toString());
            if (!TextUtils.isEmpty(editHoraSex.getText())) horarios.put("sexta", editHoraSex.getText().toString());
            dados.put("horariosSemana", horarios);
        } else {
            dados.put("tipo", "Diário");
            dados.put("data", dataAula);
            dados.put("hora", editHoraDiario.getText().toString().trim());
        }

        db.collection("aulas")
                .add(dados)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Aula salva com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar aula: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
