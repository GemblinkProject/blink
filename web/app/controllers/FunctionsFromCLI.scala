package controllers

object FunctionCLIMap {
  val functionMap = blink.cli.CommandLineInterface.getInstance.getFunctionMap
  def apply(name: String) = functionMap getFunction name
}

//object FunctionBridgeGem extends FunctionBridgeCLI[Int, Int] {
//  def function = FunctionCLIMap("")
//}

class FunctionsFromCLI {

}