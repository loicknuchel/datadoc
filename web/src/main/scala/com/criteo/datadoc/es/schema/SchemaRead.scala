package com.criteo.datadoc.es.schema

import com.criteo.datadoc.domain.schema._
import com.criteo.datadoc.es.EventHandler

class SchemaRead() extends EventHandler[SchemaEvent] {
  type Store = Map[DatabaseName, Map[TableName, Seq[Column]]]
  private var store: Store = Map()

  override def init(events: Seq[SchemaEvent]): Unit =
    store = events.foldLeft(store)(update)

  override def handle(event: SchemaEvent): Unit =
    store = update(store, event)

  def getTables(): Seq[TableFull] =
    store.flatMap { case (db, tables) => tables.map { case (table, columns) => TableFull(db, table, columns) } }.toSeq

  def getTables(db: DatabaseName): Seq[Table] =
    store.getOrElse(db, Map()).map { case (table, columns) => Table(table, columns) }.toSeq

  def getColumns(db: DatabaseName, table: TableName): Seq[Column] =
    store.getOrElse(db, Map()).getOrElse(table, Seq())

  private def update(store: Store, e: SchemaEvent): Store = e match {
    case _ => store
  }
}
