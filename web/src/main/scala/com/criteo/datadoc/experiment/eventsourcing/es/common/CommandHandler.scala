package com.criteo.datadoc.experiment.eventsourcing.es.common

import java.util.Date

trait CommandHandler[State, C <: Command, E <: Event] {
  protected val handlers: Seq[EventHandler[E]]
  protected var state: State
  private var index: Long = 0 // TODO: how to initialize ?

  def handle(c: C): Either[Seq[CommandError], Seq[EventFull[E]]] = {
    val res = decide(state, c).map(buildFullEvent(_, index + 1))
    res match {
      case Left(_) => // println(s"Errors on $c and $state: $errors")
      case Right(events) =>
        state = evolve(state, events.map(_.e))
        index = events.lastOption.map(_.i).getOrElse(index)
        handlers.foreach(_.handle(events))
    }
    res
  }

  protected def decide(s: State, c: C): Either[Seq[CommandError], Seq[E]]

  protected def evolve(s: State, e: E): State

  private def evolve(s: State, events: Seq[E]): State =
    events.foldLeft(s)(evolve)

  private def buildFullEvent(events: Seq[E], start: Long): Seq[EventFull[E]] =
    events.zipWithIndex.map { case (e, i) => EventFull[E](e, start + i, new Date()) }
}
