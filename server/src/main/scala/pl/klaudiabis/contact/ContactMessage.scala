package pl.klaudiabis.contact

case class ContactMessage(name: String, email: String, subject: String, body: String)

case class ContactMessages(messages: List[ContactMessage]) extends AnyVal {

  def withNewProduct(message: ContactMessage): ContactMessages = copy(messages = messages :+ message)

  
}
