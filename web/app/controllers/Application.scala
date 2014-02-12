package controllers

import play.api._

import play.api.mvc._
import play.api.mvc.Results.{Ok, NotFound, InternalServerError}
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Routes

import play.api.data.Forms._
import AuxiliaryFormats.{Tuple2Writes => _, _}
import FunctionTree._

object FunctionTree {
  class FunctionTree
  case class FunctionLeaf(func: FunctionJsonBridge) extends FunctionTree
  case class FunctionNode(name: String, subTrees: Seq[FunctionTree]) extends FunctionTree
  implicit def toFunctionNode[A <% FunctionTree](t: (String, Seq[A])): FunctionTree =
    FunctionNode(t._1, (t._2 map {implicitly[FunctionTree](_)}))
  implicit def toFunctionLeaf(f: FunctionJsonBridge): FunctionTree = FunctionLeaf(f)
  
  
  implicit def FunctionTreeWrites = new Writes[FunctionTree]{
    def writes(t: FunctionTree): JsValue = t match {
      case FunctionLeaf(f) => Json.toJson(f)
      case FunctionNode(name, subTrees) => JsObject(Seq(
      	"name" -> JsString(name),
      	"subTrees" -> JsArray(subTrees map writes)
      ))
    }
  }
}

object Application extends Controller {
  val defaultRoot: Seq[FunctionTree] = Seq(
    "Gem Factory" -> Seq(
      FunctionGemFromDB
    ),
    "Gem Functions" -> Seq(
      FunctionGemVertexesPositions
    ),
    "Blink Factory" -> Seq(
      FunctionBlinkFromDB
    ),
    "Blink Functions" -> Seq(
      FunctionBlinkPositions
    ),
    "Examples and tests" -> Seq(
      FunctionExampleGetFromList
    )
  )
  
  def index = Action {
    Ok(views.html.index())
  }
  def getFunctions = Action {
    Ok(Json.toJson(defaultRoot))
  }
  def callFunction = Action { request =>
    val postData = request.body.asJson.get.as[Map[String, JsValue]]
    def jsonError(msg: String) = Json.toJson(Map("type" -> "Error", "msg" -> msg))
    try {
      val function = FunctionsPoll(postData("id").as[String])
      try {
        Ok(Json.toJson(Map(
          "type" -> JsString(function.returns._1.toString),
          "ret" -> function(postData("args"))
        )))
      } catch {
	      case e: JsResultException =>
	        Ok(jsonError("Invalid json data: " + e))
	      case e: ExpectedError =>
	        Ok(jsonError("Expected error: " + e))
	      case e: Exception =>
	        Ok(jsonError("Unexpected error: " + e))
      }
    } catch {
      case e: java.util.NoSuchElementException =>
        Ok(jsonError("Function not found"))
      case e: Exception =>
        Ok(jsonError("Unexpected error: " + e))
    }
  }
}
