name := "My Project"
 
version := "1.0"
 
scalaVersion := "2.9.1"
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.2"

libraryDependencies += "com.typesafe.akka" % "akka-testkit" % "2.0.2"

libraryDependencies += "junit" % "junit" % "4.8.1"

libraryDependencies += "org.scalatest" % "scalatest_2.9.1" % "1.6.1"
