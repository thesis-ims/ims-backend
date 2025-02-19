package com.backend.ims.general.service;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Objects;


public class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private EmailService emailService;

  private static final String MAIL = "recipient@example.com";
  private static final String SUBJECT = "Test Subject";
  private static final String CONTENT = "Test Email Content";

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSendEmailWithValidParameters() {
    emailService.sendEmail(MAIL, SUBJECT, CONTENT);
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    Mockito.verify(mailSender).send(messageCaptor.capture());

    SimpleMailMessage sentMessage = messageCaptor.getValue();

    Assert.assertEquals(Objects.requireNonNull(sentMessage.getTo())[0], MAIL);
    Assert.assertEquals(sentMessage.getSubject(), SUBJECT);
    Assert.assertEquals(sentMessage.getText(), CONTENT);
  }

  @Test
  public void testSendEmailWithEmptySubject() {
    emailService.sendEmail(MAIL, "", CONTENT);
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    Mockito.verify(mailSender).send(messageCaptor.capture());

    Assert.assertEquals(messageCaptor.getValue().getSubject(), "");
  }

  @Test
  public void testSendEmailWithNullContent() {
    emailService.sendEmail(MAIL, SUBJECT, null);
    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    Mockito.verify(mailSender).send(messageCaptor.capture());

    Assert.assertNull(messageCaptor.getValue().getText());
  }
}
