package com.criteo.datadoc.experiment.eventsourcing.domain.documentation

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.UserId
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{ColumnName, DatabaseName, TableName}

case class DocItem(target: DocItem.Target,
                   kind: DocItem.Kind,
                   content: Markdown,
                   created: Date,
                   source: DocItem.Source)

object DocItem {

  sealed trait Target // what is documented
  case class DatabaseTarget(db: DatabaseName) extends Target // a database
  case class TableTarget(db: DatabaseName, table: TableName) extends Target // a table
  case class ColumnTarget(db: DatabaseName, table: TableName, column: ColumnName) extends Target // a column

  sealed trait Kind // the kind of documentations
  case object Description extends Kind // functional description of the target
  case object Owner extends Kind // who is responsible of the target
  case object Depends extends Kind // parent dependency of the target
  case object Usage extends Kind // exemple on how to use the target
  case object Values extends Kind // possibles values of the target
  case object Technical extends Kind // technical documentation of the target

  sealed trait Source // where the documentation comes from
  case class UserSource(id: UserId) extends Source // filled by user in UI
  case class MetastoreSource() extends Source // taken from metastore comments
  case class DataflowSource(project: String, job: String) extends Source // taken from dataflow job

}
