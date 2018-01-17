package com.criteo.datadoc.experiment.eventsourcing.es.common

import java.util.Date

trait Command

case class CommandFull[C <: Command](c: C, created: Date)
