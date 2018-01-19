package com.criteo.datadoc.experiment.eventsourcing.domain.common

sealed trait DbFlag
sealed trait TableFlag
sealed trait ColumnFlag
case object Deprecated extends DbFlag with TableFlag with ColumnFlag
case object NotImplementedYep extends DbFlag with TableFlag with ColumnFlag
case object AvailableInVertica extends TableFlag
case object Transparency extends ColumnFlag // exposed to Criteo clients
case class Custom(value: String) extends DbFlag with TableFlag with ColumnFlag
