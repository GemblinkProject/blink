package controllers

import play.api.libs.json._
import AuxiliarFormats._

abstract class FunctionJsonBridge {
  def name: String
  def parameters: Map[String, Any]
  def returnDesc: String
  def apply(json: JsValue): JsValue
}

abstract class FunctionBridge[A: Reads, R: Writes] extends FunctionJsonBridge {
  type Arg = A
  type Return = R

  def apply(x: Arg): Return
  def apply(json: JsValue): JsValue = Json.toJson[R](apply(json.as[A]))
}

case class ExpectedError(error: String) extends Exception

object FunctionExampleGetFromList extends FunctionBridge[(List[Int], Int), Int] {
  def name = "Get from list (example)"
  def parameters = Map (
    "List" -> ("A list of integers", Map(
      'x' -> "An element"
    )),
    "Idx" -> "The index of the element you want"
  )
  def returnDesc = "The Idx-element of List"

  def apply(x: Arg) = ((apply _).tupled)(x)

  def apply(list: List[Int], idx: Int): Int = {
    if (idx < 0 || list.size <= idx) {
      throw new ExpectedError("Index out of range")
    } else {
      list(idx)
    }
  }
}

