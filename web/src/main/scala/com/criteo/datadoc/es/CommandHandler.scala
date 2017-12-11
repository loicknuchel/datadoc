package com.criteo.datadoc.es

trait CommandHandler[C, E] {
  def handle(c: C): Either[Seq[String], Seq[E]]
}
