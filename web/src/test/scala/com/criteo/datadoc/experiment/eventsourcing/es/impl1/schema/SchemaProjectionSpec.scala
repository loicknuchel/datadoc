package com.criteo.datadoc.experiment.eventsourcing.es.impl1.schema

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.schema.{Column, Table}
import com.criteo.datadoc.experiment.eventsourcing.es.common.EventFull
import org.scalatest.{FunSpec, Matchers}

class SchemaProjectionSpec extends FunSpec with Matchers {
  def projectionWith(given: Seq[SchemaEvent]): SchemaProjection =
    new SchemaProjection(given.zipWithIndex.map { case (e, i) => EventFull(e, i, new Date()) })

  describe("SchemaProjection") {
    it("should have no table at the begining") {
      val p = projectionWith(Seq())
      p.getTables() shouldBe Seq()
    }
    it("should have a table if TableCreated") {
      val p = projectionWith(Seq(TableCreated("db", "table", Seq())))
      p.getTables() shouldBe Seq(Table("db", "table", Seq()))
      p.getTables("db") shouldBe Seq(Table("db", "table", Seq()))
      p.getTables("other") shouldBe Seq()
      p.getTable("db", "table") shouldBe Some(Table("db", "table", Seq()))
      p.getTable("db", "other") shouldBe None
    }
    it("should have no table if TableDeleted") {
      val p = projectionWith(Seq(TableCreated("db", "table", Seq()), TableDeleted("db", "table")))
      p.getTables() shouldBe Seq()
    }
    it("should have a table with a column if ColumnCreated") {
      val p = projectionWith(Seq(TableCreated("db", "table", Seq()), ColumnCreated("db", "table", Column("name", "typ", 1))))
      p.getTable("db", "table") shouldBe Some(Table("db", "table", Seq(Column("name", "typ", 1))))
    }
    it("should have no column if ColumnDeleted") {
      val p = projectionWith(Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1))), ColumnDeleted("db", "table", "name")))
      p.getTable("db", "table") shouldBe Some(Table("db", "table", Seq()))
    }
    it("should update the column if ColumnDeleted") {
      val p = projectionWith(Seq(TableCreated("db", "table", Seq(Column("name", "typ", 1))), ColumnUpdated("db", "table", Column("name", "king", 1))))
      p.getTable("db", "table") shouldBe Some(Table("db", "table", Seq(Column("name", "king", 1))))
    }
  }
}
