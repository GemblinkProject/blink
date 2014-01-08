package blink.cli;
import py4j.GatewayServer;

public class PythonBridge {
  public static void main(String[] args) {
    CommandLineInterface cli = CommandLineInterface.getInstance();
    cli._currentCommand = new String("Python Bridge");
    GatewayServer server = new GatewayServer(cli, Integer.parseInt(args[0]));
    server.start();
  }
}
