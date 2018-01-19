package com.criteo.datadoc.experiment.eventsourcing.domain.documentation

import com.criteo.datadoc.experiment.eventsourcing.domain.common._
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.TableRef

case class TableDoc(description: Option[Meta[Markdown]] = None,
                    owner: Option[Meta[Contact]] = None,
                    dependsOn: Option[Meta[Seq[TableRef]]] = None,
                    usage: Option[Meta[Markdown]] = None,
                    sourceCode: Option[Meta[Link]] = None,
                    scheduler: Option[Meta[Link]] = None,
                    monitoring: Option[Meta[Link]] = None,
                    warning: Option[Meta[Markdown]] = None,
                    flags: Option[Meta[Seq[TableFlag]]] = None,
                    sample: Option[Meta[TableData]] = None) // `SELECT * FROM table LIMIT 10;`
