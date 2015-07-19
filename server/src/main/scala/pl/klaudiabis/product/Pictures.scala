package pl.klaudiabis.product

import java.util.UUID

case class Picture(id: String, bytes: Array[Byte])

case class Pictures(pictures: List[Picture]) {

  def withNewPicture(picture: Picture): Pictures = copy(pictures = pictures :+ picture)

  def get(id: UUID): Option[Picture] = pictures.find(_.id == id)

}

object Pictures {
  val empty: Pictures = Pictures(List.empty)
}
