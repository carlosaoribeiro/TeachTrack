package com.carlosribeiro.teachtrack.util;

import java.util.*;

public class SendGridPayload {

    private final List<Personalization> personalizations;
    private final From from;
    private final List<Content> content;
    private final List<Attachment> attachments;

    public SendGridPayload(String fromEmail, String toName, String toEmail, String texto, String base64ICS) {
        this.from = new From(fromEmail);
        this.personalizations = Collections.singletonList(new Personalization(toName, toEmail));
        this.content = Collections.singletonList(new Content("text/plain", texto));
        this.attachments = Collections.singletonList(new Attachment(base64ICS, "aula.ics", "text/calendar"));
    }

    static class From {
        String email;

        public From(String email) {
            this.email = email;
        }
    }

    static class Personalization {
        List<Email> to;

        public Personalization(String name, String email) {
            this.to = Collections.singletonList(new Email(name, email));
        }

        static class Email {
            String name;
            String email;

            public Email(String name, String email) {
                this.name = name;
                this.email = email;
            }
        }
    }

    static class Content {
        String type;
        String value;

        public Content(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    static class Attachment {
        String content;
        String filename;
        String type;

        public Attachment(String content, String filename, String type) {
            this.content = content;
            this.filename = filename;
            this.type = type;
        }
    }
}
