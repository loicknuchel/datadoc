package com.criteo.datadoc.experiment.eventsourcing.services

import com.criteo.datadoc.experiment.eventsourcing.domain.common.AggregateId
import com.criteo.datadoc.experiment.eventsourcing.es.common.{Event, EventFull}

import scala.collection.mutable

trait EventStore {
  def getEvents(aggregateId: AggregateId): Seq[EventFull[Event]]

  def saveEvents(aggregateId: AggregateId, events: Seq[EventFull[Event]], expectedVersion: Int): Unit
}

class InMemoryEventStore extends EventStore {
  private val eventStreams: mutable.Map[AggregateId, (Int, Seq[EventFull[Event]])] = mutable.Map()

  override def getEvents(aggregateId: AggregateId): Seq[EventFull[Event]] =
    getAggregate(aggregateId)._2

  override def saveEvents(aggregateId: AggregateId, events: Seq[EventFull[Event]], expectedVersion: Int): Unit = {
    val (version, previousEvents) = getAggregate(aggregateId)
    if (!isVersionValid(version, events)) {
      throw new IllegalArgumentException("Wrong version: concurrency exception")
    }
    val newVersion = events.lastOption.map(_.i).getOrElse(version)
    eventStreams + (aggregateId -> (newVersion, previousEvents ++ events))
  }

  private def getAggregate(aggregateId: AggregateId): (Int, Seq[EventFull[Event]]) =
    eventStreams.getOrElse(aggregateId, (0, Seq()))

  private def isVersionValid(current: Int, events: Seq[EventFull[Event]]): Boolean = {
    events.zipWithIndex.forall { case (EventFull(_, version, _), i) =>
      version == current + i + 1
    }
  }
}
