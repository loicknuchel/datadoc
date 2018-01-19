package com.criteo.datadoc.experiment.eventsourcing.domain.documentation

import com.criteo.datadoc.experiment.eventsourcing.domain.common.{DbFlag, Meta}

case class DbDoc(description: Option[Meta[Markdown]] = None,
                 warning: Option[Meta[Markdown]] = None,
                 flags: Option[Meta[Seq[DbFlag]]] = None)
