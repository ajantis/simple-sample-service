import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtStartScript
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

object SimpleServiceBuild extends Build {
  val Organization = "some-company"
  val Version      = "1.0-SNAPSHOT"
  val ScalaVersion = "2.10.3"

  lazy val simpleService = Project(
    id = "simpleservice",
    base = file("."),
    settings = defaultSettings ++
      Seq(SbtStartScript.stage in Compile := Unit),
    aggregate = Seq(service)
  )
  
  lazy val service = Project(
    id = "service",
    base = file("service"),
    dependencies = Seq(),
    settings = defaultSettings ++
      SbtStartScript.startScriptForClassesSettings ++
      Seq(libraryDependencies ++= Dependencies.service)
  )

  lazy val buildSettings = Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion
  )

  lazy val defaultSettings = Defaults.defaultSettings ++ formatSettings ++ buildSettings ++ Seq(
    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-optimise", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    mainClass in (Compile, run) := Some("com.sample.service.ServiceMain"),

    // disable parallel tests
    parallelExecution in Test := false
  )

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}

object Dependencies {
  import Dependency._
  val service = Seq(akkaActor, sprayCan, sprayRouting, sprayJson, jUnit, akkaTestKit, specs2)
}

object Dependency {
  object Version {
    val Akka      = "2.3.0"
    val JUnit     = "4.10"
    val Specs2    = "2.2.2"
    val Spray     = "1.3.1"
    val SprayJson = "1.2.5"
  }

  // ---- Application dependencies ----
 
  val akkaActor      = "com.typesafe.akka"  %%  "akka-actor"        % Version.Akka
  val sprayCan       = "io.spray"           %   "spray-can"         % Version.Spray
  val sprayRouting   = "io.spray"           %   "spray-routing"     % Version.Spray
  val sprayJson      = "io.spray"           %%  "spray-json"        % Version.SprayJson
  
  // ---- Test dependencies ----

  val specs2      = "org.specs2"          %% "specs2"                  % Version.Specs2     % "test"
  val akkaTestKit = "com.typesafe.akka"   %% "akka-testkit"            % Version.Akka       % "test"
  val jUnit       = "junit"               % "junit"                    % Version.JUnit      % "test"
}
