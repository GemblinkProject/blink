package controllers

import AuxiliaryFormats._
import ParameterDesc._
import blink.Gem
import blink.GemColor
import blink.TuttesLayout
import blink.App
import scala.collection.JavaConversions._
import ParameterTypes._
import controllers.{ParameterTypes => T}

object FunctionGemVertexesPositions extends FunctionBridge[(Gem, Int), Seq[(Double,Double)]] {
  def name = "Gem Vertexes Positions"
  def parametersDesc = Seq (
    T.Gem -> "Gem" -> "",
    T.Int -> "Off-color" -> 0 -> "Integer representing the color that won't be considered in the layout"
  )
  def returns = T.Matrix -> "The position (X,Y) for each vertex"

  def apply(a: Parameters) = (apply _) tupled a
  def apply(gem: Gem, color: Int): Seq[(Double,Double)] = {
    val mapPositions = TuttesLayout.tutteLayout(gem, GemColor.getByNumber(color), 0,0,1,1).toMap;
    val ret = new collection.mutable.ArrayBuffer[(Double,Double)]
    for (x <- 0 to (gem.getNumVertices-1)) {
      ret += ((0.0, 0.0))
    }
    for ((v, p) <- mapPositions) {
      ret(v.getLabel-1) = (p.getX, p.getY)
    }
    ret
  }
}

object FunctionGemFromDB extends FunctionBridge[Int, Gem] {
  def name = "Gem from DB"
  def parametersDesc = Seq (
    T.Int -> "ID" -> ""
  )
  def returns = T.Gem -> "Gem with given ID"

  def apply(id: Int): Gem = {
    App.getRepositorio().getGemById(id).getGem()
  }
}

