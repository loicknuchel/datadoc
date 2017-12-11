package com.criteo.datadoc.web

import com.criteo.datadoc.es.schema.{SchemaCommandHandler, SchemaRead, SchemaEventRepository}

class Main {
  def main(args: Array[String]): Unit = {
    val schemaRead = new SchemaRead()
    val schemaWrite = new SchemaEventRepository()
    val schemaCommandHandler = new SchemaCommandHandler(Seq(schemaWrite, schemaRead))

  }
}
