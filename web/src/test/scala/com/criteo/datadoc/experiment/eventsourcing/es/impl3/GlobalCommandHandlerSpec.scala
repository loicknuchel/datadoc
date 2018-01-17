package com.criteo.datadoc.experiment.eventsourcing.es.impl3

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.UserId
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem._
import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, Table}
import com.criteo.datadoc.experiment.eventsourcing.es.common.{Command, CommandFull, Event, EventFull}
import org.scalatest.{FunSpec, Matchers}

class GlobalCommandHandlerSpec extends FunSpec with Matchers {
  val now = new Date()
  def globalCommandHandlerAssert(given: Seq[Event], when: Command, expect: Seq[Event], asserts: GlobalCommandHandler => Unit = _ => ()): Unit = {
    val commandHandler = new GlobalCommandHandler(given.zipWithIndex.map { case (e, i) => EventFull(e, i, now) }, Seq())
    commandHandler.handle(CommandFull(when, now)) match {
      case Right(events) => events.map(_.e) should contain theSameElementsAs expect
      case Left(errors) => fail(s"${Left(errors)} found when ${Right(expect)} expected")
    }
    asserts(commandHandler)
  }

  describe("GlobalCommandHandler") {
    describe("handle") {
      it("should not send events if schema stays empty") {
        globalCommandHandlerAssert(
          given = Seq(),
          when = UpdateSchema(Seq()),
          expect = Seq(),
          asserts = s => {
            s.getTables() shouldBe Seq()
          })
      }
      it("should send TableCreated event") {
        globalCommandHandlerAssert(
          given = Seq(),
          when = UpdateSchema(Seq(Table("db", "table", Seq()))),
          expect = Seq(TableCreated("db", "table", Seq())))
      }
      it("should send TableDeleted event") {
        globalCommandHandlerAssert(
          given = Seq(TableCreated("db", "table", Seq())),
          when = UpdateSchema(Seq()),
          expect = Seq(TableDeleted("db", "table")))
      }
      it("should send ColumnCreated event") {
        globalCommandHandlerAssert(
          given = Seq(TableCreated("db", "table", Seq())),
          when = UpdateSchema(Seq(Table("db", "table", Seq(Column("name", "typ", 1))))),
          expect = Seq(ColumnCreated("db", "table", Column("name", "typ", 1))))
      }
      it("should send ColumnDeleted event") {
        globalCommandHandlerAssert(
          given = Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1)))),
          when = UpdateSchema(Seq(Table("db", "table", Seq()))),
          expect = Seq(ColumnDeleted("db", "table", "name")))
      }
      it("should send ColumnUpdated event") {
        globalCommandHandlerAssert(
          given = Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1)))),
          when = UpdateSchema(Seq(Table("db", "table", Seq(Column("name", "typ2", 1))))),
          expect = Seq(ColumnUpdated("db", "table", Column("name", "typ2", 1))))
      }
      it("should work with complex use case") {
        globalCommandHandlerAssert(
          given = Seq(
            TableCreated("db", "t1", Seq(Column("c1", "typ", 1))),
            TableCreated("db", "t2", Seq()),
            DocUpdated(TableTarget("db", "t2"), Description, "doc", UserSource(UserId(""))),
            ColumnCreated("db", "t2", Column("c1", "typ", 1))),
          when = UpdateSchema(Seq(
            Table("db", "t2", Seq(Column("c1", "typ", 1))),
            Table("db", "t3", Seq(Column("c1", "typ", 1))))),
          expect = Seq(
            TableDeleted("db", "t1"),
            TableCreated("db", "t3", Seq(Column("c1", "typ", 1)))),
          asserts = s => {
            s.getTables("db").length shouldBe 2
            s.getTableDoc("db", "t2") shouldBe Map(Description -> DocItem(TableTarget("db", "t2"), Description, "doc", now, UserSource(UserId(""))))
          })
      }
    }
    describe("state") {

    }
  }
}
