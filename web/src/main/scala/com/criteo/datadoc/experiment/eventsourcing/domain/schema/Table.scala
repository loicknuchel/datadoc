package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class Table(db: DatabaseName,
                 table: TableName,
                 columns: Seq[Column])

object Table {

  import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem._

  val documentationFields: Seq[Kind] = Seq(Description, Owner, Depends, Usage)
}
