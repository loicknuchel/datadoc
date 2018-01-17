package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem.{ColumnTarget, DatabaseTarget, TableTarget}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.ColumnName
import com.criteo.datadoc.experiment.eventsourcing.es.common.{EventFull, EventHandler}

class DocProjection(initEvents: Seq[EventFull[TableEvent]]) extends EventHandler[TableEvent] {

  import DocProjection._

  case class State(table: Map[DocItem.Kind, DocItem],
                   columns: Map[ColumnName, Map[DocItem.Kind, DocItem]])

  private val initState = State(Map(), Map())
  private var state = initEvents.foldLeft(initState)(update)

  override def handle(event: EventFull[TableEvent]): Unit =
    state = update(state, event)

  def getDoc(): (Map[DocItem.Kind, DocItem], Map[ColumnName, Map[DocItem.Kind, DocItem]]) =
    (state.table, state.columns)

  def getTableDoc(): Map[DocItem.Kind, DocItem] =
    state.table

  def getColumnDoc(name: ColumnName): Map[DocItem.Kind, DocItem] =
    state.columns.getOrElse(name, Map())

  private def update(s: State, e: EventFull[TableEvent]): State = e match {
    case EventFull(u@DocUpdated(target, kind, _, _), _, created) => target match {
      case _: DatabaseTarget =>
        s
      case TableTarget(_, _) =>
        s.copy(table = s.table + (kind -> build(u, created)))
      case ColumnTarget(_, _, columnName) =>
        val columnDoc = s.columns.getOrElse(columnName, Map()) + (kind -> build(u, created))
        s.copy(columns = s.columns + (columnName -> columnDoc))
    }
    case _ => s
  }
}

object DocProjection {
  private def build(e: DocUpdated, created: Date): DocItem =
    DocItem(
      target = e.target,
      kind = e.kind,
      content = e.content,
      created = created,
      source = e.source)
}
