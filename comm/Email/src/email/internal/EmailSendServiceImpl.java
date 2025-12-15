package email.internal;

import java.io.File;
import java.util.Map;

import email.EmailSendService;
import email.sender.EmailMessage;
import email.sender.EmailMessageQueue;

public class EmailSendServiceImpl implements EmailSendService {


	@Override
	public void sendEmail(String tomail, String subject,String senderName, String content) {
		EmailMessageQueue.add( new EmailMessage(tomail,  subject,senderName, content, null, null, null,null));
		
	}

	@Override
	public void sendEmail(String tomail, String subject, String ftlname, Map<String, Object> map) {
		EmailMessageQueue.add( new EmailMessage( tomail,  subject, null,null, ftlname, map, null,null));
		
	}

	@Override
	public void sendEmail(String tomail, String subject, String content, String ftlname, Map<String, Object> map,
			File file, String filename) {
		EmailMessageQueue.add( new EmailMessage( tomail,  subject,null, content, ftlname, map, file,filename));
		
	}

}
