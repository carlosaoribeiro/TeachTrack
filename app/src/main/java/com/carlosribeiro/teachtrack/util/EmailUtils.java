package com.carlosribeiro.teachtrack.util;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EmailUtils {

    private static final String TAG = "EMAIL_ERROR";

    private static final String FROM_EMAIL = "crls.ribeiro.dev@gmail.com";
    private static final String FROM_NAME = "TeachTrack App";
    private static final String API_KEY = "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";

    public static void enviarConvitePorEmail(String emailAluno, String nomeAluno, String data, String hora) {
        new Thread(() -> {
            try {
                // 1. Criar conteúdo ICS
                String conteudoICS = gerarICS(nomeAluno, data, hora);
                String base64ICS = Base64.encodeToString(conteudoICS.getBytes(), Base64.NO_WRAP);

                // 2. Criar o JSON payload
                String mensagem = "Olá, segue em anexo o convite da aula.";
                SendGridPayload payload = new SendGridPayload(
                        FROM_EMAIL,
                        nomeAluno,
                        emailAluno,
                        mensagem,
                        base64ICS
                );

                String json = new Gson().toJson(payload);

                // 3. Enviar para API SendGrid
                URL url = new URL("https://api.sendgrid.com/v3/mail/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(json);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("EMAIL", "Código de resposta: " + responseCode);

            } catch (Exception e) {
                Log.e(TAG, "Erro ao enviar convite: ", e);
            }
        }).start();
    }

    private static String gerarICS(String nomeAluno, String data, String hora) {
        try {
            // 🧠 Parse da data e hora para formato ICS
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            Date dataHora = sdf.parse(data + " " + hora);

            SimpleDateFormat icsFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            icsFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

            Date fim = new Date(dataHora.getTime() + (60 * 60 * 1000)); // 1h depois

            return "BEGIN:VCALENDAR\n" +
                    "VERSION:2.0\n" +
                    "PRODID:-//TeachTrack//EN\n" +
                    "BEGIN:VEVENT\n" +
                    "SUMMARY:Aula agendada\n" +
                    "DESCRIPTION:Sua aula foi confirmada\n" +
                    "DTSTART;TZID=America/Sao_Paulo:" + icsFormat.format(dataHora) + "\n" +
                    "DTEND;TZID=America/Sao_Paulo:" + icsFormat.format(fim) + "\n" +
                    "STATUS:CONFIRMED\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR";
        } catch (Exception e) {
            Log.e(TAG, "Erro ao gerar ICS: ", e);
            return "";
        }
    }
}
