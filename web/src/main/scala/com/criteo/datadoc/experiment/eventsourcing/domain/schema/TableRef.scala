package com.criteo.datadoc.experiment.eventsourcing.domain.schema

case class TableRef(db: DatabaseName,
                    table: TableName)
