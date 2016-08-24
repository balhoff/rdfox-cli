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

## Usage

```
Usage

 rdfox-cli [options] : a command line wrapper for RDFox

Options

   --data     : Folder of RDF data files in Turtle format
   --export   : export RDF triples to Turtle file
   --ontology : OWL ontology to import into reasoning rules
   --reason   : apply reasoning after importing rules and data
   --store    : save the current state of the store to file
   --threads  : number of threads for parallel processing
```
