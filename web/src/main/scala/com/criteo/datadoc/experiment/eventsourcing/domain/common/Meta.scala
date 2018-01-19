package com.criteo.datadoc.experiment.eventsourcing.domain.common

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem.Source

case class Meta[A](value: A,
                   source: Source,
                   edited: Date,
                   version: Long,
                   history: Seq[Meta[A]])

object Meta {
  def apply[A](value: A, source: Source, edited: Date, version: Long, prev: Option[Meta[A]]): Meta[A] =
    prev
      .map(m => new Meta(value, source, edited, version, m +: m.history))
      .getOrElse(new Meta(value, source, edited, version, Seq()))
}
