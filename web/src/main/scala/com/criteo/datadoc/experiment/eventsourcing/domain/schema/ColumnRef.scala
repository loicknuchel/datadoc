package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class ColumnRef(db: DatabaseName,
                     table: TableName,
                     column: ColumnName)
