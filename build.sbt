import ReleaseTransformations._

lazy val commonSettings = Seq(
  organization := "com.rklaehn",
  scalaVersion := "3.2.1",
  crossScalaVersions := Seq("2.13.10", "3.0.2", "3.1.3", "3.2.1"),
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.9.0",
    "org.typelevel" %%% "algebra" % "2.9.0",
    "com.rklaehn" %%% "sonicreducer" % "1.0.0",
    "org.typelevel" %%% "algebra-laws" % "2.9.0" % "test",
    "org.scalatest" %%% "scalatest" % "3.2.14" % "test",
    "org.typelevel" %% "discipline-scalatest" % "2.2.0" % "test"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature"
  ),
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("http://github.com/rklaehn/radixtree")),

  // release stuff
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := sonatypePublishTo.value,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra :=
    <scm>
      <url>git@github.com:rklaehn/radixtree.git</url>
      <connection>scm:git:git@github.com:rklaehn/radixtree.git</connection>
    </scm>
      <developers>
        <developer>
          <id>r_k</id>
          <name>R&#xFC;diger Klaehn</name>
          <url>http://github.com/rklaehn/</url>
        </developer>
      </developers>
  ,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    ReleaseStep(action = Command.process("package", _)),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    ReleaseStep(action = Command.process("publishSigned", _)),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges))

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false)

lazy val root = coreAggregate
  .settings(name := "root")
  .settings(commonSettings: _*)
  .settings(noPublish: _*)

lazy val core = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(name := "radixtree")
  .settings(commonSettings: _*)

lazy val instrumentedTest = project.in(file("instrumentedTest"))
  .settings(name := "instrumentedTest")
  .settings(commonSettings: _*)
  .settings(instrumentedTestSettings: _*)
  .settings(noPublish: _*)
  .dependsOn(coreJVM)

lazy val instrumentedTestSettings = {
  def makeAgentOptions(classpath:Classpath) : String = {
    val jammJar = classpath.map(_.data).filter(_.toString.contains("jamm")).head
    s"-javaagent:$jammJar"
  }
  Seq(
    Test / javaOptions += makeAgentOptions((Test / dependencyClasspath).value),
      libraryDependencies += "com.github.jbellis" % "jamm" % "0.3.0" % "test",
      fork := true
    )
}

lazy val coreJVM = core.jvm
lazy val coreJS = core.js
lazy val coreAggregate =
  project.in(file("."))
    .aggregate(coreJVM, coreJS)

lazy val bench = (project in file("bench"))
  .settings(noPublish: _*)
  .settings(commonSettings: _*)
  .settings(name := "bench")
  .dependsOn(coreAggregate % "test -> test")
  .enablePlugins(JmhPlugin)