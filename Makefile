ROOT=$(shell pwd)
LIBS=colt.jar commons-collections-3.1.jar commons-collections-testframework-3.1.jar concurrent.jar \
derby.jar jgraph.jar jscience.jar jung-1.7.4.jar junit-4.0.jar sqlite-jdbc-3.7.2.jar \
vecmath.jar j3dcore.jar itextpdf-5.4.2.jar py4j0.8.jar

comma:= :
empty:=
space:= $(empty) $(empty)

LIBS2=$(addprefix $(ROOT)/lib/, $(LIBS))
LIBS3=$(subst $(space),$(comma),$(LIBS2))
# LIBS2=$(patsubst %, $(ROOT)/lib/%, $(LIBS))
JFILES:=$(shell find src | egrep "[.]java$$")
SFILES:=$(shell find src | egrep "[.]scala$$")

scalav:=scala-2.10.3
playv:=2.2.1
scalac:=aux/$(scalav)/bin/scalac
scalae:=aux/$(scalav)/bin/scala
play:=aux/play-$(playv)/play
p?=9000
m?=12
b?=1

all:
	@mkdir -p bin
	@# find ./src | egrep "[.]java$$" > file.list
	@javac -classpath $(LIBS3):src -d bin $(JFILES)

scala: $(scalac)
	@mkdir -p bin
	@# find ./src | egrep "[.]java$$|[.]scala$$" > file.list
	@$(scalac) -classpath $(LIBS3):src -d bin $(JFILES) $(SFILES)

$(scalac): 
	mkdir -p aux
	cd aux; wget "http://www.scala-lang.org/files/archive/$(scalav).tgz" -O "$(scalav).tgz"
	cd aux; tar -zxvf "$(scalav).tgz"

browser: $(play)
	$(if $(b), @(sleep 5; x-www-browser "http://localhost:$(p)")&)
	@cd web; export _JAVA_OPTIONS="-Xmx$(m)000m"; ../$(play) "start $(p)"

browserdev: $(play)
	@cd web; export _JAVA_OPTIONS="-Xmx$(m)000m"; ../$(play) "run $(p)"

$(play):
	mkdir -p aux
	cd aux; wget "http://downloads.typesafe.com/play/$(playv)/play-$(playv).zip" -O "play-$(playv).zip"
	cd aux; unzip "play-$(playv).zip"

#echo $(LIBS3)
# javac -classpath $(LIBS3):src/. src/blink/*.java 
# find src/. | grep java$ | grep -v "#" | xargs -I {} -t javac -classpath $(LIBS3):$(ROOT)/src {}

run:
	java -Xmx$(m)000m -classpath $(LIBS3):bin blink/cli/CommandLineInterface

runs:
	$(scalae) -Xmx$(m)000m -classpath $(LIBS3):bin blink/cli/CommandLineInterface


