package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, ColumnName, DatabaseName, TableName}
import com.criteo.datadoc.experiment.eventsourcing.es.common.Event

sealed trait SchemaEvent extends Event // all events related to schema aggregate
case class TableCreated(db: DatabaseName, table: TableName, columns: Seq[Column]) extends SchemaEvent // when a new table is created
case class TableDeleted(db: DatabaseName, table: TableName) extends SchemaEvent // when a table is deleted
case class ColumnCreated(db: DatabaseName, table: TableName, column: Column) extends SchemaEvent // when add a new column
case class ColumnDeleted(db: DatabaseName, table: TableName, column: ColumnName) extends SchemaEvent // when remove a column
case class ColumnUpdated(db: DatabaseName, table: TableName, column: Column) extends SchemaEvent // when update a column
