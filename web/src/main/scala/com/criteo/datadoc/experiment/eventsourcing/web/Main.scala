package com.criteo.datadoc.experiment.eventsourcing.web

import com.criteo.datadoc.experiment.eventsourcing.es.GlobalEventHandler
import com.criteo.datadoc.experiment.eventsourcing.es.impl1.documentation.{DocCommandHandler, DocEventRepository}
import com.criteo.datadoc.experiment.eventsourcing.es.impl1.schema.{SchemaCommandHandler, SchemaEventRepository, SchemaProjection}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Main {
  def main(args: Array[String]): Unit = {
    val globalHandler = new GlobalEventHandler

    val schemaEvents = Await.result(SchemaEventRepository.readEvents(), Duration.Inf)
    val schemaProjection = new SchemaProjection(schemaEvents)
    val schemaWrite = new SchemaEventRepository(schemaEvents)
    val schemaCommandHandler = new SchemaCommandHandler(schemaEvents, Seq(schemaWrite, schemaProjection/*, globalHandler*/)) // TODO: covariance ?

    val docEvents = Await.result(DocEventRepository.readEvents(), Duration.Inf)
    val docWrite = new DocEventRepository(docEvents)
    val docCommandHandler = new DocCommandHandler(docEvents, Seq(docWrite))
  }
}
