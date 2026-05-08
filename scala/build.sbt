ThisBuild / scalaVersion := "3.3.3"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "dev.kdtree"

lazy val root = (project in file("."))
  .settings(
    name := "poc-tree-ds-kata",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.2" % Test
  )
