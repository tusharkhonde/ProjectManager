package com.sjsu.project.mail.agent;

public interface MailAgent {

	public boolean sendRegistrationMail(String name, String mailID, String subject,
			String msg);
	public boolean sendInvitationMail(String name, String mailID, String subject,
			String msg);

}
