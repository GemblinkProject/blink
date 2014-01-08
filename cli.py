import subprocess
from py4j.java_gateway import (JavaGateway, GatewayClient, Py4JNetworkError)
import random
import time
import keyword
import sys
import readline
import rlcompleter
import os
import code

# Add path with python modules for blink
path = os.path.join(os.environ['PWD'], 'pysrc/')
if path not in sys.path:
    pass # sys.path.append(path)

# Open a JVM with the modules loaded and open gateway
java_cmd = open('run').readlines()[0].split(' ')
java_cmd[-2] += ':lib/py4j0.8.jar' # add library py4j
java_cmd[-1] = 'blink/cli/PythonBridge'

retries = 0
rerun_java_after = 5
while retries < 20:
    if retries%rerun_java_after == 0:
        java_gateway_port = random.randint(16000, 32000)
        jvm = subprocess.Popen(java_cmd + [str(java_gateway_port)],
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE,
                               )
    try:
        java_gateway = JavaGateway(GatewayClient(port=java_gateway_port))
        java_name = java_gateway.jvm.System.getProperty("java.runtime.name")
        java_cli = java_gateway.entry_point
    except Py4JNetworkError:
        print('Waiting JVM...')
        time.sleep(1)
    except Exception as e:
        print('Unexpected exception: ' + str(e))
        jvm.kill()
        sys.exit(0)
    else:
        print('Connected through ' + java_name);
        break
    retries += 1

# Functions to flush stderr and stdout from the JVM (can be usefull to debug)
def flush_stderr():
    lines = ''
    for line in jvm.stderr:
        lines += line
    return lines

def flush_stdout():
    lines = ''
    for line in jvm.stdout:
        lines += line
    return lines

# Map java CLI function to be used here
def java_cli_function(obj, java_gateway=java_gateway):
    '''Return a python function to use a java cli function
    '''
    def function(*args, **kwargs):
        params = java_gateway.jvm.java.util.ArrayList()
        for arg in args:
            params.add(arg)
        datamap = java_gateway.jvm.blink.cli.DataMap()
        for kw, arg in kwargs.iteritems():
            datamap.addData(kw, arg)
        return obj.evaluate(params, datamap)
    return function

# Append the suffix "_" to java cli functions to builtin functions or python keywords
reserved_words = dir(__builtins__) + keyword.kwlist
functions = {
    'flush_stderr': flush_stderr,
    'flush_stdout': flush_stdout,
}
try:
    java_function_map = java_cli.getFunctionMap()
    function_names = java_function_map.getKeySet()
    for function_name in function_names:
        func = java_cli_function(java_function_map.getFunction(function_name))
        if function_name in reserved_words:
            function_name += '_'
        functions[function_name] = func    
except Exception as e:
    print('Exception when get functions from Java CLI: ' + str(e))

# Starts Python CLI
readline.set_completer(rlcompleter.Completer(functions).complete)
readline.parse_and_bind("tab: complete")
try:
    histfile = os.path.join(os.environ['HOME'], '.gemblinkhistory')
    readline.read_history_file(histfile)
except IOError:
    pass

code.interact(banner="Python CLI for GemBlink", local=functions)

readline.write_history_file(histfile)
jvm.kill()
