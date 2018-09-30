package com.shop.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {
	
	public static void sendMail(String email, String emailMsg) throws AddressException, MessagingException{
		// 1.����һ���������ʼ��������Ự���� Session
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "SMTP");
		properties.setProperty("mail.host", "smtp.163.com");
		properties.setProperty("mail.smtp.auth", "true");// ָ����֤Ϊtrue
		
		// ������֤��
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("kenanwaini@163.com", "a46495862");
			}
		};
		
		Session session = Session.getInstance(properties,authenticator);
		
		// 2.����һ��Message�����൱�����ʼ�����
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("kenanwaini@163.com")); // ���÷�����
		message.setRecipient(RecipientType.TO, new InternetAddress(email));
		message.setSubject("�û�����");
		message.setContent(emailMsg, "text/html;charset=utf-8");
		// 3.���� Transport���ڽ��ʼ�����
		Transport.send(message);
	}
	
}
