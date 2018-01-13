package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.domain.schema._
import com.criteo.datadoc.experiment.eventsourcing.es.common.{EventFull, EventHandler}

class SchemaProjection(initEvents: Seq[EventFull[SchemaEvent]]) extends EventHandler[SchemaEvent] {
  type State = Map[DatabaseName, Map[TableName, Seq[Column]]]
  private val initState: State = Map()
  private var state: State = initEvents.foldLeft(initState)(update)

  override def handle(event: EventFull[SchemaEvent]): Unit =
    state = update(state, event)

  def getTables(): Seq[Table] =
    state.flatMap { case (db, tables) => tables.map { case (table, columns) => Table(db, table, columns) } }.toSeq

  def getTables(db: DatabaseName): Seq[Table] =
    state.getOrElse(db, Map()).map { case (table, columns) => Table(db, table, columns) }.toSeq

  def getTable(db: DatabaseName, table: TableName): Option[Table] =
    state.getOrElse(db, Map()).get(table).map(columns => Table(db, table, columns))

  private def update(s: State, e: EventFull[SchemaEvent]): State = e match {
    case EventFull(TableCreated(db, table, columns), _, _) =>
      s + (db -> (s.getOrElse(db, Map()) + (table -> columns)))
    case EventFull(TableDeleted(db, table), _, _) =>
      s + (db -> (s.getOrElse(db, Map()) - table))
    case EventFull(ColumnCreated(db, table, column), _, _) =>
      s + (db -> (s.getOrElse(db, Map()) + (table -> (column +: s(db)(table)))))
    case EventFull(ColumnDeleted(db, table, columnName), _, _) =>
      s + (db -> (s.getOrElse(db, Map()) + (table -> s(db)(table).filterNot(_.name == columnName))))
    case EventFull(ColumnUpdated(db, table, column), _, _) =>
      s + (db -> (s.getOrElse(db, Map()) + (table -> (column +: s(db)(table).filterNot(_.name == column.name)))))
  }
}
