package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem.{ColumnTarget, DatabaseTarget, TableTarget}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Column
import com.criteo.datadoc.experiment.eventsourcing.es.common.{CommandError, CommandHandler, EventFull, EventHandler}
import com.criteo.datadoc.experiment.eventsourcing.helpers.SeqHelpers

case class State(schema: Option[Seq[Column]])

/**
  * This CommandHandler handle all commands related to one table :
  *   - schema commands
  *   - doc commands
  *
  * In this implementation, there is an aggregate per hive table
  */
class TableCommandHandler(protected val initEvents: Seq[EventFull[TableEvent]],
                          protected val handlers: Seq[EventHandler[TableEvent]]
                         ) extends CommandHandler[State, TableCommand, TableEvent] {

  import TableCommandHandler._

  private val initState: State = State(None)
  override protected var state: State = initEvents.map(_.e).foldLeft(initState)(evolve)

  override protected def decide(s: State, c: TableCommand): Either[Seq[CommandError], Seq[TableEvent]] = c match {
    case UpdateSchema(columns) => Right(tableDiff(state.schema, columns))
    case UpdateDoc(target, kind, content, source) => target match {
      case _: DatabaseTarget => Left(Seq(s"Unable to document a database !"))
      case TableTarget(_, _) => Right(Seq(DocUpdated(target, kind, content, source)))
      case ColumnTarget(_, _, columnName) =>
        if (s.schema.exists(_.exists(_.name == columnName))) Right(Seq(DocUpdated(target, kind, content, source)))
        else Left(Seq(s"Unable to document column $columnName, it does not exists !"))
    }
  }

  override protected def evolve(s: State, e: TableEvent): State = e match {
    case TableCreated(columns) =>
      s.copy(schema = Some(columns))
    case TableDeleted() =>
      s.copy(schema = None)
    case ColumnCreated(column) =>
      s.copy(schema = Some(column +: s.schema.get))
    case ColumnDeleted(columnName) =>
      s.copy(schema = Some(s.schema.get.filterNot(_.name == columnName)))
    case ColumnUpdated(column) =>
      s.copy(schema = Some(column +: s.schema.get.filterNot(_.name == column.name)))
    case _: DocUpdated => s
  }
}

object TableCommandHandler {
  private def tableDiff(prev: Option[Seq[Column]], next: Option[Seq[Column]]): Seq[TableEvent] = {
    (prev, next) match {
      case (None, None) => Seq()
      case (None, Some(t2)) => Seq(TableCreated(t2))
      case (Some(_), None) => Seq(TableDeleted())
      case (Some(t1), Some(t2)) if t1 != t2 => columnDiff(t1, t2)
      case _ => Seq()
    }
  }

  private def columnDiff(prev: Seq[Column], next: Seq[Column]): Seq[TableEvent] = {
    val (existing, created, deleted) = SeqHelpers.diff[Column](prev, next, eq)

    created.map(c => ColumnCreated(c)) ++
      deleted.map(c => ColumnDeleted(c.name)) ++
      existing
        .filter { case (c1, c2) => c1 != c2 }
        .map { case (_, c2) => ColumnUpdated(c2) }
  }

  private def eq(e1: Column, e2: Column): Boolean =
    e1.name == e2.name
}
