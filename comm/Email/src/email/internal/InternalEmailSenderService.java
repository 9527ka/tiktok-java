package email.internal;

import email.sender.EmailMessage;

public interface InternalEmailSenderService {
	 /**
     *  邮件发送
     */
    public void send(EmailMessage emailMessage) throws Exception ;
}
