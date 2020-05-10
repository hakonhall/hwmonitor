all: compile run

compile:
	modulec -n no.ion.hwmonitor -v 0.1.0 -d build/classes -e no.ion.hwmonitor.Main -f build/ src -- --release 11

run:
	javahms --module-path build --module no.ion.hwmonitor

re: clean all

clean:
	rm -rf build
