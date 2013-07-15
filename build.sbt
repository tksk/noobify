import AssemblyKeys._

assemblySettings

name := "noobify"

version := "0.0.1"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation")

seq(webSettings :_*)

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/repo/"


EclipseKeys.withSource := true


libraryDependencies ++= Seq(
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided->default"
  , "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container->default" artifacts Artifact("javax.servlet", "jar", "jar")
  , "org.eclipse.jetty" % "jetty-webapp" % "8.1.11.v20130520" % "container"
  , "org.slf4j" % "slf4j-api" % "1.7.5"
  , "ch.qos.logback" % "logback-classic" % "1.0.13" % "runtime"
  , "org.scalatra" %% "scalatra" % "2.2.1"
  , "org.scalatra" %% "scalatra-scalate" % "2.2.1"
  , "com.tristanhunt" %% "knockoff" % "0.8.1"
  , "joda-time" % "joda-time" % "2.1"
  , "org.joda" % "joda-convert" % "1.2"
  , "org.scalaj" % "scalaj-time_2.10.2" % "0.7"
  , "org.apache.tika" % "tika-core" % "1.4"
  //,"org.scalatra" %% "scalatra-specs2" % "2.2.1" % "test"
)
