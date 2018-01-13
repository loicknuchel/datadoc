package com.criteo.datadoc.experiment.eventsourcing.es.documentation

import com.criteo.datadoc.experiment.eventsourcing.es.common.{EventFull, EventHandler}

import scala.concurrent.Future

class DocEventRepository(initEvents: Seq[EventFull[DocEvent]]) extends EventHandler[DocEvent] {
  override def handle(event: EventFull[DocEvent]): Unit = ??? // TODO write event to db
}

object DocEventRepository {
  def readEvents(): Future[Seq[EventFull[DocEvent]]] = ??? // TODO read events from db
}
