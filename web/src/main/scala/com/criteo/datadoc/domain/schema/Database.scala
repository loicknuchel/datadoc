package com.criteo.datadoc.domain.schema

case class Database(name: DatabaseName,
                    tables: Seq[Table]) {
  def flatten: Seq[TableFull] =
    tables.map(TableFull.from(name, _))
}

object Database {

  import com.criteo.datadoc.domain.documentation.DocItem._

  val documentationFields: Seq[Kind] = Seq(Owner, Description)

  def from(columns: Seq[ColumnFull]): Seq[Database] =
    columns.groupBy(_.db).map { case (db, dbColumns) =>
      val tables = dbColumns.groupBy(_.table).map { case (table, tableColumns) =>
        Table(table, tableColumns.map(ColumnFull.to))
      }.toSeq
      Database(db, tables)
    }.toSeq
}
