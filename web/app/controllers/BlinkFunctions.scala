package controllers

import ParameterTypes._
import AuxiliaryFormats._
import scala.collection.JavaConversions._
import blink.{GBlink, App, BlinkDrawing}
import java.awt.geom.GeneralPath
import controllers.{ParameterTypes => T}

object FunctionBlinkPositions extends FunctionBridge[GBlink, (Seq[(Double,Double)],Seq[(GeneralPath, Boolean)])] {
  def name = "Blink Positions"
  def parametersDesc = Seq (
    T.Gem -> "Blink" -> ""
  )
  def returns = T.Anything -> "The position for each vertex and path for each edges"

  def apply(b: GBlink) = {
    val blinkDrawing = new BlinkDrawing(b, 2, -1)
    val vertexPositions = new collection.mutable.ListBuffer[(Double,Double)]
    for ((v, p) <- blinkDrawing.getMapVertices) {
      vertexPositions += ((p.getX(), p.getY()))
    }
    val edges = new collection.mutable.ListBuffer[(GeneralPath, Boolean)] 
    for ((v, p) <- blinkDrawing.getMapEdges) {
      edges += ((p.getPath, p.getColor != java.awt.Color.green))
    }
    (vertexPositions, edges)
  }
}

object FunctionBlinkFromDB extends FunctionBridge[Int, GBlink] {
  def name = "Blink from DB"
  def parametersDesc = Seq (
    T.Int -> "ID" -> ""
  )
  def returns = T.Blink -> "Blink with given ID"

  def apply(id: Int): GBlink = {
    App.getRepositorio().getBlinksByIDs(id).get(0).getBlink
  }
}



