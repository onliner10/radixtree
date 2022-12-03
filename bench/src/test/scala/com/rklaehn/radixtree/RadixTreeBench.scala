package com.rklaehn.radixtree

import org.openjdk.jmh.annotations._

import scala.io.Source
import scala.util.hashing.Hashing

@State(Scope.Benchmark)
class RadixTreeBench {
  var tree: RadixTree[String, Unit] = _

  @Setup(Level.Trial)
  def setupBenchmark(fixture: BenchFixture): Unit = {
    tree = RadixTree(fixture.kvs: _*).packed
  }

  implicit object EqHashing extends Hashing[Unit] {
    override def hash(x: Unit): Int = 0
  }

  @Benchmark
  def create1000(fixture: BenchFixture): RadixTree[String, Unit] = {
    RadixTree(fixture.kvs: _*)
  }

  @Benchmark
  def lookup1000(fixture: BenchFixture): Boolean = {
    fixture.kvs.forall {
      case (k, _) => tree.contains(k)
    }
  }

  @Benchmark
  def filterPrefix(): RadixTree[String, Unit] = {
    tree.filterPrefix("one")
  }

  @Benchmark
  def filterContains(): Boolean = {
    tree.contains("one")
  }
}
