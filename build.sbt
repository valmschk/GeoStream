val scala3Version = "3.3.4"
val catsEffectVersion = "3.5.7"
val fs2Version = "3.11.0"
val skunkVersion = "0.6.4"
val http4sVersion = "0.23.27"
val circeVersion = "0.14.10"

lazy val root = project
  .in(file("."))
  .settings(
    name := "pet-cat",
    version := "0.1.0",
    scalaVersion := scala3Version,

    Compile / run / fork := true,
    Compile / run / javaOptions += "-Dfile.encoding=UTF-8",

    libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "co.fs2"        %% "fs2-core"    % fs2Version,
  "co.fs2"        %% "fs2-io"      % fs2Version,
  "org.tpolecat"  %% "skunk-core"  % skunkVersion,
  "org.tpolecat"  %% "natchez-core" % "0.3.5",   
  "org.http4s"    %% "http4s-ember-server" % http4sVersion,
  "org.http4s"    %% "http4s-dsl"          % http4sVersion,
  "org.http4s"    %% "http4s-circe"        % http4sVersion,
  "io.circe"      %% "circe-generic" % circeVersion,
  "org.slf4j"     %  "slf4j-simple"  % "2.0.13",
  "org.typelevel" %% "cats-effect-testing-specs2" % "1.5.0" % Test
)
  )