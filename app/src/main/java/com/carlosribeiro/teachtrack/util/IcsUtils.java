package com.carlosribeiro.teachtrack.util;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IcsUtils {

    public static File gerarArquivoIcs(Context context, String aluno, String tipo, String data, String hora) {
        try {
            String titulo = "Aula com " + aluno;
            String descricao = "Aula do tipo: " + tipo;
            String dtInicio = formatarDataHora(data, hora);
            String dtFim = formatarDataHora(data, somarMinutos(hora, 45));

            String conteudo = "BEGIN:VCALENDAR\n" +
                    "VERSION:2.0\n" +
                    "BEGIN:VEVENT\n" +
                    "SUMMARY:" + titulo + "\n" +
                    "DESCRIPTION:" + descricao + "\n" +
                    "DTSTART;TZID=America/Sao_Paulo:" + dtInicio + "\n" +
                    "DTEND;TZID=America/Sao_Paulo:" + dtFim + "\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR";

            File file = new File(context.getCacheDir(), "convite_aula.ics");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(conteudo.getBytes());
                fos.flush();
            }

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private static String formatarDataHora(String data, String hora) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = sdf.parse(data + " " + hora);
        SimpleDateFormat icsFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        return icsFormat.format(date);
    }

    private static String somarMinutos(String hora, int minutos) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = sdf.parse(hora);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutos);
        return sdf.format(cal.getTime());
    }
}
