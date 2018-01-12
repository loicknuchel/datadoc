package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class ColumnFull(db: DatabaseName,
                      table: TableName,
                      name: ColumnName,
                      typ: ColumnType)

object ColumnFull {
  def from(db: DatabaseName, table: TableName, column: Column): ColumnFull =
    ColumnFull(db, table, column.name, column.typ)

  def to(c: ColumnFull): Column =
    Column(c.name, c.typ)
}
