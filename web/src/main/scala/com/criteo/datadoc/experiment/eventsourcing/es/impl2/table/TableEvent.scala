package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.{DocItem, Markdown}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, ColumnName}
import com.criteo.datadoc.experiment.eventsourcing.es.common.Event

sealed trait TableEvent extends Event //
case class TableCreated(columns: Seq[Column]) extends TableEvent // when a new table is created
case class TableDeleted() extends TableEvent // when a table is deleted
case class ColumnCreated(column: Column) extends TableEvent // when add a new column
case class ColumnDeleted(column: ColumnName) extends TableEvent // when remove a column
case class ColumnUpdated(column: Column) extends TableEvent // when update a column
case class DocUpdated(target: DocItem.Target, kind: DocItem.Kind, content: Markdown, source: DocItem.Source) extends TableEvent //
