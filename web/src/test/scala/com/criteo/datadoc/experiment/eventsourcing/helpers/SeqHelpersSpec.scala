package com.criteo.datadoc.experiment.eventsourcing.helpers

import org.scalatest.{FunSpec, Matchers}

class SeqHelpersSpec extends FunSpec with Matchers {

  import SeqHelpers._

  describe("diff") {
    it("should have only created elements") {
      diff[Int](Seq(), Seq(1, 2), _ == _) shouldBe(Seq(), Seq(1, 2), Seq())
    }
    it("should have only deleted elements") {
      diff[Int](Seq(1, 2), Seq(), _ == _) shouldBe(Seq(), Seq(), Seq(1, 2))
    }
    it("should have only modified elements") {
      diff[Int](Seq(1, 2), Seq(1, 2), _ == _) shouldBe(Seq(1 -> 1, 2 -> 2), Seq(), Seq())
    }
    it("should work in complex case") {
      diff[Int](Seq(1, 2, 3, 4), Seq(2, 5), _ == _) shouldBe(Seq(2 -> 2), Seq(5), Seq(1, 3, 4))
    }
  }

  describe("join") {
    it("should have only created elements") {
      join[Int](Seq(), Seq(1, 2), _ == _) shouldBe Seq(None -> Some(1), None -> Some(2))
    }
    it("should have only deleted elements") {
      join[Int](Seq(1, 2), Seq(), _ == _) shouldBe Seq(Some(1) -> None, Some(2) -> None)
    }
    it("should have only modified elements") {
      join[Int](Seq(1, 2), Seq(1, 2), _ == _) shouldBe Seq(Some(1) -> Some(1), Some(2) -> Some(2))
    }
    it("should work in complex case") {
      join[Int](Seq(1, 2, 3, 4), Seq(2, 5), _ == _) shouldBe Seq(Some(1) -> None, Some(2) -> Some(2), Some(3) -> None, Some(4) -> None, None -> Some(5))
    }
  }
}
