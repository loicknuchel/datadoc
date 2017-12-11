package com.criteo.datadoc.es.schema

import com.criteo.datadoc.es.{CommandHandler, EventHandler}

class SchemaCommandHandler(handlers: Seq[EventHandler[SchemaEvent]]) extends CommandHandler[SchemaCommand, SchemaEvent] {
  type Error = String
  type SchemaState = Unit
  private val initialState: SchemaState = ()
  private var state: SchemaState = initialState

  SchemaEventRepository.readEvents()
    .map(events => handlers.foreach(_.init(events)))

  def handle(c: SchemaCommand): Unit = {
    decide(state, c) match {
      case Left(errors) => println(s"Errors on $c and $state: $errors")
      case Right(events) =>
        handlers.foreach(_.handle(events))
    }
  }

  private def decide(s: SchemaState, c: SchemaCommand): Either[Seq[Error], Seq[SchemaEvent]] = c match {
    case UpdateSchema(databases) => Right(Seq())
    case _ => Right(Seq())
  }

  private def evolve(s: SchemaState, e: SchemaEvent): SchemaState = e match {
    case _ => s
  }
}
