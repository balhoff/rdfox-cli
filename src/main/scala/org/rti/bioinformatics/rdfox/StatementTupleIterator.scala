package org.rti.bioinformatics.rdfox

import org.openrdf.model.Resource
import org.openrdf.model.Statement
import org.openrdf.model.URI
import org.openrdf.model.Value
import org.openrdf.model.ValueFactory
import org.openrdf.model.impl.ValueFactoryImpl

import uk.ac.ox.cs.JRDFox.model.Datatype._
import uk.ac.ox.cs.JRDFox.store.{ Resource => RDFoxResource }
import uk.ac.ox.cs.JRDFox.store.TupleIterator

class StatementTupleIterator(tuples: TupleIterator) extends Iterator[Statement] {

  require(tuples.getArity == 3, "Tuples must be triples")

  private var multiplicity = tuples.open

  private val factory: ValueFactory = ValueFactoryImpl.getInstance

  override def hasNext: Boolean = multiplicity != 0

  override def next(): Statement = {
    val s = tuples.getResource(0)
    val p = tuples.getResource(1)
    val o = tuples.getResource(2)
    val statement = factory.createStatement(
      resourceToValue(s).asInstanceOf[Resource],
      resourceToValue(p).asInstanceOf[URI],
      resourceToValue(o))
    multiplicity = tuples.advance()
    statement
  }

  private def resourceToValue(resource: RDFoxResource): Value = resource.m_datatype match {
    case BLANK_NODE        => factory.createBNode(resource.m_lexicalForm)
    case IRI_REFERENCE     => factory.createURI(resource.m_lexicalForm)
    case RDF_PLAIN_LITERAL => factory.createLiteral(resource.m_lexicalForm)
    case other             => factory.createLiteral(resource.m_lexicalForm, factory.createURI(other.getIRI))
  }

}