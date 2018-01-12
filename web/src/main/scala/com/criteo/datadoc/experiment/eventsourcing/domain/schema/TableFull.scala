package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class TableFull(db: DatabaseName,
                     table: TableName,
                     columns: Seq[Column]) {
  def flatten: Seq[ColumnFull] =
    columns.map(ColumnFull.from(db, table, _))
}

object TableFull {
  def from(db: DatabaseName, t: Table): TableFull =
    TableFull(db, t.name, t.columns)

  def to(t: TableFull): Table =
    Table(t.table, t.columns)
}
