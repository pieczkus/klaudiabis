package pl.klaudiabis.contact

import akka.actor.Actor
import org.apache.commons.mail.{DefaultAuthenticator, HtmlEmail}

class ContactWorkExecutor(smtpConfig: SmtpConfig) extends Actor {

  override def receive: Receive = {
    case msg: ContactMessage =>

      val email = new HtmlEmail()
      email.setStartTLSEnabled(smtpConfig.tls)
      email.setSSLOnConnect(smtpConfig.ssl)
      email.setSmtpPort(smtpConfig.port)
      email.setHostName(smtpConfig.host)
      email.setAuthenticator(new DefaultAuthenticator(
        smtpConfig.user,
        smtpConfig.password
      ))
      email.setTextMsg(msg.body)

      email.addTo(smtpConfig.user)
        .setFrom(smtpConfig.user)
        .addReplyTo(msg.email, msg.name)
        .setSubject(msg.subject)
        .send()


      sender() ! ContactWorker.WorkComplete(s"${msg.name} is a being contacted back")
  }
}
