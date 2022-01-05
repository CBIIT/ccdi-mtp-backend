package models.entities

import play.api.{Logger, Logging}
import play.api.libs.json._
import play.api.libs.json.Reads._

/*
{
  "targetFromSourceId": "ENSG00000000003",
  "diseaseFromSourceMappedId": "EFO_0000174",
  "Gene_symbol": "TSPAN6",
  "Disease": "Ewing sarcoma",
  "SNV": false,
  "CNV": true,
  "Fusion": false,
  "GeneExpression": true,
  "id": "08bc9958-5d58-11ec-92ac-acde48001122"
}
*/

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
  implicit val PedCanNavObjectImpReader: Reads[PedCanNavObject] = (
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

  implicit val PedCanNavImpReader: Reads[PedCanNav] = (
      (JsPath \ "rows").read[Seq[PedCanNavObject]]
  )(PedCanNav.apply _)
}
