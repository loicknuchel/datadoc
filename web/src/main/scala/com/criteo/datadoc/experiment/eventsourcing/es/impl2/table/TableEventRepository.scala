package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import com.criteo.datadoc.experiment.eventsourcing.es.common.{EventFull, EventHandler}

import scala.concurrent.Future

class TableEventRepository(initEvents: Seq[EventFull[TableEvent]]) extends EventHandler[TableEvent] {
  override def handle(event: EventFull[TableEvent]): Unit = ??? // TODO write event to db
}

object TableEventRepository {
  def readEvents(): Future[Seq[EventFull[TableEvent]]] = ??? // TODO read events from db
}
