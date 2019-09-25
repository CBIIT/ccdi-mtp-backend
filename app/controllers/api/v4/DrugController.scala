package controllers.api.v4

import javax.inject._
import models.Backend
import models.Entities.JSONImplicits._
import models.Entities.TargetsBody
import models.entities.APIErrorMessage
import models.entities.Drug.JSONImplicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DrugController @Inject()(implicit ec: ExecutionContext, backend: Backend, cc: ControllerComponents)
  extends AbstractController(cc) {

  // example from here https://github.com/nemoo/play-slick3-example/blob/master/app/controllers/Application.scala
  def byId(id:String) = Action.async { req =>
    for {
      drugs <- backend.getDrugs(Seq(id))
    } yield drugs.headOption match {
      case None => NotFound(Json.toJson(APIErrorMessage(NOT_FOUND, s"$id not found")))
      case Some(t) => Ok(Json.toJson(t))
    }
  }

  def byIds(ids: Seq[String]) = Action.async { req =>
    (req.method, ids) match {
      case ("POST", _ :: _) | ("GET", _ :: _) =>
        for {
          drugs <- backend.getDrugs(ids)
        } yield Ok(Json.toJson(drugs))

      case ("POST", Nil) =>
        req.body.asJson.map(_.as[TargetsBody]) match {
          case Some(body) =>
            for {
              drugs <- backend.getDrugs(body.ids)
            } yield Ok(Json.toJson(drugs))

          case None => Future.successful(
            BadRequest(Json.toJson(APIErrorMessage(BAD_REQUEST,
              s"body field `ids` must exist as a list of target ids"))))
        }

      case (_, _) =>
        Future.successful(
          BadRequest(Json.toJson(APIErrorMessage(BAD_REQUEST,
            s"parameter `ids` must contain at least one ensembl ID"))))
    }
  }
}