package com.criteo.datadoc.es.schema

import com.criteo.datadoc.domain.schema._
import com.criteo.datadoc.es.EventHandler

class SchemaRead(initEvents: Seq[SchemaEvent]) extends EventHandler[SchemaEvent] {
  type State = Map[DatabaseName, Map[TableName, Seq[Column]]]
  private val initState: State = Map()
  private var state: State = initEvents.foldLeft(initState)(update)

  override def handle(event: SchemaEvent): Unit =
    state = update(state, event)

  def getTables(): Seq[TableFull] =
    state.flatMap { case (db, tables) => tables.map { case (table, columns) => TableFull(db, table, columns) } }.toSeq

  def getTables(db: DatabaseName): Seq[Table] =
    state.getOrElse(db, Map()).map { case (table, columns) => Table(table, columns) }.toSeq

  def getColumns(db: DatabaseName, table: TableName): Seq[Column] =
    state.getOrElse(db, Map()).getOrElse(table, Seq())

  private def update(state: State, e: SchemaEvent): State = e match {
    case _ => state
  }
}
