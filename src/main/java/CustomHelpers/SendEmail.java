package CustomHelpers;


import CustomUtils.Props;
import Models.Chapter;
import Models.Manga;
import Models.User;

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail implements Runnable {


    private Manga manga;
    private Chapter chapter;
    private List<User> users;

    @Override
    public void run() {
        String from = "carlosfebres972@gmail.com";
        String pass = "25803400";

        String subject = MessageFormat.format(Props.getProperty("email_subjet"), manga.getMangaName(), chapter.getChapterTitle());
        String body = MessageFormat.format(Props.getProperty("email_body"), manga.getMangaName(), chapter.getChapterTitle());

        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(props);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            for (User user : users) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
            }

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    public Manga getManga() {
        return manga;
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void send() {
        Thread thread = new Thread(this);
        thread.start();
    }
}