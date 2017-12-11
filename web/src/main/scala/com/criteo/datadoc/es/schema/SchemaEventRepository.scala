package com.criteo.datadoc.es.schema

import com.criteo.datadoc.es.EventHandler

import scala.concurrent.Future

class SchemaEventRepository extends EventHandler[SchemaEvent] {
  override def init(events: Seq[SchemaEvent]): Unit = () // nothing to do here

  override def handle(event: SchemaEvent): Unit = ???
}

object SchemaEventRepository {
  def readEvents(): Future[Seq[SchemaEvent]] = ???
}
