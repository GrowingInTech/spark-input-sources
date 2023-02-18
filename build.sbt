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
val projectName = "spark-input-sources"
val projectVersion = "0.1.2"

lazy val sparkVersion = "3.3.0"
lazy val scalatestVersion = "3.2.14"

// https://github.com/djspiewak/sbt-github-packages/issues/24
 githubTokenSource := TokenSource.GitConfig("github.token")  || TokenSource.Environment("GITHUB_TOKEN")

lazy val commonSettings = Seq(
  organization := "com.growingintech",
  scalaVersion := "2.12.15",
  version := projectVersion
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := projectName,
    dependencyOverrides ++= {
      List(
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.4.2"
      )
    },
    libraryDependencies ++= {
      List(
        "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
        "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
        "com.github.pureconfig" %% "pureconfig" % "0.17.2",
        "io.delta" %% "delta-core" % "2.2.0",
        "com.google.cloud.spark" %% "spark-bigquery" % "0.28.0",

        // For unit testing
        "org.scalatest" %% "scalatest" % scalatestVersion % Test,
        "org.scalatest" %% "scalatest-shouldmatchers" % scalatestVersion % Test,
        "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0" % Test,
        "org.scalacheck" %% "scalacheck" % "1.17.0" % Test
      )
    }
  )

 resolvers += Resolver.githubPackages("GrowingInTech", projectName)
 githubOwner := "GrowingInTech"
 githubRepository := projectName
 publishMavenStyle := true
//
// publishTo := githubPublishTo.value
