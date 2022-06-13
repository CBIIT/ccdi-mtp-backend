package models.entities
import play.api.{Logger, Logging}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class PedCanNavObject(targetFromSourceId: String,
                  diseaseFromSourceMappedId: String,
                  Gene_symbol: String,
                  Disease: String,
                  SNV: Boolean,
                  CNV: Boolean,
                  Fusion: Boolean,
                  GeneExpression: Boolean,
                  id: String
                 )

case class PedCanNav(rows: Seq[PedCanNavObject])


object PedCanNavObject {
  implicit val pedCanNavObjectImpR: Reads[PedCanNavObject] = (
    (JsPath \ "targetFromSourceId").read[String] and
      (JsPath \ "diseaseFromSourceMappedId").read[String] and
      (JsPath \ "Gene_symbol").read[String] and
      (JsPath \ "Disease").read[String] and
      (JsPath \ "SNV").read[Boolean] and
      (JsPath \ "CNV").read[Boolean] and
      (JsPath \ "Fusion").read[Boolean] and
      (JsPath \ "GeneExpression").read[Boolean] and
      (JsPath \ "id").read[String]
  )(PedCanNavObject.apply _)
}

