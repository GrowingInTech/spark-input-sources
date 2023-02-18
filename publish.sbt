/*
 * Copyright 2023 GrowingInTech.com. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License
 * is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
ThisBuild / organization := "com.growingintech"
ThisBuild / organizationName := "growingintech"
ThisBuild / organizationHomepage := Some(url("https://growingintech.com"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/GrowingInTech/spark-input-sources"),
    "scm:git@github.GrowingInTech/spark-input-sources.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "dwsmith1983",
    name = "Dustin Smith",
    email = "dustin.william.smith@gmail.com",
    url = url("https://dustinsmith.info")
  )
)

ThisBuild / description := "Describe your project here..."
ThisBuild / licenses := List("The license" -> new URL("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/GrowingInTech/spark-input-sources"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / publishMavenStyle := true

ThisBuild / versionScheme := Some("early-semver")
