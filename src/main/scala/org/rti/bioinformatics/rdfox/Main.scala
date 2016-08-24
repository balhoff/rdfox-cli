package org.rti.bioinformatics.rdfox

import org.backuity.clist._
import java.io.File
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import uk.ac.ox.cs.JRDFox.store.DataStore
import uk.ac.ox.cs.JRDFox.store.DataStore.EqualityAxiomatizationType
import uk.ac.ox.cs.JRDFox.store.DataStore.UpdateType
import uk.ac.ox.cs.JRDFox.store.DataStore.Format
import java.util.Date
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._

object Main extends CliMain[Unit](
  name = "rdfox-cli",
  description = "a command line wrapper for RDFox") {

  var ontOpt = opt[Option[String]](name = "ontology", description = "OWL ontology to import into reasoning rules")
  var storeFileOpt = opt[Option[File]](name = "store", description = "save the current state of the store to file")
  var threadsOpt = opt[Option[Int]](name = "threads", description = "number of threads for parallel processing")
  var exportFileOpt = opt[Option[File]](name = "export", description = "export RDF triples to Turtle file")
  var reason = opt[Boolean](default = false, description = "apply reasoning after importing rules and data")
  var dataFolderOpt = opt[Option[File]](name = "data", description = "folder of RDF data files in Turtle format")

  def run: Unit = {
    import Util.time
    val dataStore = storeFileOpt.filter(_.exists).map(new DataStore(_))
      .getOrElse(new DataStore(DataStore.StoreType.ParallelComplexNN, EqualityAxiomatizationType.NoUNA))

    val ontIRIOpt = ontOpt.map { ontPath => if (ontPath.startsWith("http")) IRI.create(ontPath) else IRI.create(new File(ontPath)) }
    ontIRIOpt.foreach { ontIRI =>
      val manager = OWLManager.createOWLOntologyManager()
      val ontology = time("Loaded ontology from file") {
        manager.loadOntology(ontIRI)
      }
      time("Imported ontology into RDFox rules") {
        dataStore.importOntology(ontology, UpdateType.Add, true, true)
      }
    }
    threadsOpt.foreach(dataStore.setNumberOfThreads)
    dataFolderOpt.foreach { dataFolder =>
      val datafiles = FileUtils.listFiles(dataFolder, null, true).asScala.filterNot(_.getName == "catalog-v001.xml").filter(_.isFile).toArray
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
      time("Exported data to Turtle") {
        dataStore.export(exportFile, Format.Turtle)
      }
    }
    dataStore.dispose()
  }

}

object Util {

  def time[T](action: String)(f: => T): T = {
    val s = System.currentTimeMillis
    val res = f
    val time = (System.currentTimeMillis - s) / 1000.0
    println(s"$action in ${time}s")
    res
  }

}