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
package com.growingintech.datasources

import com.growingintech.SparkSessionWrapper

import org.apache.spark.sql.DataFrame

/**
 * With this sealed trait, we can use InputSources for loading data from
 * Spark file(s), query, or a table.
 */
sealed trait InputSources {

  def loadData: DataFrame
}

case class FileSource(
                       filePath: String,
                       filter: String,
                       format: String,
                       versionOrTime: Option[String] = None,
                       optionValue: Option[String] = None
                     ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    if (format != "delta" & versionOrTime.isEmpty & optionValue.isEmpty) {
      spark.read.format(format).load(filePath).filter(filter)
    }
    else {
      if (versionOrTime.isDefined & format != "delta") {
        throw new IllegalArgumentException("versionOrTime cannot be defined when fileType is not delta.")
      }
      if (optionValue.isDefined & format != "delta") {
        throw new IllegalArgumentException("optionValue cannot be defined when fileType is not delta.")
      }
      if (optionValue.isDefined & versionOrTime.isEmpty) {
        throw new IllegalArgumentException("optionValue cannot be defined when versionOrTime is empty.")
      }
      if (optionValue.isEmpty & versionOrTime.isDefined) {
        throw new IllegalArgumentException("versionOrTime cannot be defined when optionValue is empty.")
      }
      spark.read.format(format)
        .option(optionValue.get, versionOrTime.get)
        .load(filePath)
        .filter(filter)
    }
  }
}

case class QuerySource(query: String) extends
  InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    spark.sql(query)
  }
}

case class TableSource(
                        tableName: String,
                        filter: String
                      ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    spark.table(tableName).filter(filter)
  }
}

case class BigQuerySource(
                           query: String,
                           dataset: String
                         ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    spark.conf.set("materializationDataset", dataset)
    spark.conf.set("viewsEnabled", "true")
    spark.read.format("bigquery").option("query", query).load()
  }
}
