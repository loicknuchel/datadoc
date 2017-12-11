package com.criteo.datadoc.es

trait EventHandler[E] {
  def init(events: Seq[E]): Unit

  def handle(event: E): Unit

  def handle(events: Seq[E]): Unit =
    events.map(handle)
}
