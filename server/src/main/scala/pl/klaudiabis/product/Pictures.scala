package pl.klaudiabis.product

case class Pictures(pictureUrls: List[String]) extends AnyVal {

  def withNewPicture(pictureUrl: String): Pictures = copy(pictureUrls = pictureUrls :+ pictureUrl)

  def urls(): List[String] = pictureUrls

}

object Pictures {
  val empty: Pictures = Pictures(List.empty)
}
