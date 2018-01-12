package com.criteo.datadoc.experiment.eventsourcing.web

import com.criteo.datadoc.experiment.eventsourcing.es.schema.{SchemaCommandHandler, SchemaEvent, SchemaEventRepository, SchemaRead}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Main {
  def main(args: Array[String]): Unit = {
    val events: Seq[SchemaEvent] = Await.result(SchemaEventRepository.readEvents(), Duration.Inf)
    val schemaRead = new SchemaRead(events)
    val schemaWrite = new SchemaEventRepository(events)
    val schemaCommandHandler = new SchemaCommandHandler(events, Seq(schemaWrite, schemaRead))

  }
}
