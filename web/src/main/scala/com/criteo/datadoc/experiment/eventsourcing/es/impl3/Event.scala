package com.criteo.datadoc.experiment.eventsourcing.es.impl3

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.{DocItem, Markdown}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, ColumnName, DatabaseName, TableName}
import com.criteo.datadoc.experiment.eventsourcing.es.common.Event

case class TableCreated(db: DatabaseName, table: TableName, columns: Seq[Column]) extends Event // when a new table is created
case class TableDeleted(db: DatabaseName, table: TableName) extends Event // when a table is deleted
case class ColumnCreated(db: DatabaseName, table: TableName, column: Column) extends Event // when add a new column
case class ColumnDeleted(db: DatabaseName, table: TableName, column: ColumnName) extends Event // when remove a column
case class ColumnUpdated(db: DatabaseName, table: TableName, column: Column) extends Event // when update a column
case class DocUpdated(target: DocItem.Target, kind: DocItem.Kind, content: Markdown, source: DocItem.Source) extends Event //
