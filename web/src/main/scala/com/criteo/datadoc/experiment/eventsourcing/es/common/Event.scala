package com.criteo.datadoc.experiment.eventsourcing.es.common

import java.util.Date

trait Event

case class EventFull[E <: Event](e: E, i: Long, created: Date)
