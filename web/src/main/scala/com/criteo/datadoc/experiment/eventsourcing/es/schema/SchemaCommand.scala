package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Table
import com.criteo.datadoc.experiment.eventsourcing.es.common.Command

sealed trait SchemaCommand extends Command // all commands related to schema
case class UpdateSchema(schema: Seq[Table]) extends SchemaCommand // full update of the schema
