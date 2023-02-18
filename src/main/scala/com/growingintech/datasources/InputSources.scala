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
                       filter: Option[String] = None,
                       format: String,
                       versionOrTime: Option[String] = None,
                       optionValue: Option[String] = None
                     ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    if (filter.isDefined) {
      withFilter
    }
    else {
      withoutFilter
    }
  }

  private def withFilter: DataFrame = {
    if (format != "delta" & versionOrTime.isEmpty & optionValue.isEmpty) {
      spark.read.format(format).load(filePath).filter(filter.get)
    }
    else {
      exceptionCheck()
      spark.read.format(format)
        .option(optionValue.get, versionOrTime.get)
        .load(filePath)
        .filter(filter.get)
    }
  }

  private def withoutFilter: DataFrame = {
    if (format != "delta" & versionOrTime.isEmpty & optionValue.isEmpty) {
      spark.read.format(format).load(filePath)
    }
    else {
      exceptionCheck()
      spark.read.format(format)
        .option(optionValue.get, versionOrTime.get)
        .load(filePath)
    }
  }

  private def exceptionCheck(): Unit = {
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
                        filter: Option[String] = None
                      ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    if (filter.isDefined) {
      spark.table(tableName).filter(filter.get)
    }
    else {
      spark.table(tableName)
    }
  }
}

case class BigQuerySource(
                           query: String,
                           dataset: String,
                           fasterExecution: Boolean = false
                         ) extends InputSources with SparkSessionWrapper {

  override def loadData: DataFrame = {

    spark.conf.set("materializationDataset", dataset)
    spark.conf.set("viewsEnabled", "true")

    if (fasterExecution) {
      // faster but creates temporary tables in the BQ account
      spark.read.format("bigquery").option("query", query).load()
    }
    else {
      // slower but no table creation
      spark.read.format("bigquery").load(query)
    }
  }
}
