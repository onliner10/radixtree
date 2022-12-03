package com.rklaehn.radixtree

import org.openjdk.jmh.annotations.{Level, Scope, Setup, State}

import scala.io.Source

@State(Scope.Benchmark)
//noinspection SourceNotClosed
class BenchFixture {
  var names: Array[String] = _
  var kvs: Array[(String, Unit)] = _

  //noinspection SourceNotClosed
  @Setup(Level.Trial)
  def setupBenchmark(): Unit = {
    names = Source.fromURL("https://github.com/dwyl/english-words/blob/master/words.txt?raw=true").getLines.toArray
    kvs = names.map(s => s -> ())
  }
}
