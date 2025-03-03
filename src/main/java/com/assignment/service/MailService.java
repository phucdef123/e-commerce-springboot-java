package com.assignment.service;

import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@EnableScheduling
public interface MailService {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Mail {
		@Default
		private String from = "Shopdee <shopdee1@gmail.com>";
		private String to, cc, bcc, subject, body, filenames;
	}

	void send(Mail mail);

	default void send(String to, String subject, String body) {
		Mail mail = Mail.builder().to(to).subject(subject).body(body).build();
		this.send(mail);
	}

	void push(Mail mail);

	default void push(String to, String subject, String body) {
		this.push(Mail.builder().to(to).subject(subject).body(body).build());
	}
}
