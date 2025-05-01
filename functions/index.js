const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { defineSecret } = require("firebase-functions/params");
const sgMail = require("@sendgrid/mail");

// 🔒 Chave SendGrid via Secret Manager (não use .env nem local.properties)
const SENDGRID_KEY = defineSecret("SENDGRID_KEY");

admin.initializeApp();

exports.enviarConvitePorEmail = onDocumentCreated(
  {
    document: "aulas/{aulaId}",
    secrets: [SENDGRID_KEY],
  },
  async (event) => {
    sgMail.setApiKey(SENDGRID_KEY.value());

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

      const horaCompacta = aula.hora.replace(":", "").padEnd(4, "0");
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

      await admin.firestore().collection("aulas").doc(aulaId).update({
        conviteEnviado: true,
      });

    } catch (error) {
      console.error("❌ Erro ao enviar convite:", error.message);
    }
  }
);

// 🧠 Helpers
function formatarDataParaICS(data) {
  const [dia, mes, ano] = data.split('/');
  return `${ano}${mes.padStart(2, "0")}${dia.padStart(2, "0")}`;
}

function calcularHorarioFim(horaInicio, duracao, data) {
  const [dia, mes, ano] = data.split('/');
  const dataStr = `${ano}-${mes}-${dia}T${horaInicio}:00`;
  const dataObj = new Date(dataStr);

  if (isNaN(dataObj.getTime())) {
    throw new RangeError(`Invalid time value: ${dataStr}`);
  }

  dataObj.setMinutes(dataObj.getMinutes() + duracao);
  return dataObj.toISOString().slice(0, 16).replace(/[-:]/g, "");
}
