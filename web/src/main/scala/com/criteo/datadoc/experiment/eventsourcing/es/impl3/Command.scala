package com.criteo.datadoc.experiment.eventsourcing.es.impl3

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.{DocItem, Markdown}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Table
import com.criteo.datadoc.experiment.eventsourcing.es.common.Command

case class UpdateSchema(schema: Seq[Table]) extends Command //
case class UpdateDoc(target: DocItem.Target, kind: DocItem.Kind, content: Markdown, source: DocItem.Source) extends Command //
