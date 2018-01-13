package com.criteo.datadoc.experiment.eventsourcing.es.common

trait EventHandler[E <: Event] {
  def handle(event: EventFull[E]): Unit

  def handle(events: Seq[EventFull[E]]): Unit =
    events.foreach(handle)
}
