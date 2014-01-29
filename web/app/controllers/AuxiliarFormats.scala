package controllers

import play.api.libs.json._

object AuxiliarFormats {
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
  
  implicit def EitherReads[A: Reads, B: Reads] = new Reads[Either[A,B]] {
    def reads(json: JsValue): JsResult[Either[A,B]] = {
      Json.fromJson[A](json) match {
        case e1:JsError =>
          Json.fromJson[B](json) match {
            case e2:JsError =>
              JsError((e1++e2).errors);
            case y:JsSuccess[B] => JsSuccess(Right(y.get))
          }
        case x:JsSuccess[A] => JsSuccess(Left(x.get))
      }
    }
  }
}
