package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.es.common.{EventFull, EventHandler}

import scala.concurrent.Future

class SchemaEventRepository(initEvents: Seq[EventFull[SchemaEvent]]) extends EventHandler[SchemaEvent] {
  override def handle(event: EventFull[SchemaEvent]): Unit = ??? // TODO write event to db
}

object SchemaEventRepository {
  def readEvents(): Future[Seq[EventFull[SchemaEvent]]] = ??? // TODO read events from db
}
