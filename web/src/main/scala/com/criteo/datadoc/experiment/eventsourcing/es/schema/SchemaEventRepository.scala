package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.es.EventHandler

import scala.concurrent.Future

class SchemaEventRepository(initEvents: Seq[SchemaEvent]) extends EventHandler[SchemaEvent] {
  override def handle(event: SchemaEvent): Unit = ???
}

object SchemaEventRepository {
  def readEvents(): Future[Seq[SchemaEvent]] = ???
}
