package com.criteo.datadoc.experiment.eventsourcing.helpers

object SeqHelpers {
  def diff[A](prev: Seq[A], next: Seq[A], eq: (A, A) => Boolean): (Seq[(A, A)], Seq[A], Seq[A]) = {
    val existing = prev.flatMap(a1 => next.find(eq(a1, _)).map(a2 => (a1, a2)))
    val created = next.filter(notIn(prev, eq))
    val deleted = prev.filter(notIn(next, eq))
    (existing, created, deleted)
  }

  def join[A](prev: Seq[A], next: Seq[A], eq: (A, A) => Boolean): Seq[(Option[A], Option[A])] = {
    prev.map(a => (Some(a), next.find(eq(a, _)))) ++
      next.filter(notIn(prev, eq)).map(a => (None, Some(a)))
  }

  private def notIn[A](other: Seq[A], eq: (A, A) => Boolean)(a: A): Boolean =
    !other.exists(eq(a, _))
}
