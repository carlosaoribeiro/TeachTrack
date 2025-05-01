package com.carlosribeiro.teachtrack.util;

import android.util.Base64;
import android.util.Log;
import com.carlosribeiro.teachtrack.BuildConfig;

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

    /**
     * Recupera a chave da SendGrid de forma segura via variável de ambiente (BuildConfig).
     * Isso exige que você configure no `build.gradle`:
     *
     * buildConfigField("String", "SENDGRID_KEY", "\"SUA_CHAVE_AQUI\"")
     */
    private static final String API_KEY = BuildConfig.SENDGRID_KEY;


    public static void enviarConvitePorEmail(String emailAluno, String nomeAluno, String data, String hora) {
        new Thread(() -> {
            try {
                String conteudoICS = gerarICS(nomeAluno, data, hora);
                String base64ICS = Base64.encodeToString(conteudoICS.getBytes(), Base64.NO_WRAP);

                String mensagem = "Olá, segue em anexo o convite da aula.";
                SendGridPayload payload = new SendGridPayload(
                        FROM_EMAIL,
                        nomeAluno,
                        emailAluno,
                        mensagem,
                        base64ICS
                );

                String json = new Gson().toJson(payload);

                URL url = new URL("https://api.sendgrid.com/v3/mail/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY); // ✅ Correto
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
