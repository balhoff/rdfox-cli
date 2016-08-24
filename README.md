# rdfox-cli

Simple command line wrapper for common RDFox operations.

## Building

Install `sbt` (Scala Build Tool) on your system. For Mac OS X, it is easily done using [Homebrew](http://brew.sh): `brew install sbt`. `sbt` requires a working Java installation, but you do not need to otherwise install Scala.

Download the RDFox libs for your platform from http://www.cs.ox.ac.uk/isg/tools/RDFox/otherOS_download.html. Place `JRDFox.jar` and the associated native libraries (e.g. `libCppRDFox.*`, `libCppRDFox-logAPI.*`) into `lib`.

`sbt compile`

## Running

To build the command-line executable, run:

`sbt stage`

You will find executables for Unix and Windows in `target/universal/stage/bin/`. These depend on the libraries in `target/universal/stage/lib`.
