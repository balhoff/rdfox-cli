package org.rti.bioinformatics.rdfox

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.HashMap
import java.util.HashSet

import scala.collection.JavaConverters._
import scala.io.Source

import org.apache.commons.io.FileUtils
import org.backuity.clist._
import org.openrdf.rio.RDFFormat
import org.openrdf.rio.Rio
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom

import uk.ac.ox.cs.JRDFox.Prefixes
import uk.ac.ox.cs.JRDFox.store.DataStore
import uk.ac.ox.cs.JRDFox.store.DataStore.QueryDomain
import uk.ac.ox.cs.JRDFox.store.DataStore.UpdateType

object Main extends CliMain[Unit](
  name = "rdfox-cli",
  description = "a command line wrapper for RDFox") {

  var ontOpt = opt[Option[String]](name = "ontology", description = "OWL ontology to import into reasoning rules")
  var rulesOpt = opt[Option[String]](name = "rules", description = "datalog rules file to import")
  var storeFileOpt = opt[Option[File]](name = "store", description = "save the current state of the store to file")
  var threadsOpt = opt[Option[Int]](name = "threads", description = "number of threads for parallel processing")
  var exportFileOpt = opt[Option[File]](name = "export", description = "export RDF triples to Turtle file")
  var inferredOnly = opt[Boolean](default = false, name = "inferred-only", description = "export inferred triples only")
  var reason = opt[Boolean](default = false, description = "apply reasoning after importing rules and data")
  var dataFolderOpt = opt[Option[File]](name = "data", description = "folder of RDF data files in Turtle format")
  var excludedProperties = opt[Option[File]](name = "excluded-properties", description = "file containing OWL properties, one per line, to exclude from RDF exports")

  def run: Unit = {
    val dataStore = storeFileOpt.filter(_.exists).map(new DataStore(_))
      .getOrElse(new DataStore(DataStore.StoreType.ParallelComplexNN, Map("equality" -> "noUNA").asJava))
    val ontIRIOpt = ontOpt.map { ontPath => if (ontPath.startsWith("http")) IRI.create(ontPath) else IRI.create(new File(ontPath)) }
    ontIRIOpt.foreach { ontIRI =>
      val manager = OWLManager.createOWLOntologyManager()
      val ontology = time("Loaded ontology from file") {
        val startingOnt = manager.loadOntology(ontIRI)
        val tbox = startingOnt.getTBoxAxioms(true)
        val rbox = startingOnt.getRBoxAxioms(true)
        val all = new HashSet[OWLAxiom]()
        all.addAll(tbox)
        all.addAll(rbox)
        manager.createOntology(all)
      }
      time("Imported ontology into RDFox rules") {
        dataStore.importOntology(ontology, UpdateType.Add, true, false)
      }
    }

    rulesOpt.foreach { rulesFile =>
      time("Imported datalog rules") {
        dataStore.importFiles(Array(new File(rulesFile)))
      }
    }

    threadsOpt.foreach { threadCount =>
      time("Set number of threads") {
        dataStore.setNumberOfThreads(threadCount)
      }
    }

    dataFolderOpt.foreach { dataFolder =>
      val datafiles = FileUtils.listFiles(dataFolder, null, true).asScala
        .filterNot(_.getName == "catalog-v001.xml")
        .filterNot(_.isHidden())
        .filter(_.isFile).toArray
      time("Imported data files") {
        dataStore.importFiles(datafiles)
      }
    }

    if (reason) {
      time("Applied reasoning") {
        dataStore.applyReasoning()
      }
    }

    storeFileOpt.foreach { storeFile =>
      time("Saved database to file") {
        dataStore.save(storeFile)
      }
    }

    exportFileOpt.foreach { exportFile =>
      time("Exported data to turtle") {
        val prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE
        val parameters = new HashMap[String, String]()
        if (inferredOnly) parameters.put("domain", QueryDomain.IDBrepNoEDB.toString)
        val propertiesFilter = excludedProperties.map { propertiesFile =>
          val source = Source.fromFile(propertiesFile, "utf-8")
          val values = source.getLines.map(_.trim).filter(_.nonEmpty).map(p => s"<$p>").mkString(", ")
          s"FILTER(?p NOT IN ($values))"
        }.getOrElse("")
        val tuples = dataStore.compileQuery(s"""
SELECT DISTINCT ?s ?p ?o 
WHERE {
  ?s ?p ?o . 
  FILTER(!isLiteral(?s))
  FILTER(isIRI(?p)) 
  FILTER(?s != ?o)
  $propertiesFilter
}""", prefixes, parameters)
        val triplesOutput = new BufferedOutputStream(new FileOutputStream(exportFile))
        val writer = Rio.createWriter(RDFFormat.TURTLE, triplesOutput)
        writer.startRDF()
        new StatementTupleIterator(tuples).foreach(writer.handleStatement)
        writer.endRDF()
        triplesOutput.close()
      }
    }

    dataStore.dispose()
  }

  def time[T](action: String)(f: => T): T = {
    val s = System.currentTimeMillis
    val res = f
    val time = (System.currentTimeMillis - s) / 1000.0
    println(s"$action in ${time}s")
    res
  }

}
