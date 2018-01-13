package com.criteo.datadoc.experiment.eventsourcing.es

import com.criteo.datadoc.experiment.eventsourcing.es.common.{Event, EventFull, EventHandler}

class GlobalEventHandler extends EventHandler[Event] {
  override def handle(event: EventFull[Event]): Unit = ???
}
