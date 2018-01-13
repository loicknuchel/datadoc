package com.criteo.datadoc.experiment.eventsourcing.helpers

import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.Properties
import org.scalacheck.ScalacheckShapeless._

object SeqHelpersProperties extends Properties("SeqHelpers") {

  import SeqHelpers._

  property("new elements should be created") = forAll { (s: Seq[Int]) =>
    diff[Int](Seq(), s, _ == _) == (Seq(), s, Seq())
  }
  property("old elements should be deleted") = forAll { (s: Seq[Int]) =>
    diff[Int](s, Seq(), _ == _) == (Seq(), Seq(), s)
  }
  property("same elements should be existing") = forAll { (s: Seq[Int]) =>
    diff[Int](s, s, _ == _) == (s.zip(s), Seq(), Seq())
  }
}
