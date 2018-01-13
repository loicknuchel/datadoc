package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, DatabaseName, Table, TableName}
import com.criteo.datadoc.experiment.eventsourcing.es.common.{CommandError, CommandHandler, EventFull, EventHandler}
import com.criteo.datadoc.experiment.eventsourcing.es.schema.SchemaCommandHandler._
import com.criteo.datadoc.experiment.eventsourcing.helpers.SeqHelpers

case class State(schema: Map[(DatabaseName, TableName), Table])

class SchemaCommandHandler(initEvents: Seq[EventFull[SchemaEvent]], protected val handlers: Seq[EventHandler[SchemaEvent]]) extends CommandHandler[State, SchemaCommand, SchemaEvent] {
  private val initState: State = State(Map())
  protected var state: State = initEvents.map(_.e).foldLeft(initState)(evolve)

  override protected def decide(s: State, c: SchemaCommand): Either[Seq[CommandError], Seq[SchemaEvent]] = c match {
    case UpdateSchema(tables) => Right(tableDiff(state.schema.values.toSeq, tables))
  }

  override protected def evolve(s: State, e: SchemaEvent): State = e match {
    case TableCreated(db, table, columns) =>
      State(schema = s.schema + ((db, table) -> Table(db, table, columns)))
    case TableDeleted(db, table) =>
      State(schema = s.schema - (db -> table))
    case ColumnCreated(db, table, column) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = column +: t.columns)
      State(schema = s.schema + ((db, table) -> u))
    case ColumnDeleted(db, table, columnName) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = t.columns.filterNot(_.name == columnName))
      State(schema = s.schema + ((db, table) -> u))
    case ColumnUpdated(db, table, column) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = column +: t.columns.filterNot(_.name == column.name))
      State(schema = s.schema + ((db, table) -> u))
  }
}

object SchemaCommandHandler {
  private def tableDiff(prev: Seq[Table], next: Seq[Table]): Seq[SchemaEvent] = {
    val (existing, created, deleted) = SeqHelpers.diff[Table](prev, next, eq)

    created.map(t => TableCreated(t.db, t.table, t.columns)) ++
      deleted.map(t => TableDeleted(t.db, t.table)) ++
      existing
        .filter { case (t1, t2) => t1 != t2 }
        .flatMap { case (t1, t2) => columnDiff(t2, t1.columns, t2.columns) }
  }

  private def columnDiff(table: Table, prev: Seq[Column], next: Seq[Column]): Seq[SchemaEvent] = {
    val (existing, created, deleted) = SeqHelpers.diff[Column](prev, next, eq)

    created.map(c => ColumnCreated(table.db, table.table, c)) ++
      deleted.map(c => ColumnDeleted(table.db, table.table, c.name)) ++
      existing
        .filter { case (c1, c2) => c1 != c2 }
        .map { case (_, c2) => ColumnUpdated(table.db, table.table, c2) }
  }

  private def eq(e1: Table, e2: Table): Boolean =
    e1.db == e2.db && e1.table == e2.table

  private def eq(e1: Column, e2: Column): Boolean =
    e1.name == e2.name
}
