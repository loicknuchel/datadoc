package com.criteo.datadoc.experiment.eventsourcing.domain.common

case class TableData(headers: Seq[String],
                     data: Seq[Seq[String]])
