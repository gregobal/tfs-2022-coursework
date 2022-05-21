package eventus.service

import courier.Defaults.executionContext
import courier.{Envelope, Mailer, Text}
import eventus.common.AppConfig
import zio.{Accessible, Task, URLayer, ZLayer}

import javax.mail.internet.InternetAddress

trait EmailService {
  def send(to: String, subject: String, content: String): Task[Unit]
}

object EmailService extends Accessible[EmailService]

case class EmailServiceImpl(config: AppConfig) extends EmailService {
  val mailer: Mailer = Mailer(
    config.email.host,
    config.email.port
  ).auth(true)
    .as(
      config.email.user,
      config.email.password
    )
    .startTls(true)()

  override def send(
      to: String,
      subject: String,
      content: String
  ): Task[Unit] =
    Task
      .fromFuture(_ =>
        mailer(
          Envelope
            .from(new InternetAddress(config.email.user))
            .to(new InternetAddress(to))
            .subject(subject)
            .content(Text(content))
        )
      )
}

object EmailServiceImpl {
  val live: URLayer[AppConfig, EmailServiceImpl] =
    ZLayer.fromFunction(EmailServiceImpl(_))
}
