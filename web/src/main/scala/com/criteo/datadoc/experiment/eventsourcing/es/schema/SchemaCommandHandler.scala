package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.es.{CommandHandler, EventHandler}

class SchemaCommandHandler(initEvents: Seq[SchemaEvent], handlers: Seq[EventHandler[SchemaEvent]]) extends CommandHandler[SchemaCommand, SchemaEvent] {
  type Error = String
  type State = Unit
  private val initState: State = ()
  private var state: State = initEvents.foldLeft(initState)(evolve)

  def handle(c: SchemaCommand): Unit = {
    decide(state, c) match {
      case Left(errors) => println(s"Errors on $c and $state: $errors")
      case Right(events) =>
        state = events.foldLeft(state)(evolve)
        handlers.foreach(_.handle(events))
    }
  }

  private def decide(s: State, c: SchemaCommand): Either[Seq[Error], Seq[SchemaEvent]] = c match {
    case UpdateSchema(databases) => Right(Seq()) // TODO: generate diff events
    case _ => Right(Seq())
  }

  private def evolve(s: State, e: SchemaEvent): State = e match {
    // TODO: apply event to state
    case _ => s
  }
}
