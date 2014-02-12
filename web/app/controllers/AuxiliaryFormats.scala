package controllers

import play.api.libs.json._
import ParameterDesc._
import blink.{Gem, GBlink}
import blink.GemPackedLabelling
import java.awt.Shape
import java.awt.geom.PathIterator

object AuxiliaryFormats {
  private def getError(s: Seq[JsResult[Any]]) =
    s.reduce(_.asInstanceOf[JsError]++_.asInstanceOf[JsError]).asInstanceOf[JsError].errors

  implicit def Tuple2Reads[A: Reads, B: Reads] = new Reads[(A,B)] {
    def reads(json: JsValue): JsResult[(A,B)] = json match {
      case JsArray(seq) =>
        if (seq.size != 2) return JsError("error.expected.jsarray.length=2")
        val a = Json.fromJson[A](seq(0))
        val b = Json.fromJson[B](seq(1))
        val s = Seq(a,b).filter(_.isInstanceOf[JsError])
        if (s.size != 0) return JsError(getError(s))
        JsSuccess((a.get, b.get))
      case _ => JsError("error.expected.jsarray")
    }
  }
  implicit def Tuple2Writes[A: Writes, B: Writes] = new Writes[(A,B)] {
    def writes(o: (A,B)): JsValue = {
      Json.toJson(Seq(Json.toJson(o._1), Json.toJson(o._2)))
    }
  }

  implicit def Tuple3Reads[A: Reads, B: Reads, C: Reads] = new Reads[(A,B,C)] {
    def reads(json: JsValue): JsResult[(A,B,C)] = json match {
      case JsArray(seq) =>
        if (seq.size != 3) return JsError("error.expected.jsarray.length=3")
        val a = Json.fromJson[A](seq(0))
        val b = Json.fromJson[B](seq(1))
        val c = Json.fromJson[C](seq(2))
        val s = Seq(a,b,c).filter(_.isInstanceOf[JsError])
        if (s.size != 0) return JsError(getError(s))
        JsSuccess((a.get, b.get, c.get))
      case _ => JsError("error.expected.jsarray")
    }
  }
  implicit def Tuple3Writes[A: Writes, B: Writes, C: Writes] = new Writes[(A,B,C)] {
    def writes(o: (A,B,C)): JsValue = {
      Json.toJson(Seq(Json.toJson(o._1), Json.toJson(o._2), Json.toJson(o._3)))
    }
  }
  implicit def GemReads = new Reads[Gem] {
    def reads(json: JsValue): JsResult[Gem] = json match {
      case JsString(s) => JsSuccess(new Gem(new GemPackedLabelling(s)))
      case _ => JsError("error.expected.jsstring")
    }
  }
  implicit def GemWrites = new Writes[Gem] {
    def writes(gem: Gem): JsValue = {
      JsString(gem.getCurrentLabelling.getLettersString(""))
    }
  }
  implicit def BlinkReads = new Reads[GBlink] {
    def reads(json: JsValue): JsResult[GBlink] = json match {
      case JsString(s) => JsSuccess(new GBlink(s))
      case _ => JsError("error.expected.jsstring")
    }
  }
  implicit def BlinkWrites = new Writes[GBlink] {
    def writes(blink: GBlink): JsValue = {
      JsString(blink.codeAndColors)
    }
  }
  implicit def ShapeWrites = new Writes[Shape] {
    def writes(shape: Shape): JsValue = {
      val iterator = shape.getPathIterator(null)
      var str = ""
      while (!iterator.isDone) {
        val coords = new Array[Double](6)
        def point(idx: Int) = coords(idx*2)+","+coords(idx*2+1)+" "
        val code = iterator.currentSegment(coords)
        if (code == PathIterator.SEG_CLOSE) {
          str += "z "
        } else if (code == PathIterator.SEG_CUBICTO) {
          str += "C"+point(0)+point(1)+point(2)
        } else if (code == PathIterator.SEG_LINETO) {
          str += "L"+point(0)
        } else if (code == PathIterator.SEG_MOVETO) {
          str += "M"+point(0)
        } else if (code == PathIterator.SEG_QUADTO) {
          str += "Q"+point(0)+point(1)
        } else {
          throw new Exception("Unexpected segment type in shape")
        }
        iterator.next
      }
      JsString(str)
    }
  }
  
  implicit def EitherReads[A: Reads, B: Reads] = new Reads[Either[A,B]] {
    def reads(json: JsValue): JsResult[Either[A,B]] = {
      Json.fromJson[A](json) match {
        case e1:JsError =>
          Json.fromJson[B](json) match {
            case e2:JsError => JsError((e1++e2).errors)
            case y:JsSuccess[B] => JsSuccess(Right(y.get))
          }
        case x:JsSuccess[A] => JsSuccess(Left(x.get))
      }
    }
  }
  
  implicit def ParameterDescWrites = new Writes[ParameterDesc]{
    def writes(par: ParameterDesc): JsValue = par match {
      case ElementWithDefaultDesc(t, name, desc, default) => JsObject(Seq(
        "type" -> JsString(t.toString),
        "name" -> JsString(name),
        "desc" -> JsString(desc),
        "default" -> default
      ))
      case ElementDesc(t, name, desc) => JsObject(Seq(
        "type" -> JsString(t.toString),
        "name" -> JsString(name),
        "desc" -> JsString(desc)
      ))
      case SeqDesc(name, desc, seq) => JsObject(Seq(
        "type" -> JsString("Seq"),
        "name" -> JsString(name),
        "desc" -> JsString(desc),
        "seq" -> JsArray(seq map writes)
      ))
    }
  }
  implicit def FunctionJsonBridgeWrites = new Writes[FunctionJsonBridge]{
    def writes(f: FunctionJsonBridge): JsValue = {
      JsObject(Seq(
        "id" -> JsString(f.id),
        "name" -> JsString(f.name),
        "parametersDesc" -> Json.toJson(f.parametersDesc),
        "returnDesc" -> JsString(f.returns._2),
        "returnType" -> JsString(f.returns._1.toString)
      ))
    }
  }
  
}
