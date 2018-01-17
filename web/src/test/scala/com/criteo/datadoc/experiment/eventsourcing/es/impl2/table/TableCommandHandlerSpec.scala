package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.Column
import com.criteo.datadoc.experiment.eventsourcing.es.common.EventFull
import org.scalatest.{FunSpec, Matchers}

class TableCommandHandlerSpec extends FunSpec with Matchers {
  def tableCommandHandlerAssert(given: Seq[TableEvent], when: TableCommand, expect: Seq[TableEvent]): Unit =
    new TableCommandHandler(given.zipWithIndex.map { case (e, i) => EventFull(e, i, new Date()) }, Seq()).handle(when) match {
      case Right(events) => events.map(_.e) should contain theSameElementsAs expect
      case Left(errors) => fail(s"${Left(errors)} found when ${Right(expect)} expected")
    }

  describe("SchemaCommandHandler") {
    it("should not send events if schema stays empty") {
      tableCommandHandlerAssert(
        given = Seq(),
        when = UpdateSchema(None),
        expect = Seq())
    }
    it("should send TableCreated event") {
      tableCommandHandlerAssert(
        given = Seq(),
        when = UpdateSchema(Some(Seq())),
        expect = Seq(TableCreated(Seq())))
    }
    it("should send TableDeleted event") {
      tableCommandHandlerAssert(
        given = Seq(TableCreated(Seq())),
        when = UpdateSchema(None),
        expect = Seq(TableDeleted()))
    }
    it("should send ColumnCreated event") {
      tableCommandHandlerAssert(
        given = Seq(TableCreated(Seq())),
        when = UpdateSchema(Some(Seq(Column("name", "typ", 1)))),
        expect = Seq(ColumnCreated(Column("name", "typ", 1))))
    }
    it("should send ColumnDeleted event") {
      tableCommandHandlerAssert(
        given = Seq(TableCreated(Seq(Column("name", "typ", 1)))),
        when = UpdateSchema(Some(Seq())),
        expect = Seq(ColumnDeleted("name")))
    }
    it("should send ColumnUpdated event") {
      tableCommandHandlerAssert(
        given = Seq(TableCreated(Seq(Column("name", "typ", 1)))),
        when = UpdateSchema(Some(Seq(Column("name", "typ2", 1)))),
        expect = Seq(ColumnUpdated(Column("name", "typ2", 1))))
    }
    it("should work with complex use case") {
      tableCommandHandlerAssert(
        given = Seq(
          TableCreated(Seq(Column("c1", "typ", 1))),
          ColumnCreated(Column("c2", "typ", 2)),
          ColumnCreated(Column("c3", "typ", 3)),
          ColumnDeleted("c2")),
        when = UpdateSchema(Some(Seq(Column("c3", "typ", 1)))),
        expect = Seq(
          ColumnDeleted("c1"),
          ColumnUpdated(Column("c3", "typ", 1))))
    }
  }
}
