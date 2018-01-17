package com.criteo.datadoc.experiment.eventsourcing.es.impl2.table

import java.util.Date

import com.criteo.datadoc.experiment.eventsourcing.domain.UserId
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem
import com.criteo.datadoc.experiment.eventsourcing.domain.documentation.DocItem._
import com.criteo.datadoc.experiment.eventsourcing.es.common.EventFull
import org.scalatest.{FunSpec, Matchers}

class DocProjectionSpec extends FunSpec with Matchers {
  private val now = new Date()
  private val source = UserSource(UserId("i"))
  private val tableTarget = TableTarget("db", "table")
  private val columnTarget = (name: String) => ColumnTarget("db", "table", name)

  private def projectionWith(given: Seq[TableEvent]): DocProjection =
    new DocProjection(given.zipWithIndex.map { case (e, i) => EventFull(e, i, now) })

  describe("DocProjection") {
    it("should have no doc at the begining") {
      val p = projectionWith(Seq())
      p.getDoc() shouldBe(Map(), Map())
    }
    it("should have some doc when updated") {
      val p = projectionWith(Seq(DocUpdated(tableTarget, Description, "content", source)))
      p.getDoc() shouldBe(Map(Description -> DocItem(tableTarget, Description, "content", now, source)), Map())
    }
    it("should replace doc with same target & kind") {
      val p = projectionWith(Seq(
        DocUpdated(tableTarget, Description, "content", source),
        DocUpdated(tableTarget, Description, "content2", source)))
      p.getDoc() shouldBe(Map(Description -> DocItem(tableTarget, Description, "content2", now, source)), Map())
    }
    it("should support complex edits") {
      val p = projectionWith(Seq(
        DocUpdated(tableTarget, Description, "content", source),
        DocUpdated(columnTarget("c1"), Description, "content", source),
        DocUpdated(columnTarget("c2"), Description, "content", source),
        DocUpdated(columnTarget("c1"), Description, "content2", source),
        DocUpdated(columnTarget("c1"), Technical, "content", source)))
      p.getDoc() shouldBe(
        Map(
          Description -> DocItem(tableTarget, Description, "content", now, source)),
        Map(
          "c1" -> Map(
            Description -> DocItem(columnTarget("c1"), Description, "content2", now, source),
            Technical -> DocItem(columnTarget("c1"), Technical, "content", now, source)),
          "c2" -> Map(
            Description -> DocItem(columnTarget("c2"), Description, "content", now, source)))
      )
    }
  }
}
