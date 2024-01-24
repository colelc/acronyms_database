package edu.duke.fuqua.utils;

import java.sql.Date;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class EmailUtils {

	private static boolean TEST_WITH_BCC;
	private static String TESTER_EMAIL;
	private static String STEALTH;
	private static Logger log = Logger.getLogger(EmailUtils.class);

	static {
		try {
			TESTER_EMAIL = ConfigUtils.getProperty("tester.email");
			TEST_WITH_BCC = ConfigUtils.getPropertyAsBoolean("test.with.bcc");
			STEALTH = ConfigUtils.getProperty("stealth.email");
		} catch (Exception e) {
			TEST_WITH_BCC = false;
			log.error("Cannot acquire properties value for bcc email testing");
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static Session getSession() throws Exception {
		try {
			String EMAIL_SERVER = ConfigUtils.getProperty("email.server");
			Properties props = System.getProperties();
			props.put("mail.smtp.host", EMAIL_SERVER);

			return Session.getInstance(props, null);
		} catch (Exception e) {
			throw e;
		}
	}

	public static void sendEmail(boolean stealth, String subject, String body, String from, String... to) throws Exception {
		try {

			Message msg = new MimeMessage(getSession());

			msg.setSubject(subject);
			msg.setSentDate(new Date(System.currentTimeMillis()));

			msg.setFrom(new InternetAddress(from, "Case Real Estate Project"));
			for (String toEmail : Arrays.asList(to)) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			}

			if (msg.getRecipients(RecipientType.TO).length > 0) {
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				mimeBodyPart.setContent(body, "text/html; charset=UTF-8");

				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(mimeBodyPart);

				msg.setContent(multipart);
				Transport.send(msg);
				log.info((stealth ? STEALTH : "") + " Email sent from " + from + " to " + (Arrays.asList(to).stream().collect(Collectors.joining(", "))));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static void testerEmailCheck(String subject, String body, String from) throws Exception {
		try {
			if (TEST_WITH_BCC) {
				EmailUtils.sendEmail(true, subject, body, from, new String[] { TESTER_EMAIL });
			}
		} catch (Exception e) {
			throw e;
		}
	}

}
