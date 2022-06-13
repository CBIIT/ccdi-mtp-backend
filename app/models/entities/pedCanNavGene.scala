package models.entities
import play.api.{Logger, Logging}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class PedCanNavGeneObject(key: String,
							doc_count: Int)

case class PedCanNavGene(rows: Seq[PedCanNavGeneObject])

object PedCanNavGeneObject {
  implicit val pedCanNavGeneObjectImpR: Reads[PedCanNavGeneObject] = (
    (JsPath \ "key").read[String] and
     (JsPath \ "doc_count").read[Int] 
  )(PedCanNavGeneObject.apply _)
}

