package com.rklaehn.radixtree

import org.openjdk.jmh.annotations._

import scala.collection.immutable.SortedMap
import scala.util.hashing.Hashing

@State(Scope.Benchmark)
class SortedMapBench {
  var sortedMap: SortedMap[String, Unit] = _

  @Setup(Level.Trial)
  def setupBenchmark(fixture: BenchFixture): Unit = {
    sortedMap = SortedMap(fixture.kvs: _*)
  }

  implicit object EqHashing extends Hashing[Unit] {
    override def hash(x: Unit): Int = 0
  }

  @Benchmark
  def create1000(fixture: BenchFixture): SortedMap[String, Unit] = {
    SortedMap(fixture.kvs: _*)
  }

  @Benchmark
  def lookup1000(fixture: BenchFixture): Boolean = {
    fixture.kvs.forall {
      case (k, _) => sortedMap.contains(k)
    }
  }

  @Benchmark
  def filterPrefix(): SortedMap[String, Unit] = {
    sortedMap.filter { case (k,_) => k.startsWith("one") }
  }


  @Benchmark
  def filterContains(): Boolean = {
    sortedMap.contains("one")
  }
}