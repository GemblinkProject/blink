package controllers

import play.api.libs.json.{Json, JsValue, Reads, Writes}
import AuxiliaryFormats._
import ParameterDesc._
import controllers.{ParameterTypes => T}

class ParameterType
object ParameterTypes {
	case object Boolean extends ParameterType
	case object Int extends ParameterType
	case object Float extends ParameterType
	case object String extends ParameterType
	case object BigString extends ParameterType
	case object Gem extends ParameterType
	case object Blink extends ParameterType
	case object Matrix extends ParameterType
	case object Anything extends ParameterType
}

object ParameterDesc {
  class ParameterDesc
  case class ElementWithDefaultDesc(`type`: ParameterType, name: String, desc: String, default: JsValue) extends ParameterDesc
  case class ElementDesc(`type`: ParameterType, name: String, desc: String) extends ParameterDesc
  case class SeqDesc(name: String, desc: String, seq: Seq[ParameterDesc]) extends ParameterDesc
}

object FunctionsPoll {
  val map: collection.mutable.Map[String, FunctionJsonBridge] = collection.mutable.Map()
  def update = map.update _
  def apply(id:String) = map(id)
}

abstract class FunctionJsonBridge {
  implicit def toSeqDesc[A <% ParameterDesc](x: ((String, String), Seq[A])) =
    SeqDesc(x._1._1, x._1._2, x._2 map {implicitly[ParameterDesc](_)})
  implicit def toElementDesc(x: ((ParameterType, String), String)) = ElementDesc(x._1._1, x._1._2, x._2)
  implicit def toElementWithDefaultDesc[A: Writes](x: (((ParameterType, String), A), String)) =
    ElementWithDefaultDesc(x._1._1._1, x._1._1._2, x._2, Json.toJson(x._1._2))
  
  case class ArgumentValue(name: String, value: JsValue) {
    def tuple = (name, value)
  }
  implicit def toArgumentValue[A: Writes](x: (String, A)) = ArgumentValue(x._1, Json.toJson(x._2))
  
  def id: String = name
  def name: String
  def parametersDesc: Seq[ParameterDesc]
  def returns: (ParameterType, String)
  def apply(json: JsValue): JsValue
  
  FunctionsPoll(id) = this
  val w = FunctionsPoll(id)
}

abstract class FunctionBridge[P: Reads, R: Writes] extends FunctionJsonBridge {
  type Parameters = P
  type Return = R
  
  def apply(x: Parameters): Return
  def apply(json: JsValue): JsValue = Json.toJson[R](apply(json.as[P]))
}

case class ExpectedError(error: String) extends Exception {
  override def toString = error
}

object FunctionExampleGetFromList extends FunctionBridge[(List[Int], Int), Int] {
  def name = "Get from list"
  def parametersDesc = Seq (
    "List" -> "A list of integers" -> Seq(
      T.Int -> "x" -> "An element"
    ),
    T.Int -> "Idx" -> 0 -> "The index of the element you want"
  )
  def returns = T.Int -> "The Idx-element of List (0-based)"

  def apply(x: Parameters) = (apply _) tupled x

  def apply(list: List[Int], idx: Int): Int = {
    if (idx < 0 || list.size <= idx) {
      throw new ExpectedError("Index out of range")
    } else {
      list(idx)
    }
  }
}
