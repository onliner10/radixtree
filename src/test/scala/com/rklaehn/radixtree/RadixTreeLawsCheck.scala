package com.rklaehn.radixtree

import algebra.instances.all._
import org.scalacheck.Arbitrary
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import Instances._
import algebra.laws.RingLaws
import cats.kernel.laws.discipline.MonoidTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers

class RadixTreeLawsCheck extends AnyFunSuite with FunSuiteDiscipline with Checkers {

  implicit def arbRadixTree[K: Arbitrary : RadixTree.Key, V: Arbitrary]: Arbitrary[RadixTree[K, V]] = Arbitrary {
    for {
      kvs <- Arbitrary.arbitrary[List[(K, V)]]
    } yield
    RadixTree(kvs: _*)
  }

  checkAll("MonoidTests[RadixTree[String, String]].monoid", MonoidTests[RadixTree[String, String]].monoid)
  checkAll("MonoidTests[RadixTree[Array[Byte], Array[Byte]]].monoid", MonoidTests[RadixTree[Array[Byte], Array[Byte]]].monoid)
  checkAll("RingLaws[RadixTree[String, Byte]].additiveMonoid", RingLaws[RadixTree[String, Short]].additiveMonoid)
  checkAll("RingLaws[RadixTree[Array[Byte], Int]].additiveMonoid", RingLaws[RadixTree[String, Int]].additiveMonoid)
}
