package com.criteo.datadoc.experiment.eventsourcing.es

trait EventHandler[E] {
  def handle(event: E): Unit

  def handle(events: Seq[E]): Unit =
    events.foreach(handle)
}
