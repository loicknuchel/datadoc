package com.criteo.datadoc.es.schema

import com.criteo.datadoc.es.EventHandler

import scala.concurrent.Future

class SchemaEventRepository(initEvents: Seq[SchemaEvent]) extends EventHandler[SchemaEvent] {
  override def handle(event: SchemaEvent): Unit = ???
}

object SchemaEventRepository {
  def readEvents(): Future[Seq[SchemaEvent]] = ???
}
