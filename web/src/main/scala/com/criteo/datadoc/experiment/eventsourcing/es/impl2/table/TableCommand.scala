package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.{DocItem, Markdown}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Column
import com.criteo.datadoc.experiment.eventsourcing.es.common.Command

sealed trait TableCommand extends Command //
case class UpdateSchema(table: Option[Seq[Column]]) extends TableCommand //
case class UpdateDoc(target: DocItem.Target, kind: DocItem.Kind, content: Markdown, source: DocItem.Source) extends TableCommand //
