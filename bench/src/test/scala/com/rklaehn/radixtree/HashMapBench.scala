package com.rklaehn.radixtree

import org.openjdk.jmh.annotations._

import scala.collection.immutable.{HashMap, SortedMap}
import scala.util.hashing.Hashing

@State(Scope.Benchmark)
class HashMapBench {
  var hashMap: HashMap[String, Unit] = _

  @Setup(Level.Trial)
  def setupBenchmark(fixture: BenchFixture): Unit = {
    hashMap = HashMap(fixture.kvs: _*)
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
      case (k, _) => hashMap.contains(k)
    }
  }

  @Benchmark
  def filterPrefix(): HashMap[String, Unit] = {
    hashMap.filter { case (k,_) => k.startsWith("one") }
  }

  @Benchmark
  def filterContains(): Boolean = {
    hashMap.contains("one")
  }
}
