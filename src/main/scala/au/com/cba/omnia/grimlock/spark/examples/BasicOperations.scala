// Copyright 2014,2015,2016 Commonwealth Bank of Australia
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package au.com.cba.omnia.grimlock.spark.examples

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.position._

import au.com.cba.omnia.grimlock.spark.environment._
import au.com.cba.omnia.grimlock.spark.Matrix._
import au.com.cba.omnia.grimlock.spark.position.Positions._
import au.com.cba.omnia.grimlock.spark.position.PositionDistributable._
import au.com.cba.omnia.grimlock.spark.Types._

import org.apache.spark.{ SparkConf, SparkContext }

object BasicOperations {

  def main(args: Array[String]) {
    // Define implicit context.
    implicit val ctx = Context(new SparkContext(args(0), "Grimlock Spark Demo", new SparkConf()))

    // Path to data files, output folder
    val path = if (args.length > 1) args(1) else "../../data"
    val output = "spark"

    // Read the data (ignoring errors). This returns a 2D matrix (instance x feature).
    val (data, _) = loadText(s"${path}/exampleInput.txt", Cell.parse2D())

    // Get the number of rows.
    data
      .size(First)
      .saveAsText(s"./demo.${output}/row_size.out")
      .toUnit

    // Get all dimensions of the matrix.
    data
      .shape()
      .saveAsText(s"./demo.${output}/matrix_shape.out")
      .toUnit

    // Get the column names.
    data
      .names(Over[Position1D, Position2D](Second))
      .saveAsText(s"./demo.${output}/column_names.out")
      .toUnit

    // Get the type of variables of each column.
    data
      .types(Over[Position1D, Position2D](Second), true)
      .saveAsText(s"./demo.${output}/column_types.txt")
      .toUnit

    // Transpose the matrix.
    data
      .permute(Second, First)
      .saveAsText(s"./demo.${output}/transposed.out")
      .toUnit

    // Construct a simple query
    def simpleQuery(cell: Cell[Position2D]) = (cell.content.value gtr 995) || (cell.content.value equ "F")

    // Find all co-ordinates that match the above simple query.
    val coords = data
      .which(simpleQuery)
      .saveAsText(s"./demo.${output}/query.txt")

    // Get the data for the above coordinates.
    data
      .get(coords)
      .saveAsText(s"./demo.${output}/values.txt")
      .toUnit

    // Keep columns A and B, and remove row 0221707
    data
      .slice(Over[Position1D, Position2D](Second), List("fid:A", "fid:B"), true)
      .slice(Over[Position1D, Position2D](First), "iid:0221707", false)
      .saveAsText(s"./demo.${output}/sliced.txt")
      .toUnit
  }
}

