package com.backendspringboot.email;

import com.backendspringboot.constant.EmailConstant;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {
    private Session getEmailSession(){
        //we will pass the constants that we define into this properties
        Properties properties = System.getProperties();
        properties.put(EmailConstant.SMTP_HOST, EmailConstant.GMAIL_SMTP_SERVER);
        properties.put(EmailConstant.SMTP_AUTH,true);
        properties.put(EmailConstant.SMTP_PORT, EmailConstant.DEFAULT_PORT);
        properties.put(EmailConstant.SMTP_STARTTLS_ENABLE,true);
        properties.put(EmailConstant.SMTP_STARTTLS_REQUIRED,true);
        return Session.getInstance(properties,null);
    }

    private Message createConfirmationEmail(String firstName, String email, String username) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username,false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL,false));
        message.setSubject(EmailConstant.EMAIL_SUBJECT_CONFIRMATION_ACCOUNT);
        message.setContent(email,"text/html; charset=utf-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Message createResetPasswordEmail(String firstName, String email, String username) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EmailConstant.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username,false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EmailConstant.CC_EMAIL,false));
        message.setSubject(EmailConstant.EMAIL_SUBJECT_RESET_PASSWORD);
        message.setContent(email,"text/html; charset=utf-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    public void sendNewPasswordEmail(String firstName,String email, String username) throws MessagingException {
        Message message = createResetPasswordEmail(firstName, email, username);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendConfirmationEmail(String firstName, String email, String username) throws MessagingException {
        Message message = createConfirmationEmail(firstName, email, username);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(EmailConstant.SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(EmailConstant.GMAIL_SMTP_SERVER, EmailConstant.USERNAME, EmailConstant.PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public String buildResetPasswordEmail(String name, String password){
        return "<body style=\"margin:0;padding:0;\">\n" +
                "  <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;background:#ededed;\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\" style=\"padding:0;\">\n" +
                "        <table role=\"presentation\" style=\"width:600px;border-collapse:collapse;border:1px solid #cccccc;border-spacing:0;text-align:left;\">\n" +
                "          <tr>\n" +
                "            <td style=\"padding:25px 0px 30px 20px;background:#303C54;\">\n" +
                "            <b> <p style=\"margin:0;font-size:25px;line-height:16px;font-family:Arial,sans-serif;color:#ffffff;\">Balance-tn</p></b>      \n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td style=\"padding:36px 30px 42px 30px;background:#ffffff;\">\n" +
                "              <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;\">\n" +
                "                <tr>\n" +
                "                  <td style=\"padding:0 0 36px 0;color:#153643;\">\n" +
                "<h1 style=\"font-size:40px;font-family:Arial,sans-serif;\">Reset Password</h1>\n" +
                "<p style=\"font-size:16px;line-height:24px;font-family:Arial,sans-serif;\">\n" +
                "Hi, "+name+"<br>\n" +
                "Your new password is: <b></b>\n"
                + password +
                "<br> <br>\n" +
                "Best Regards,\n" +
                "<br>\n" +
                "Balance-tn Team\n" +
                "</p>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td style=\"padding:27px;background:#DBDBDB;\">\n" +
                "              <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;font-size:9px;font-family:Arial,sans-serif;\">\n" +
                "                <tr>\n" +
                "                  <td style=\"padding:0;width:50%;\" align=\"left\">\n" +
                "                    <p style=\"margin:0;font-size:14px;line-height:16px;font-family:Arial,sans-serif;color:#00000;\">\n" +
                "                      \t&#169; CIMF 2022<br/>\n" +
                "                    </p>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>";
    }

    public String buildConfirmationEmail(String name, String link) {
        return "<body style=\"margin:0;padding:0;\">\n" +
                "  <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;background:#ededed;\">\n" +
                "    <tr>\n" +
                "      <td align=\"center\" style=\"padding:0;\">\n" +
                "        <table role=\"presentation\" style=\"width:600px;border-collapse:collapse;border:1px solid #cccccc;border-spacing:0;text-align:left;\">\n" +
                "          <tr>\n" +
                "            <td style=\"padding:25px 0px 30px 20px;background:#303C54;\">\n" +
                "            <b> <p style=\"margin:0;font-size:25px;line-height:16px;font-family:Arial,sans-serif;color:#ffffff;\">Balance-tn</p></b>      \n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td style=\"padding:36px 30px 42px 30px;background:#ffffff;\">\n" +
                "              <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;\">\n" +
                "                <tr>\n" +
                "                  <td style=\"padding:0 0 36px 0;color:#153643;\">\n" +
                "<h1 align=\"center\" style=\"font-size:45px;font-family:Arial,sans-serif;\">Thank you</h1>\n" +
                "<p style=\"margin:0 0 12px 0;font-size:16px;line-height:24px;font-family:Arial,sans-serif;\">\n" +
                "<b>Hi "+ name +", <br></b>\n" +
                "\n" +
                "Thanks for creating an account on Balance-tn, we'd like to remind you to <b>verify your Balance-tn profile</b>.\n" +
                "<br>\n" +
                "Please click on the below link to activate your account:\n" +
                "</p>\n" +
                "<br>\n" +
                "<p align=\"center\">\n" +
                "<a  style=\"font-size:17px;font-family:Arial,sans-serif;\n" +
                "background-color: #303C54;\n" +
                "      color: white;\n" +
                "      padding: 14px 25px;\n" +
                "      text-align: center;\n" +
                "      text-decoration: none;\n" +
                "      display: inline-block;\" \n" +
                "      href=\""+link+"\" target=\"_blank\">Active Account</a>\n" +
                "     \n" +
                "</p>\n" +
                " <br>\n" +
                "<p style=\"margin:0 0 0 0;font-size:16px;line-height:24px;font-family:Arial,sans-serif;\">\n" +
                "Link will expire in 60 minutes. <br>\n" +
                "Best Regards, <br>\n" +
                "Balance-tn Team\n" +
                "</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td style=\"padding:27px;background:#dbdbdb;\">\n" +
                "              <table role=\"presentation\" style=\"width:100%;border-collapse:collapse;border:0;border-spacing:0;font-size:9px;font-family:Arial,sans-serif;\">\n" +
                "                <tr>\n" +
                "                  <td style=\"padding:0;width:50%;\" align=\"left\">\n" +
                "                    <p style=\"margin:0;font-size:14px;line-height:16px;font-family:Arial,sans-serif;color:#000000;\">\n" +
                "                      \t&#169; CIMF 2022<br/>\n" +
                "                    </p>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "</body>\n" +
                "\n" +
                "\n" +
                "\n";
    }
}
