const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const sgMail = require("@sendgrid/mail");

admin.initializeApp();

// 🔐 SendGrid API Key (deve ser movida para Firebase Config em produção)
sgMail.setApiKey(
  "SG.82HTvVDdQt2my8jJTwYbYg." +
  "wGUda1XPrBBTMME_qBbc3MikGJDRpepE383m5bSE4oM"
);

/**
 * Função dispara ao criar nova aula no Firestore
 */
exports.enviarConvitePorEmail = onDocumentCreated("aulas/{aulaId}", async (event) => {
  const aula = event.data.data();
  const aulaId = event.params.aulaId;

  if (aula.conviteEnviado) {
    console.log("🚫 Convite já havia sido enviado.");
    return;
  }

  try {
    if (!aula.data || !aula.hora) {
      throw new Error("❌ Aula sem data ou hora definida.");
    }

    const horaCompacta = aula.hora.replace(":", "").padEnd(4, "0"); // ex: "08:30" → "0830"
    const dtStart = `${formatarDataParaICS(aula.data)}T${horaCompacta}00`;
    const dtEnd = calcularHorarioFim(aula.hora, aula.duracao || 60, aula.data);

    const conteudoICS = `
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//TeachTrack//EN
BEGIN:VEVENT
SUMMARY:Aula de ${aula.tema || "Inglês"}
DESCRIPTION:Olá ${aula.aluno || aula.nomeAluno}, sua aula foi agendada.
DTSTART;TZID=America/Sao_Paulo:${dtStart}
DTEND;TZID=America/Sao_Paulo:${dtEnd}
LOCATION:Online
STATUS:CONFIRMED
END:VEVENT
END:VCALENDAR`;

    const msg = {
      to: aula.email || aula.emailAluno,
      from: "crls.ribeiro.dev@gmail.com",
      subject: `Convite para aula de ${aula.tema || "Inglês"}`,
      text: `Olá ${aula.aluno || aula.nomeAluno}, sua aula foi agendada para o dia ${aula.data} às ${aula.hora}.`,
      html: `
        <p>Olá <strong>${aula.aluno || aula.nomeAluno}</strong>,</p>
        <p>Sua aula de <strong>${aula.tema || "Inglês"}</strong> está agendada! ✍️</p>
        <ul>
          <li><strong>Data:</strong> ${aula.data}</li>
          <li><strong>Horário:</strong> ${aula.hora}</li>
          <li><strong>Plataforma:</strong> Online</li>
        </ul>
        <p>Anexo você encontrará um convite de calendário. Basta abrir e adicionar à sua agenda. 🗓️</p>

        <p>
          <a href="https://wa.me/13466379674" target="_blank"
             style="
               display: inline-block;
               padding: 12px 20px;
               background-color: #25D366;
               color: white;
               text-decoration: none;
               border-radius: 6px;
               font-weight: bold;
             ">
             Falar com o professor no WhatsApp
          </a>
        </p>

        <p>Até a aula!<br>
        Equipe <strong>TeachTrack</strong> 🚀</p>
      `,
      attachments: [
        {
          content: Buffer.from(conteudoICS).toString("base64"),
          filename: "aula.ics",
          type: "text/calendar",
          disposition: "attachment",
        },
      ],
    };

    await sgMail.send(msg);
    console.log("✅ Convite enviado com sucesso para:", msg.to);

    // Marca como convite enviado
    await admin.firestore().collection("aulas").doc(aulaId).update({
      conviteEnviado: true,
    });
  } catch (error) {
    console.error("❌ Erro ao enviar convite:", error.message);
  }
});

/**
 * 🧠 Converte data "dd/MM/yyyy" para formato ICS "yyyyMMdd"
 */
function formatarDataParaICS(data) {
  const [dia, mes, ano] = data.split('/');
  return `${ano}${mes.padStart(2, "0")}${dia.padStart(2, "0")}`;
}

/**
 * ✅ Calcula o horário de fim no formato ICS (ex: 20250430T090000)
 */
function calcularHorarioFim(horaInicio, duracao, data) {
  if (!horaInicio || !data) {
    throw new Error("Dados de hora ou data estão ausentes.");
  }

  const [dia, mes, ano] = data.split('/');
  const dataStr = `${ano}-${mes}-${dia}T${horaInicio}:00`;
  const dataObj = new Date(dataStr);

  if (isNaN(dataObj.getTime())) {
    throw new RangeError(`Invalid time value: ${dataStr}`);
  }

  dataObj.setMinutes(dataObj.getMinutes() + duracao);

  // Converte para "yyyyMMddTHHmmss" sem hifens nem dois-pontos
  return dataObj.toISOString().slice(0, 16).replace(/[-:]/g, "");
}
