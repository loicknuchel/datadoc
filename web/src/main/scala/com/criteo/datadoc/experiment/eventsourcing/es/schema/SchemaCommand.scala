package com.criteo.datadoc.experiment.eventsourcing.es.schema

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Database

sealed trait SchemaCommand // all commands related to schema
case class UpdateSchema(databases: Seq[Database]) extends SchemaCommand // full update of the schema
