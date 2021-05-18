package models.entities

import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._


case class DoseAndType(
                        dosing: Seq[String],
                        `type`: Seq[String]
                      )

case class LocationAndSource(location: String, source: String)

case class TargetClass(id: Long, label: String, level: String)

case class LabelAndSource(label: String, source: String)

case class Tractability(id: String, modality: String, value: Boolean)

case class TargetTissue(
                         efoId: String,
                         label: String,
                         modelName: Option[String]
                       )

case class SafetyLiability(
                            assayDescription: Option[String],
                            assayFormat: Option[String],
                            assayType: Option[String],
                            datasource: String,
                            effects: Option[DoseAndType],
                            event: Option[String],
                            eventId: Option[String],
                            pmid: Option[String],
                            tissue: TargetTissue,
                            url: Option[String]
                          )

case class CancerHallmark(description: String, impact: String, label: String, pmid: Long)

case class HallmarkAttribute(name: String, description: String, pmid: Long)

case class Hallmarks(cancerHallmarks: Seq[CancerHallmark], attributes: Seq[HallmarkAttribute])

case class Homologue(
                      homologyType: String,
                      queryPercentageIdentity: Double,
                      speciesId: String,
                      speciesName: String,
                      targetGeneId: String,
                      targetGeneSymbol: String,
                      targetPercentageIdentity: Double
                    )

case class GenomicLocation(chromosome: String, start: Long, end: Long, strand: Int)

case class GeneOntology(id: String,
                        aspect: String,
                        evidence: String,
                        geneProduct: String,
                        source: String)

case class Tep(uri: String, name: String)

case class IdAndSource(id: String, source: String)

case class Constraint(
                       constraintType: String,
                       exp: Option[Double],
                       obs: Option[Long],
                       oe: Option[Double],
                       oeLower: Option[Double],
                       oeUpper: Option[Double],
                       score: Option[Double],
                       upperBin: Option[Long],
                       upperBin6: Option[Long],
                       upperRank: Option[Long]
                     )

case class Target(id: String,
                  alternativeGenes: Seq[String],
                  approvedSymbol: String,
                  approvedName: String,
                  biotype: String,
                  dbXrefs: Seq[IdAndSource],
                  functionDescriptions: Seq[String],
                  geneticConstraint: Seq[Constraint],
                  genomicLocation: GenomicLocation,
                  geneOntology: Seq[GeneOntology],
                  hallmarks: Option[Hallmarks],
                  homologues: Seq[Homologue],
                  proteinIds: Seq[IdAndSource],
                  safetyLiabilities: Seq[SafetyLiability],
                  subcellularLocations: Seq[LocationAndSource],
                  synonyms: Seq[LabelAndSource], // double check, this is name and symbol
                  targetClass: Seq[TargetClass],
                  tep: Option[Tep],
                  tractability: Seq[Tractability],
                  transcriptIds: Seq[String])

object Target extends Logging {

  implicit val cancerHallmarkImpW = Json.writes[CancerHallmark]
  implicit val cancerHallmarkImpR: Reads[CancerHallmark] =
    ((__ \ "description").read[String] and
      (__ \ "impact").read[String] and
      (__ \ "label").read[String] and
      (__ \ "pmid").read[Long]) (CancerHallmark.apply _)

  implicit val hallmarkAttributeImpW = Json.writes[HallmarkAttribute]
  implicit val hallmarkAttributeImpR: Reads[HallmarkAttribute] =
    ((__ \ "attribute_name").read[String] and
      (__ \ "description").read[String] and
      (__ \ "pmid").read[Long]) (HallmarkAttribute.apply _)

  implicit val hallmarksImpW = Json.writes[Hallmarks]
  implicit val hallmarksImpR: Reads[Hallmarks] =
    ((__ \ "cancerHallmarks").readWithDefault[Seq[CancerHallmark]](Seq.empty) and
      (__ \ "attributes").readWithDefault[Seq[HallmarkAttribute]](Seq.empty)) (Hallmarks.apply _)

  implicit val geneOntologyImpW = Json.writes[models.entities.GeneOntology]
  implicit val geneOntologyImpR: Reads[models.entities.GeneOntology] =
    ((__ \ "id").read[String] and
      (__ \ "aspect").read[String] and
      (__ \ "evidence").read[String] and
      (__ \ "geneProduct").read[String] and
      (__ \ "source").read[String]) (GeneOntology.apply _)

  implicit val tepImpF = Json.format[Tep]
  implicit val idAndSourceImpF = Json.format[IdAndSource]
  implicit val labelAndSourceImpF = Json.format[LabelAndSource]
  implicit val locationAndSourceImpF = Json.format[LocationAndSource]
  implicit val targetClassImpF = Json.format[TargetClass]
  implicit val tractabilityImpF = Json.format[Tractability]
  implicit val constraintImpF = Json.format[Constraint]
  implicit val homologueImpF = Json.format[Homologue]
  implicit val doseAndTypeImpF = Json.format[DoseAndType]
  implicit val tissueImpF = Json.format[TargetTissue]
  implicit val safetyLiabilityImpF = Json.format[SafetyLiability]
  implicit val genomicLocationImpW = Json.format[models.entities.GenomicLocation]

  implicit val targetImpW = Json.writes[models.entities.Target]
  implicit val targetImpR: Reads[models.entities.Target] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "alternativeGenes").readWithDefault[Seq[String]](Seq.empty) and
      (JsPath \ "approvedSymbol").read[String] and
      (JsPath \ "approvedName").read[String] and
      (JsPath \ "biotype").read[String] and
      (JsPath \ "dbXrefs").readWithDefault[Seq[IdAndSource]](Seq.empty) and
      (JsPath \ "functionDescriptions").readWithDefault[Seq[String]](Seq.empty) and
      (JsPath \ "constraint").readWithDefault[Seq[Constraint]](Seq.empty) and
      (JsPath \ "genomicLocation").read[GenomicLocation] and
      (JsPath \ "geneOntology").readWithDefault[Seq[GeneOntology]](Seq.empty) and
      (JsPath \ "hallmarks").readNullable[Hallmarks] and
      (JsPath \ "homologues").readWithDefault[Seq[Homologue]](Seq.empty) and
      (JsPath \ "proteinIds").readWithDefault[Seq[IdAndSource]](Seq.empty) and
      (JsPath \ "safetyLiabilities").readWithDefault[Seq[SafetyLiability]](Seq.empty) and
      (JsPath \ "subcellularLocations").readWithDefault[Seq[LocationAndSource]](Seq.empty) and
      (JsPath \ "synonyms").readWithDefault[Seq[LabelAndSource]](Seq.empty) and
      (JsPath \ "targetClass").readWithDefault[Seq[TargetClass]](Seq.empty) and
      (JsPath \ "tep").readNullable[Tep] and
      (JsPath \ "tractability").readWithDefault[Seq[Tractability]](Seq.empty) and
      (JsPath \ "transcriptIds").readWithDefault[Seq[String]](Seq.empty)
  )(Target.apply _)

  def fromJsValue(jObj: JsValue): Option[Target] = {
    /* apply transformers for json and fill the target
     start from internal objects and then map the external
     */
    val source = (__ \ '_source).json.pick
    jObj
      .transform(source)
      .asOpt
      .map(obj => {
        logger.debug(Json.prettyPrint(obj))
        obj.as[Target]
      })
  }
}
