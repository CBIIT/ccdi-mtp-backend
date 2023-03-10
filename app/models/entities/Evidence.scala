package models.entities

import models.Backend
import models.gql.Fetchers.{
  diseasesFetcher,
  drugsFetcher,
  goFetcher,
  soTermsFetcher,
  targetsFetcher
}
import models.gql.Objects
import models.gql.Objects.{diseaseImp, drugImp, geneOntologyTermImp, targetImp}
import play.api.libs.json._
import sangria.schema.{
  Field,
  FloatType,
  ListType,
  LongType,
  ObjectType,
  OptionType,
  StringType,
  fields,
  BooleanType,
  IntType
}

object Evidence {

  import sangria.macros.derive._

  case class NameAndDescription(name: String, description: String)

  case class ValidationHypothesis(name: String, description: String, status: String)

  implicit val nameAndDescriptionJsonFormatImp: OFormat[NameAndDescription] =
    Json.format[NameAndDescription]
  implicit val validationHypothesisJsonFormatImp: OFormat[ValidationHypothesis] =
    Json.format[ValidationHypothesis]

  val nameAndDescriptionImp: ObjectType[Backend, NameAndDescription] =
    deriveObjectType[Backend, NameAndDescription](
      ObjectTypeName("NameDescription")
    )
  val validationHypothesisImp: ObjectType[Backend, ValidationHypothesis] =
    deriveObjectType[Backend, ValidationHypothesis](
      ObjectTypeName("ValidationHypothesis")
    )
  val pathwayTermImp: ObjectType[Backend, JsValue] = ObjectType(
    "Pathway",
    "Pathway entry",
    fields[Backend, JsValue](
      Field(
        "id",
        StringType,
        description = Some("Pathway ID"),
        resolve = js => (js.value \ "id").as[String]
      ),
      Field(
        "name",
        StringType,
        description = Some("Pathway Name"),
        resolve = js => (js.value \ "name").as[String]
      )
    )
  )

  val sequenceOntologyTermImp: ObjectType[Backend, JsValue] = ObjectType(
    "SequenceOntologyTerm",
    "Sequence Ontology Term",
    fields[Backend, JsValue](
      Field(
        "id",
        StringType,
        description = Some("Sequence Ontology ID"),
        resolve = js => (js.value \ "id").as[String]
      ),
      Field(
        "label",
        StringType,
        description = Some("Sequence Ontology Label"),
        resolve = js => (js.value \ "label").as[String]
      )
    )
  )

  val evidenceTextMiningSentenceImp: ObjectType[Backend, JsValue] = ObjectType(
    "EvidenceTextMiningSentence",
    fields[Backend, JsValue](
      Field("dEnd", LongType, description = None, resolve = js => (js.value \ "dEnd").as[Long]),
      Field("tEnd", LongType, description = None, resolve = js => (js.value \ "tEnd").as[Long]),
      Field("dStart", LongType, description = None, resolve = js => (js.value \ "dStart").as[Long]),
      Field("tStart", LongType, description = None, resolve = js => (js.value \ "tStart").as[Long]),
      Field(
        "section",
        StringType,
        description = None,
        resolve = js => (js.value \ "section").as[String]
      ),
      Field("text", StringType, description = None, resolve = js => (js.value \ "text").as[String])
    )
  )
  val evidenceDiseaseCellLineImp: ObjectType[Backend, JsValue] = ObjectType(
    "DiseaseCellLine",
    fields[Backend, JsValue](
      Field("id",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "id").asOpt[String]
      ),
      Field("name",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "name").asOpt[String]
      ),
      Field(
        "tissue",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "tissue").asOpt[String]
      ),
      Field(
        "tissueId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "tissueId").asOpt[String]
      )
    )
  )

  val evidenceVariationImp: ObjectType[Backend, JsValue] = ObjectType(
    "EvidenceVariation",
    "Sequence Ontology Term",
    fields[Backend, JsValue](
      Field(
        "functionalConsequence",
        OptionType(sequenceOntologyTermImp),
        description = None,
        resolve = js => {
          val soId = ((js.value \ "functionalConsequenceId").asOpt[String]).map(_.replace("_", ":"))
          soTermsFetcher.deferOpt(soId)
        }
      ),
      Field(
        "numberMutatedSamples",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "numberMutatedSamples").asOpt[Long]
      ),
      Field(
        "numberSamplesTested",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "numberSamplesTested").asOpt[Long]
      ),
      Field(
        "numberSamplesWithMutationType",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "numberSamplesWithMutationType").asOpt[Long]
      )
    )
  )

  val labelledElementImp: ObjectType[Backend, JsValue] = ObjectType(
    "LabelledElement",
    fields[Backend, JsValue](
      Field("id", StringType, description = None, resolve = js => (js.value \ "id").as[String]),
      Field(
        "label",
        StringType,
        description = None,
        resolve = js => (js.value \ "label").as[String]
      )
    )
  )

  val labelledUriImp: ObjectType[Backend, JsValue] = ObjectType(
    "LabelledUri",
    fields[Backend, JsValue](
      Field("url", StringType, description = None, resolve = js => (js.value \ "url").as[String]),
      Field(
        "niceName",
        StringType,
        description = None,
        resolve = js => (js.value \ "niceName").as[String]
      )
    )
  )

  val biomarkerGeneExpressionImp: ObjectType[Backend, JsValue] = ObjectType(
    "geneExpression",
    fields[Backend, JsValue](
      Field(
        "name",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "name").asOpt[String]
      ),
      Field(
        "id",
        OptionType(geneOntologyTermImp),
        description = None,
        resolve = js => {
          val goId = (js.value \ "id").asOpt[String].map(_.replace('_', ':'))
          goFetcher.deferOpt(goId)
        }
      )
    )
  )
  val biomarkerVariantImp: ObjectType[Backend, JsValue] = ObjectType(
    "variant",
    fields[Backend, JsValue](
      Field(
        "id",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "id").asOpt[String]
      ),
      Field(
        "name",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "name").asOpt[String]
      ),
      Field(
        "functionalConsequenceId",
        OptionType(sequenceOntologyTermImp),
        description = None,
        resolve = js => {
          val soId = (js.value \ "functionalConsequenceId").asOpt[String].map(_.replace("_", ":"))
          soTermsFetcher.deferOpt(soId)
        }
      )
    )
  )
  val biomarkersImp: ObjectType[Backend, JsValue] = ObjectType(
    "biomarkers",
    fields[Backend, JsValue](
      Field(
        "geneExpression",
        OptionType(ListType(biomarkerGeneExpressionImp)),
        description = None,
        resolve = js => (js.value \ "geneExpression").asOpt[Seq[JsValue]]
      ),
      Field(
        "variant",
        OptionType(ListType(biomarkerVariantImp)),
        description = None,
        resolve = js => (js.value \ "variant").asOpt[Seq[JsValue]]
      )
    )
  )

  val evidenceImp: ObjectType[Backend, JsValue] = ObjectType(
    "Evidence",
    "Evidence for a Target-Disease pair",
    fields[Backend, JsValue](
      Field(
        "id",
        StringType,
        description = Some("Evidence identifier"),
        resolve = js => (js.value \ "id").as[String]
      ),
      Field(
        "score",
        FloatType,
        description = Some("Evidence score"),
        resolve = js => (js.value \ "score").as[Double]
      ),
      Field(
        "target",
        targetImp,
        description = Some("Target evidence"),
        resolve = js => {
          val tId = (js.value \ "targetId").as[String]
          targetsFetcher.defer(tId)
        }
      ),
      Field(
        "disease",
        diseaseImp,
        description = Some("Disease evidence"),
        resolve = js => {
          val dId = (js.value \ "diseaseId").as[String]
          diseasesFetcher.defer(dId)
        }
      ),
      Field(
        "biomarkerName",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "biomarkerName").asOpt[String]
      ),
      Field(
        "biomarkers",
        OptionType(biomarkersImp),
        description = None,
        resolve = js => (js.value \ "biomarkers").asOpt[JsValue]
      ),
      Field(
        "diseaseCellLines",
        OptionType(ListType(evidenceDiseaseCellLineImp)),
        description = None,
        resolve = js => (js.value \ "diseaseCellLines").asOpt[Seq[JsValue]]
      ),
      Field(
        "cohortPhenotypes",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "cohortPhenotypes").asOpt[Seq[String]]
      ),
      Field(
        "targetInModel",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "targetInModel").asOpt[String]
      ),
      Field(
        "reactionId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "reactionId").asOpt[String]
      ),
      Field(
        "reactionName",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "reactionName").asOpt[String]
      ),
      Field(
        "projectId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "projectId").asOpt[String]
      ),
      Field(
        "variantId",
        OptionType(StringType),
        description = Some("Variant evidence"),
        resolve = js => (js.value \ "variantId").asOpt[String]
      ),
      Field(
        "variantRsId",
        OptionType(StringType),
        description = Some("Variant dbSNP identifier"),
        resolve = js => (js.value \ "variantRsId").asOpt[String]
      ),
      Field(
        "oddsRatioConfidenceIntervalLower",
        OptionType(FloatType),
        description = Some("Confidence interval lower-bound  "),
        resolve = js => (js.value \ "oddsRatioConfidenceIntervalLower").asOpt[Double]
      ),
      Field(
        "studySampleSize",
        OptionType(LongType),
        description = Some("Sample size"),
        resolve = js => (js.value \ "studySampleSize").asOpt[Long]
      ),
      Field(
        "variantAminoacidDescriptions",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "variantAminoacidDescriptions").asOpt[Seq[String]]
      ),
      Field(
        "mutatedSamples",
        OptionType(ListType(evidenceVariationImp)),
        description = None,
        resolve = js => (js.value \ "mutatedSamples").asOpt[Seq[JsValue]]
      ),
      Field(
        "drug",
        OptionType(drugImp),
        description = None,
        resolve = js => {
          val drugId = (js.value \ "drugId").asOpt[String]
          drugsFetcher.deferOpt(drugId)
        }
      ),
      Field(
        "drugFromSource",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "drugFromSource").asOpt[String]
      ),
      Field(
        "drugResponse",
        OptionType(Objects.diseaseImp),
        description = None,
        resolve = js => {
          val efoId = (js.value \ "drugResponse").asOpt[String]
          diseasesFetcher.deferOpt(efoId)
        }
      ),
      Field(
        "cohortShortName",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "cohortShortName").asOpt[String]
      ),
      Field(
        "diseaseModelAssociatedModelPhenotypes",
        OptionType(ListType(labelledElementImp)),
        description = None,
        resolve = js => (js.value \ "diseaseModelAssociatedModelPhenotypes").asOpt[Seq[JsValue]]
      ),
      Field(
        "diseaseModelAssociatedHumanPhenotypes",
        OptionType(ListType(labelledElementImp)),
        description = None,
        resolve = js => (js.value \ "diseaseModelAssociatedHumanPhenotypes").asOpt[Seq[JsValue]]
      ),
      Field(
        "significantDriverMethods",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "significantDriverMethods").asOpt[Seq[String]]
      ),
      Field(
        "pValueExponent",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "pValueExponent").asOpt[Long]
      ),
      Field(
        "log2FoldChangePercentileRank",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "log2FoldChangePercentileRank").asOpt[Long]
      ),
      Field(
        "biologicalModelAllelicComposition",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "biologicalModelAllelicComposition").asOpt[String]
      ),
      Field(
        "confidence",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "confidence").asOpt[String]
      ),
      Field(
        "clinicalPhase",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "clinicalPhase").asOpt[Long]
      ),
      Field(
        "resourceScore",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "resourceScore").asOpt[Double]
      ),
      Field(
        "variantFunctionalConsequence",
        OptionType(sequenceOntologyTermImp),
        description = None,
        resolve = js => {
          val soId = ((js.value \ "variantFunctionalConsequenceId")
            .asOpt[String])
            .map(id => id.replace("_", ":"))
          soTermsFetcher.deferOpt(soId)
        }
      ),
      Field(
        "biologicalModelGeneticBackground",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "biologicalModelGeneticBackground").asOpt[String]
      ),
      Field(
        "urls",
        OptionType(ListType(labelledUriImp)),
        description = None,
        resolve = js => (js.value \ "urls").asOpt[Seq[JsValue]]
      ),
      Field(
        "literature",
        OptionType(ListType(StringType)),
        description = Some("list of pub med publications ids"),
        resolve = js => (js.value \ "literature").asOpt[Seq[String]]
      ),
      Field(
        "pubMedCentralIds",
        OptionType(ListType(StringType)),
        description = Some("list of central pub med publications ids"),
        resolve = js => (js.value \ "pmcIds").asOpt[Seq[String]]
      ),
      Field(
        "studyCases",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "studyCases").asOpt[Long]
      ),
      Field(
        "studyOverview",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "studyOverview").asOpt[String]
      ),
      Field(
        "allelicRequirements",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "allelicRequirements").asOpt[Seq[String]]
      ),
      Field(
        "datasourceId",
        StringType,
        description = None,
        resolve = js => (js.value \ "datasourceId").as[String]
      ),
      Field(
        "datatypeId",
        StringType,
        description = None,
        resolve = js => (js.value \ "datatypeId").as[String]
      ),
      Field(
        "oddsRatioConfidenceIntervalUpper",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "oddsRatioConfidenceIntervalUpper").asOpt[Double]
      ),
      Field(
        "clinicalStatus",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "clinicalStatus").asOpt[String]
      ),
      Field(
        "log2FoldChangeValue",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "log2FoldChangeValue").asOpt[Double]
      ),
      Field(
        "oddsRatio",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "oddsRatio").asOpt[Double]
      ),
      Field(
        "cohortDescription",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "cohortDescription").asOpt[String]
      ),
      Field(
        "publicationYear",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "publicationYear").asOpt[Long]
      ),
      Field(
        "diseaseFromSource",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "diseaseFromSource").asOpt[String]
      ),
      Field(
        "diseaseFromSourceId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "diseaseFromSourceId").asOpt[String]
      ),
      Field(
        "targetFromSourceId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "targetFromSourceId").asOpt[String]
      ),
      Field(
        "targetModulation",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "targetModulation").asOpt[String]
      ),
      Field(
        "textMiningSentences",
        OptionType(ListType(evidenceTextMiningSentenceImp)),
        description = None,
        resolve = js => (js.value \ "textMiningSentences").asOpt[Seq[JsValue]]
      ),
      Field(
        "studyId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "studyId").asOpt[String]
      ),
      Field(
        "clinicalSignificances",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "clinicalSignificances").asOpt[Seq[String]]
      ),
      Field(
        "cohortId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "cohortId").asOpt[String]
      ),
      Field(
        "pValueMantissa",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "pValueMantissa").asOpt[Double]
      ),
      Field(
        "pathways",
        OptionType(ListType(pathwayTermImp)),
        description = None,
        resolve = js => (js.value \ "pathways").asOpt[Seq[JsValue]]
      ),
      Field(
        "publicationFirstAuthor",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "publicationFirstAuthor").asOpt[String]
      ),
      Field(
        "alleleOrigins",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "alleleOrigins").asOpt[Seq[String]]
      ),
      Field(
        "biologicalModelId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "biologicalModelId").asOpt[String]
      ),
      Field(
        "biosamplesFromSource",
        OptionType(ListType(StringType)),
        description = None,
        resolve = js => (js.value \ "biosamplesFromSource").asOpt[Seq[String]]
      ),
      Field(
        "diseaseFromSourceMappedId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "diseaseFromSourceMappedId").asOpt[String]
      ),
      Field(
        "beta",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "beta").asOpt[Double]
      ),
      Field(
        "betaConfidenceIntervalLower",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "betaConfidenceIntervalLower").asOpt[Double]
      ),
      Field(
        "betaConfidenceIntervalUpper",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "betaConfidenceIntervalUpper").asOpt[Double]
      ),
      Field(
        "studyStartDate",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "studyStartDate").asOpt[String]
      ),
      Field(
        "studyStopReason",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "studyStopReason").asOpt[String]
      ),
      Field(
        "studyStopReasonCategories",
        OptionType(ListType(StringType)),
        description =
          Some("Predicted reason(s) why the study has been stopped based on studyStopReason"),
        resolve = js => (js.value \ "studyStopReasonCategories").asOpt[Seq[String]]
      ),
      Field(
        "targetFromSource",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "targetFromSource").asOpt[String]
      ),
      Field(
        "cellLineBackground",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "cellLineBackground").asOpt[String]
      ),
      Field(
        "contrast",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "contrast").asOpt[String]
      ),
      Field(
        "crisprScreenLibrary",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "crisprScreenLibrary").asOpt[String]
      ),
      Field(
        "cellType",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "cellType").asOpt[String]
      ),
      Field(
        "statisticalTestTail",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "statisticalTestTail").asOpt[String]
      ),
      Field(
        "interactingTargetFromSourceId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "interactingTargetFromSourceId").asOpt[String]
      ),
      Field(
        "phenotypicConsequenceLogFoldChange",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "phenotypicConsequenceLogFoldChange").asOpt[Double]
      ),
      Field(
        "phenotypicConsequenceFDR",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "phenotypicConsequenceFDR").asOpt[Double]
      ),
      Field(
        "phenotypicConsequencePValue",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "phenotypicConsequencePValue").asOpt[Double]
      ),
      Field(
        "geneticInteractionScore",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "geneticInteractionScore").asOpt[Double]
      ),
      Field(
        "geneticInteractionPValue",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "geneticInteractionPValue").asOpt[Double]
      ),
      Field(
        "geneticInteractionFDR",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "geneticInteractionFDR").asOpt[Double]
      ),
      Field(
        "biomarkerList",
        OptionType(ListType(nameAndDescriptionImp)),
        description = None,
        resolve = js => (js.value \ "biomarkerList").asOpt[Seq[NameAndDescription]]
      ),
      Field(
        "expectedConfidence",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "expectedConfidence").asOpt[String]
      ),
      Field(
        "projectDescription",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "projectDescription").asOpt[String]
      ),
      Field(
        "validationHypotheses",
        OptionType(ListType(validationHypothesisImp)),
        description = None,
        resolve = js => (js.value \ "validationHypotheses").asOpt[Seq[ValidationHypothesis]]
      ),
      Field(
        "geneInteractionType",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "geneInteractionType").asOpt[String]
      ),
      Field(
        "targetRole",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "targetRole").asOpt[String]
      ),
      Field(
        "interactingTargetRole",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "interactingTargetRole").asOpt[String]
      ),
      Field(
        "ancestry",
        OptionType(StringType),
        description = Some("Genetic origin of a population"),
        resolve = js => (js.value \ "ancestry").asOpt[String]
      ),
      Field(
        "ancestryId",
        OptionType(StringType),
        description = Some("Identifier of the ancestry in the HANCESTRO ontology"),
        resolve = js => (js.value \ "ancestryId").asOpt[String]
      ),
      Field(
        "statisticalMethod",
        OptionType(StringType),
        description = Some("The statistical method used to calculate the association"),
        resolve = js => (js.value \ "statisticalMethod").asOpt[String]
      ),
      Field(
        "statisticalMethodOverview",
        OptionType(StringType),
        description = Some("Overview of the statistical method used to calculate the association"),
        resolve = js => (js.value \ "statisticalMethodOverview").asOpt[String]
      ),
      Field(
        "studyCasesWithQualifyingVariants",
        OptionType(LongType),
        description = Some(
          "Number of cases in a case-control study that carry at least one allele of the qualifying variant"
        ),
        resolve = js => (js.value \ "studyCasesWithQualifyingVariants").asOpt[Long]
      ),
      Field(
        "variantHgvsId",
        OptionType(StringType),
        description = Some("Identifier in HGVS notation of the disease-causing variant"),
        resolve = js => (js.value \ "variantHgvsId").asOpt[String]
      ),
       Field(
        "releaseVersion",
        OptionType(StringType),
        description = Some("Release version"),
        resolve = js => (js.value \ "releaseVersion").asOpt[String]
      ),
      Field(
        "releaseDate",
        OptionType(StringType),
        description = Some("Release date"),
        resolve = js => (js.value \ "releaseDate").asOpt[String]
      ),
      Field(
        "warningMessage",
        OptionType(StringType),
        description = Some("Warning message"),
        resolve = js => (js.value \ "warningMessage").asOpt[String]
      ),
//Chop data fields
      Field("DeSampleGroup1",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup1").asOpt[String]),
      Field("DeSampleGroup1Count",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup1Count").asOpt[Double]),
       Field("DeSampleGroup1MeanTpm",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup1MeanTpm").asOpt[Double]),
       Field("DeSampleGroup2",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup2").asOpt[String]),
       Field("DeSampleGroup2Count",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup2Count").asOpt[Double]),
      Field("DeSampleGroup2MeanTpm",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "DeSampleGroup2MeanTpm").asOpt[Double]),
       Field("baseMeanExpression",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "baseMeanExpression").asOpt[Double]),
       Field("biosamplesFromSourceID",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "biosamplesFromSourceID").asOpt[String]),
       Field("comparisonId",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "comparisonId").asOpt[String]),
       Field("experimentId",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "experimentId").asOpt[String]),
       Field("log2FoldChangeAdjPValue",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "log2FoldChangeAdjPValue").asOpt[Double]),
      Field("log2FoldChangePvalue",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "log2FoldChangePvalue").asOpt[Double]),
      Field("log2FoldStdErr",
            OptionType(FloatType),
            description = None,
            resolve = js => (js.value \ "log2FoldStdErr").asOpt[Double]),
      Field("dataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Dataset").asOpt[String]),
      Field("Disease",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Disease").asOpt[String]),
      Field("EFO",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "EFO").asOpt[String]),
      Field("frequencyInOverallDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Frequency_in_overall_dataset").asOpt[String]),
      Field("frequencyInPrimaryTumors",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Frequency_in_primary_tumors").asOpt[String]),
      Field("frequencyInRelapseTumors",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Frequency_in_relapse_tumors").asOpt[String]),
      Field("geneFullName",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene_full_name").asOpt[String]),
      Field("geneSymbol",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene_symbol").asOpt[String]),
      Field("geneType",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene_type").asOpt[String]),
      Field("MONDO",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "MONDO").asOpt[String]),
      Field("OncoKBCancerGene",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "OncoKB_cancer_gene").asOpt[String]),
      Field("OncoKBOncogeneTSG",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "OncoKB_oncogene_TSG").asOpt[String]),
      Field("PMTL",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "PMTL").asOpt[String]),
       Field("variantCategory",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Variant_category").asOpt[String]),
      Field("variantType",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Variant_type").asOpt[String]),
      Field("pedcbioPedotMutationsPlotURL",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "PedcBio_PedOT_mutations_plot_URL").asOpt[String]),
      Field("pedcbioPedotOncoprintPlotURL",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "PedcBio_PedOT_oncoprint_plot_URL").asOpt[String]),
      Field("proteinEnsemblId",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Protein_Ensembl_ID").asOpt[String]),
      Field("proteinRefseqId",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Protein_RefSeq_ID").asOpt[String]),
      Field("totalMutationsOverSubjectsInDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Total_mutations_over_subjects_in_dataset").asOpt[String]),
      Field("totalAlterationsOverSubjectsInDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Total_alterations_over_subjects_in_dataset").asOpt[String]),
      Field("totalPrimaryTumorsMutatedOverPrimaryTumorsInDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Total_primary_tumors_mutated_over_primary_tumors_in_dataset").asOpt[String]),
      Field("totalRelapseTumorsMutatedOverRelapseTumorsInDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Total_relapse_tumors_mutated_over_relapse_tumors_in_dataset").asOpt[String]),
      Field("Alt_ID",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Alt_ID").asOpt[String]),
      Field("breakpointLocation",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "BreakpointLocation").asOpt[String]),
      Field("fusionName",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "FusionName").asOpt[String]),
      Field("fusionType",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Fusion_Type").asOpt[String]),
      Field("fusionAnno",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Fusion_anno").asOpt[String]),
      Field("gene1AAnno",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene1A_anno").asOpt[String]),
      Field("gene1BAnno",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene1B_anno").asOpt[String]),
      Field("gene2AAnno",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene2A_anno").asOpt[String]),
      Field("gene2BAnno",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene2B_anno").asOpt[String]),
      Field("genePosition",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Gene_Position").asOpt[String]),
      Field("kinaseDomainRetainedGene1A",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Kinase_domain_retained_Gene1A").asOpt[String]),
      Field("kinaseDomainRetainedGene1B",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Kinase_domain_retained_Gene1B").asOpt[String]),
      Field("Patients_in_dataset",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Patients_in_dataset").asOpt[Long]),
      Field("Primary_tumors_in_dataset",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Primary_tumors_in_dataset").asOpt[Long]),
      Field("Relapse_tumors_in_dataset",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Relapse_tumors_in_dataset").asOpt[Long]),
      Field("Total_alterations",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Total_alterations").asOpt[Long]),
      
      Field("reciprocalExistsEitherGeneKinase",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Reciprocal_exists_either_gene_kinase").asOpt[String]),
      Field("totalAlterationsOverPatientsInDataset",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Total_alterations_over_Patients_in_dataset").asOpt[String]),
      Field("Total_relapse_tumors_mutated",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Total_relapse_tumors_mutated").asOpt[Long]),
      Field("annots",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "annots").asOpt[String]),
      Field("Total_primary_tumors_mutated",
            OptionType(LongType),
            description = None,
            resolve = js => (js.value \ "Total_primary_tumors_mutated").asOpt[Long]),
      Field("hotspot",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "HotSpot").asOpt[String]),
      Field("polyphenImpact",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "PolyPhen_impact").asOpt[String]),
      Field("proteinChange",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Protein_change").asOpt[String]),
      Field("siftImpact",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "SIFT_impact").asOpt[String]),
      Field("vepImpact",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "VEP_impact").asOpt[String]),
      Field("variantIdHg38",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Variant_ID_hg38").asOpt[String]),
      Field("reciprocal_exists",
            OptionType(BooleanType),
            description = None,
            resolve = js => (js.value \ "reciprocal_exists").asOpt[Boolean]),
      Field("variantClassification",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "Variant_classification").asOpt[String]),
      Field("dbSNPId",
            OptionType(StringType),
            description = None,
            resolve = js => (js.value \ "dbSNP_ID").asOpt[String]),
        Field("transcriptId",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "transcript_id").asOpt[String]),
      Field("geneFeature",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "Gene_Feature").asOpt[String]),
      Field("medianTPM",
        OptionType(LongType),
        description = None,
        resolve = js => (js.value \ "Median_TPM").asOpt[Long]),
      Field("rnaCorrelation",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "RNA_Correlation").asOpt[Double]),
      Field("probeID",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "Probe_ID").asOpt[String]),
      Field("chromosome",
        OptionType(IntType),
        description = None,
        resolve = js => (js.value \ "Chromosome").asOpt[Int]),
      Field("location",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "Location").asOpt[String]),
      Field("betaQ1",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "Beta_Q1").asOpt[Double]),
      Field("betaQ2",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "Beta_Q2").asOpt[Double]),
      Field("betaMedian",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "Beta_Median").asOpt[Double]),
      Field("betaQ4",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "Beta_Q4").asOpt[Double]),
      Field("betaQ5",
        OptionType(FloatType),
        description = None,
        resolve = js => (js.value \ "Beta_Q5").asOpt[Double]),
      Field("chopUuid",
        OptionType(StringType),
        description = None,
        resolve = js => (js.value \ "chop_uuid").asOpt[String])
    )
  )
}