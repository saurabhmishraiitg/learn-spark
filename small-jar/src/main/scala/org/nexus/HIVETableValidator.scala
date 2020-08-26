package org.nexus

import org.apache.spark.sql.functions.{col, count, isnull, lit, lower, trim, coalesce}

/**
 * Validate 2 Given HIVE tables for all the columns
 * Input : src_tbl, tgt_tbl, PK condition, summary/detail
 * Output : Mismatch Counts, Record Report for mismatch
 * Features :
 * Handling for NULL
 * Handling for Lead/Lad Whitespace, Case Sensitive
 * Mismatch report given column wise report
 * Left/Right Join Report
 */
object HIVETableValidator extends App with CommonSparkContext {

  val src_tbl = "sample_table1"
  val tgt_tbl = "sample_table2"
  val pk = "key"

  val src = "src"
  val tgt = "tgt"

  def pk_join() = {
    col(src + "." + pk) === col(tgt + "." + pk)
  }

  def rpt_schema = {
    spark.emptyDataFrame
      .withColumn("type", lit(""))
      .withColumn("count", lit(""))
      .withColumn("key", lit(""))
      .withColumn("src_val", lit(""))
      .withColumn("tgt_val", lit(""))
  }

  val src_tbl_df = spark.table(src_tbl)
  val tgt_tbl_df = spark.table(tgt_tbl)

  var rpt_df = rpt_schema

  // Check for extra records src
  rpt_df = rpt_df.union(
    src_tbl_df.as(src).join(tgt_tbl_df.as(tgt), pk_join, "left")
      .filter(isnull(col(tgt + "." + pk)))
      .select(lit("extra record [" + src + "]").as("type"),
        count(src + "." + pk).as("count"),
        lit("-").as("key"),
        lit("-").as("src_val"),
        lit("-").as("tgt_val")
      )
  )

  // Check for extra records in tgt
  rpt_df = rpt_df.union(
    tgt_tbl_df.as(tgt).join(src_tbl_df.as(src), pk_join, "left")
      .filter(isnull(col(src + "." + pk)))
      .select(lit("extra record [" + tgt + "]").as("type"),
        count(tgt + "." + pk).as("count"),
        lit("-").as("key"),
        lit("-").as("src_val"),
        lit("-").as("tgt_val")
      )
  )

  // Compare each column for matching PK records
  val join_df = src_tbl_df.as(src).join(tgt_tbl_df.as(tgt), pk_join, "inner")

  for (col_name <- src_tbl_df.columns) {
    // 1. Matching Record, either both NULL or NULL+Empty
    // 2. Matching Records after case-conversion + TRIM
    // 3. Non-Matching Records
    rpt_df = rpt_df.union(
      join_df.filter(coalesce(col(src + "." + col_name), lit("")) === coalesce(col(tgt + "." + col_name), lit("")))
        .select(lit("coalesce, matching : [" + col_name + "]").as("type"),
          count(src + "." + pk).as("count"),
          lit("-").as("key"),
          lit("-").as("src_val"),
          lit("-").as("tgt_val")
        )
    )
      .union(
        join_df.filter(coalesce(col(src + "." + col_name), lit("")) =!= coalesce(col(tgt + "." + col_name), lit(""))
          && trim(lower(col(src + "." + col_name))) === trim(lower(col(tgt + "." + col_name))))
          .select(lit("trim, case-insensitive matching : [" + col_name + "]").as("type"),
            count(src + "." + pk).as("count"),
            lit("-").as("key"),
            lit("-").as("src_val"),
            lit("-").as("tgt_val")
          )
      )
      .union(
        join_df.filter(coalesce(col(src + "." + col_name), lit("")) =!= coalesce(col(tgt + "." + col_name), lit(""))
          && trim(lower(col(src + "." + col_name))) =!= trim(lower(col(tgt + "." + col_name))))
          .select(lit("non-matching : [" + col_name + "]").as("type"),
            lit("1").as("count"),
            col(src + "." + pk).as("key"),
            col(src + "." + col_name).as("src_val"),
            col(tgt + "." + col_name).as("tgt_val")
          )
      )
  }

  // Print the final report for review
  rpt_df.filter(col("count") =!= lit("0"))
    .show(100, truncate = false)
}
