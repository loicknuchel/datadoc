package com.criteo.datadoc.experiment.eventsourcing.es.impl1.schema

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, Table}
import com.criteo.datadoc.experiment.eventsourcing.es.common.EventFull
import org.scalatest.{FunSpec, Matchers}

class SchemaCommandHandlerSpec extends FunSpec with Matchers {
  def schemaCommandHandlerAssert(given: Seq[SchemaEvent], when: SchemaCommand, expect: Seq[SchemaEvent]): Unit =
    new SchemaCommandHandler(given.zipWithIndex.map { case (e, i) => EventFull(e, i, new Date()) }, Seq()).handle(when) match {
      case Right(events) => events.map(_.e) should contain theSameElementsAs expect
      case Left(errors) => fail(s"${Left(errors)} found when ${Right(expect)} expected")
    }

  describe("SchemaCommandHandler") {
    it("should not send events if schema stays empty") {
      schemaCommandHandlerAssert(
        given = Seq(),
        when = UpdateSchema(Seq()),
        expect = Seq())
    }
    it("should send TableCreated event") {
      schemaCommandHandlerAssert(
        given = Seq(),
        when = UpdateSchema(Seq(Table("db", "table", Seq()))),
        expect = Seq(TableCreated("db", "table", Seq())))
    }
    it("should send TableDeleted event") {
      schemaCommandHandlerAssert(
        given = Seq(TableCreated("db", "table", Seq())),
        when = UpdateSchema(Seq()),
        expect = Seq(TableDeleted("db", "table")))
    }
    it("should send ColumnCreated event") {
      schemaCommandHandlerAssert(
        given = Seq(TableCreated("db", "table", Seq())),
        when = UpdateSchema(Seq(Table("db", "table", Seq(Column("name", "typ", 1))))),
        expect = Seq(ColumnCreated("db", "table", Column("name", "typ", 1))))
    }
    it("should send ColumnDeleted event") {
      schemaCommandHandlerAssert(
        given = Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1)))),
        when = UpdateSchema(Seq(Table("db", "table", Seq()))),
        expect = Seq(ColumnDeleted("db", "table", "name")))
    }
    it("should send ColumnUpdated event") {
      schemaCommandHandlerAssert(
        given = Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1)))),
        when = UpdateSchema(Seq(Table("db", "table", Seq(Column("name", "typ2", 1))))),
        expect = Seq(ColumnUpdated("db", "table", Column("name", "typ2", 1))))
    }
    it("should work with complex use case") {
      schemaCommandHandlerAssert(
        given = Seq(
          TableCreated("db", "t1", Seq(Column("c1", "typ", 1))),
          TableCreated("db", "t2", Seq()),
          ColumnCreated("db", "t2", Column("c1", "typ", 1))),
        when = UpdateSchema(Seq(
          Table("db", "t2", Seq(Column("c1", "typ", 1))),
          Table("db", "t3", Seq(Column("c1", "typ", 1))))),
        expect = Seq(
          TableDeleted("db", "t1"),
          TableCreated("db", "t3", Seq(Column("c1", "typ", 1)))))
    }
  }
}
