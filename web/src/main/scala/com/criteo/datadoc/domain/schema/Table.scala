package com.criteo.datadoc.domain.schema

case class Table(name: TableName,
                 columns: Seq[Column]) {
  def flatten(db: String): Seq[ColumnFull] =
    columns.map(ColumnFull.from(db, name, _))
}

object Table {

  import com.criteo.datadoc.domain.documentation.DocItem._

  val documentationFields: Seq[Kind] = Seq(Owner, Description, Usage)
}
