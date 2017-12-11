package com.criteo.datadoc.es.schema

import com.criteo.datadoc.domain.schema.Database

sealed trait SchemaCommand // all commands related to schema
case class UpdateSchema(databases: Seq[Database]) extends SchemaCommand // full update of the schema
