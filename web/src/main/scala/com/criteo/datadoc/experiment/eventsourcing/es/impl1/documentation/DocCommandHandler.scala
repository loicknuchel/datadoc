package com.criteo.datadoc.experiment.eventsourcing.es.impl1.documentation

import com.criteo.datadoc.experiment.eventsourcing.es.common.{CommandError, CommandHandler, EventFull, EventHandler}

/**
  * This CommandHandler handle commands for all documentation.
  */
class DocCommandHandler(protected val initEvents: Seq[EventFull[DocEvent]],
                        protected val handlers: Seq[EventHandler[DocEvent]]
                       ) extends CommandHandler[Unit, DocCommand, DocEvent] {
  type State = Unit
  private val initState: State = ()
  override protected var state: State = initEvents.map(_.e).foldLeft(initState)(evolve)

  override protected def decide(s: State, c: DocCommand): Either[Seq[CommandError], Seq[DocEvent]] = c match {
    case _ => Right(Seq()) // TODO
  }

  override protected def evolve(s: State, e: DocEvent): State = e match {
    case _ => s // TODO
  }
}
