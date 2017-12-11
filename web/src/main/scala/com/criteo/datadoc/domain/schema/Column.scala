package com.criteo.datadoc.domain.schema

case class Column(name: ColumnName,
                  typ: ColumnType)

object Column {

  import com.criteo.datadoc.domain.documentation.DocItem._

  val documentationFields: Seq[Kind] = Seq(Owner, Description, Usage, Values)
}
