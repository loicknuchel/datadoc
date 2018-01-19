package com.criteo.datadoc.experiment.eventsourcing.domain.documentation

import com.criteo.datadoc.experiment.eventsourcing.domain.common.{ColumnFlag, Meta}
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.ColumnRef

case class ColumnDoc(possibleValues: Option[Meta[Markdown]] = None,
                     unit: Option[Meta[String]] = None, // TODO: should be Enum
                     description: Option[Meta[Markdown]] = None,
                     dependsOn: Option[Meta[Seq[ColumnRef]]] = None,
                     technical: Option[Meta[Markdown]] = None,
                     usage: Option[Meta[Markdown]] = None,
                     domain: Option[Meta[String]] = None, // TODO: should be Enum
                     warning: Option[Meta[Markdown]] = None,
                     flags: Option[Meta[Seq[ColumnFlag]]] = None,
                     sample: Option[Meta[Seq[String]]] = None) // `SELECT distinct field FROM table LIMIT 10;`
