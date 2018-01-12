package com.criteo.datadoc.experiment.eventsourcing.es

trait CommandHandler[C, E] {
  def handle(c: C): Either[Seq[String], Seq[E]]
}
