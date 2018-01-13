package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class Column(name: ColumnName,
                  typ: ColumnType,
                  index: Int)

object Column {

  import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem._

  val documentationFields: Seq[Kind] = Seq(Values, Technical, Description, Depends, Usage)
}
