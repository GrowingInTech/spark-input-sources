[![Scala CI](https://github.com/GrowingInTech/spark-input-sources/actions/workflows/CI.yml/badge.svg?branch=main)](https://github.com/GrowingInTech/spark-input-sources/actions/workflows/CI.yml)
[![codecov](https://codecov.io/gh/GrowingInTech/spark-input-sources/branch/main/graph/badge.svg?token=NYHE243GEU)](https://codecov.io/gh/GrowingInTech/spark-input-sources)
# Input Sources
Input Sources is an abstraction for loading Spark data via configuration files. Currently, it can handle

* file path sources
* table sources
* SQL sources
* BigQuery sources

This library aims to be easily extended to other sources by using sealed trait with case classes for each new sources.  
```scala
// https://mvnrepository.com/artifact/com.growingintech/spark-input-sources
libraryDependencies += "com.growingintech" %% "spark-input-sources" % "1.0.0"
````

## New Sources
Feel free to submit a PR for any new sources you would like to add. I don't plan on creating cloud accounts for all
clouds, so it will be helpful if others can work on Amazon and Azure.

## Basic Usage
In this simple example, we have a HOCON pipeline configuration string which can have as many parameters as needed for
the user's use case. For our data definition, I am using a `TableSource` example.
```scala
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
import com.growingintech.datasources.InputSources
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

import org.apache.spark.sql.DataFrame

val strConfig: String =
  """
    |{
    | pipeline-name: Data Runner
    | date: 20230216
    | data: {
    |   type: table-source
    |   table-name: default.test_data
    |   filter: "date = 20230101 AND x > 2"
    | }
    |}
    |""".stripMargin

case class Params(
                   pipelineName: String,
                   date: Int,
                   data: InputSources
                 )

val config: Params = ConfigSource.fromConfig(ConfigFactory.parseString(strConfig)).loadOrThrow[Params]
val df: Dataframe = Params.data.loadData
```