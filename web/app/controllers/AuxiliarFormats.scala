package controllers

object AuxiliarFormats {
  private def getError(s: Seq[JsResult[Any]]) =
    s.reduce(_.asInstanceOf[JsError]++_.asInstanceOf[JsError]).asInstanceOf[JsError].errors

  implicit def Tuple2Format[A: Format, B: Format] = new Format[(A,B)] {
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
    def writes(o: (A,B)): JsValue = {
      Json.toJson(Seq(Json.toJson(o._1), Json.toJson(o._2)))
    }
  }

  implicit def Tuple3Format[A: Format, B: Format, C: Format] = new Format[(A,B,C)] {
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
    def writes(o: (A,B,C)): JsValue = {
      Json.toJson(Seq(Json.toJson(o._1), Json.toJson(o._2), Json.toJson(o._3)))
    }
  }
}
