package com.criteo.datadoc.experiment.eventsourcing.es.impl3

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem.{ColumnTarget, DatabaseTarget, TableTarget}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema._
import com.criteo.datadoc.experiment.eventsourcing.es.common._
import com.criteo.datadoc.experiment.eventsourcing.helpers.SeqHelpers

case class State(currentVersion: Long,
                 schema: Map[(DatabaseName, TableName), Table],
                 dbDoc: Map[DatabaseName, Map[DocItem.Kind, DocItem]],
                 tableDoc: Map[(DatabaseName, TableName), Map[DocItem.Kind, DocItem]],
                 columnDoc: Map[(DatabaseName, TableName, ColumnName), Map[DocItem.Kind, DocItem]])

/**
  * This implementation does not use CQRS, it only has one event stream and we read data from the internal state
  */
class GlobalCommandHandler(protected val initEvents: Seq[EventFull[Event]],
                           protected val handlers: Seq[EventHandler[Event]]) {

  import GlobalCommandHandler._

  private val initState: State = State(0, Map(), Map(), Map(), Map())
  private var state: State = initEvents.foldLeft(initState)(evolve)

  def handle(c: CommandFull[Command]): Either[Seq[CommandError], Seq[EventFull[Event]]] = {
    val res = decide(state, c.c).map(buildFullEvent(_, state.currentVersion + 1, c.created))
    res match {
      case Left(_) => // println(s"Errors on $c and $state: $errors")
      case Right(events) =>
        state = evolve(state, events).copy(currentVersion = events.lastOption.map(_.i).getOrElse(state.currentVersion))
        handlers.foreach(_.handle(events))
    }
    res
  }

  private def decide(s: State, c: Command): Either[Seq[CommandError], Seq[Event]] = c match {
    case UpdateSchema(tables) => Right(tableDiff(state.schema.values.toSeq, tables))
    case UpdateDoc(target, kind, content, source) => target match {
      case _: DatabaseTarget => Left(Seq(s"Unable to document a database !"))
      case TableTarget(_, _) => Right(Seq(DocUpdated(target, kind, content, source)))
      case ColumnTarget(db, table, columnName) =>
        if (s.schema.get(db -> table).map(_.columns).getOrElse(Seq()).exists(_.name == columnName)) Right(Seq(DocUpdated(target, kind, content, source)))
        else Left(Seq(s"Unable to document the $columnName column in $db.$table, it does not exists !"))
    }
  }

  private def evolve(s: State, e: EventFull[Event]): State = e match {
    case EventFull(TableCreated(db, table, columns), _, _) =>
      s.copy(schema = s.schema + ((db, table) -> Table(db, table, columns)))
    case EventFull(TableDeleted(db, table), _, _) =>
      s.copy(schema = s.schema - (db -> table))
    case EventFull(ColumnCreated(db, table, column), _, _) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = column +: t.columns)
      s.copy(schema = s.schema + ((db, table) -> u))
    case EventFull(ColumnDeleted(db, table, columnName), _, _) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = t.columns.filterNot(_.name == columnName))
      s.copy(schema = s.schema + ((db, table) -> u))
    case EventFull(ColumnUpdated(db, table, column), _, _) =>
      val t = s.schema(db -> table)
      val u = t.copy(columns = column +: t.columns.filterNot(_.name == column.name))
      s.copy(schema = s.schema + ((db, table) -> u))
    case EventFull(u@DocUpdated(target, kind, _, _), _, _) => target match {
      case DatabaseTarget(db) => s.copy(dbDoc = update(s.dbDoc, db, kind, build(u, e.created)))
      case TableTarget(db, table) => s.copy(tableDoc = update(s.tableDoc, (db, table), kind, build(u, e.created)))
      case ColumnTarget(db, table, column) => s.copy(columnDoc = update(s.columnDoc, (db, table, column), kind, build(u, e.created)))
    }
  }

  private def evolve(s: State, events: Seq[EventFull[Event]]): State =
    events.foldLeft(s)(evolve)

  private def buildFullEvent(events: Seq[Event], start: Long, created: Date): Seq[EventFull[Event]] =
    events.zipWithIndex.map { case (e, i) => EventFull[Event](e, start + i, created) }

  def getTables(): Seq[Table] =
    state.schema.values.toSeq

  def getTables(db: DatabaseName): Seq[Table] =
    state.schema.filter { case ((dbName, _), _) => db == dbName }.values.toSeq

  def getTable(db: DatabaseName, table: TableName): Option[Table] =
    state.schema.get(db -> table)

  def getDoc(db: DatabaseName): Map[DocItem.Kind, DocItem] =
    state.dbDoc.getOrElse(db, Map())

  def getTableDoc(db: DatabaseName, table: TableName): Map[DocItem.Kind, DocItem] =
    state.tableDoc.getOrElse(db -> table, Map())

  def getColumnDoc(db: DatabaseName, table: TableName, column: ColumnName): Map[DocItem.Kind, DocItem] =
    state.columnDoc.getOrElse((db, table, column), Map())
}

object GlobalCommandHandler {
  private def tableDiff(prev: Seq[Table], next: Seq[Table]): Seq[Event] = {
    val (existing, created, deleted) = SeqHelpers.diff[Table](prev, next, eq)

    created.map(t => TableCreated(t.db, t.table, t.columns)) ++
      deleted.map(t => TableDeleted(t.db, t.table)) ++
      existing
        .filter { case (t1, t2) => t1 != t2 }
        .flatMap { case (t1, t2) => columnDiff(t2, t1.columns, t2.columns) }
  }

  private def columnDiff(table: Table, prev: Seq[Column], next: Seq[Column]): Seq[Event] = {
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

  private def update[K](initial: Map[K, Map[DocItem.Kind, DocItem]], key: K, kind: DocItem.Kind, doc: DocItem): Map[K, Map[DocItem.Kind, DocItem]] =
    initial + (key -> (initial.getOrElse(key, Map()) + (kind -> doc)))

  private def build(u: DocUpdated, created: Date): DocItem =
    DocItem(u.target, u.kind, u.content, created, u.source)
}
