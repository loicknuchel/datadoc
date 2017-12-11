package com.criteo.datadoc.domain.documentation

import java.util.Date

import com.criteo.datadoc.domain.UserId
import com.criteo.datadoc.domain.schema.{ColumnName, DatabaseName, TableName}

case class DocId(value: String)

case class DocItem(id: DocId,
                   target: DocItem.Target,
                   kind: DocItem.Kind,
                   content: Markdown,
                   created: Date,
                   source: DocItem.Source)

object DocItem {

  sealed trait Target // what is documented
  case class DatabaseTarget(db: DatabaseName) extends Target // a database
  case class TableTarget(db: DatabaseName, table: TableName) extends Target // a table
  case class ColumnTarget(db: DatabaseName, table: TableName, column: ColumnName) extends Target // a field

  sealed trait Kind // the kind of documentations
  case object Owner extends Kind // who is responsible of the target
  case object Description extends Kind // functional description of the target
  case object Usage extends Kind // exemple on how to use the target
  case object Values extends Kind // possibles values of the target

  sealed trait Source // where the documentation comes from
  case class UserSource(id: UserId) extends Source // filled by user in UI
  case class MetastoreSource() extends Source // taken from metastore comments
  case class DataflowSource(project: String, job: String) extends Source // taken from dataflow job

}
