package controllers

import play.api._
import play.api.mvc._
import blink.cli._
import blink._
import java.util.ArrayList

object Application extends Controller {
    var w = 1
  def index = Action {
  CommandLineInterface.getInstance()._currentCommand = "qwe"
    val f = new FunctionDraw
    val g = new Gem(1,1,1,1)
    val a = new ArrayList[Gem]
    a.add(g)
    //f.hardwork(a, new DataMap)
    w += 1
    Ok(views.html.index())
  }

}
