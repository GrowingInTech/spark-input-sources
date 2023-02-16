/*
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
package com.growingintech

import scala.util.hashing.MurmurHash3

import org.apache.spark.sql.functions.{col, hash}


/**
 * Helper object to compare dataframes without using Holdenkarau (has some problems with Spark 3)
 */
object HashDataFrame extends SparkSessionWrapper {

  import spark.implicits._

  /**
   * Computes a checksum on the entire contents of the supplied DataFrame. Checksum values can be used to confirm that
   * dataframe contents are unchanged after operations that MUST NOT alter actual data
   * (e.g. HDFS leaf file compaction, etc)
   *
   * This method builds hierarchichal hashes (from row hashes -> RDD partition hashes -> to a final DF hash) which
   * makes it relatively inexpensive compared to other ways of comparing dataframes (e.g. joins, minus, etc).
   * It can be used even for very large data/paths.
   * Credit to https://github.com/beljun for this method
   *
   * @param df       Dataframe to compute the checksum for.
   * @param numParts Level of parallelism. Note that checksum value changes with different numParts value so it should
   *                 remain the same across comparisons.
   * @return Checksum for dataframe.
   */
  def checksumDataFrame(df: org.apache.spark.sql.DataFrame, numParts: Int): Int = {

    MurmurHash3.orderedHash(
      df
        .select(hash(df.columns.map(col): _*).as("row_hash"))
        .repartition(numParts, $"row_hash")
        .sortWithinPartitions("row_hash")
        .mapPartitions(p => Array(MurmurHash3.orderedHash(p)).toIterator)
        .orderBy($"value")
        .collect()
    )
  }
}
