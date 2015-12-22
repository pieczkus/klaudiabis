package pl.klaudiabis.contact

case class SmtpConfig(tls: Boolean = false, ssl: Boolean = false, port: Int = 25, host: String, user: String, password: String)