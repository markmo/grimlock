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

package au.com.cba.omnia.grimlock.library.partition

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.encoding._
import au.com.cba.omnia.grimlock.framework.partition._
import au.com.cba.omnia.grimlock.framework.position._

import java.util.Date

/**
 * Binary partition based on the hash code of a coordinate.
 *
 * @param dim   The dimension to partition on.
 * @param ratio The binary split ratio (relative to `base`).
 * @param left  The identifier for the left partition.
 * @param right The identifier for the right partition.
 * @param base  The base for the ratio.
 *
 * @note The hash code modulo `base` is used for comparison with the ratio. While the position is assigned to the left
 *       partition if it is less or equal to the `ratio` value.
 */
case class BinaryHashSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  ratio: Int,
  left: S,
  right: S,
  base: Int = 100
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    Some(if (math.abs(cell.position(dim).hashCode % base) <= ratio) left else right)
  }
}

/**
 * Ternary partition based on the hash code of a coordinate.
 *
 * @param dim    The dimension to partition on.
 * @param lower  The lower ternary split ratio (relative to `base`).
 * @param upper  The upper ternary split ratio (relative to `base`).
 * @param left   The identifier for the left partition.
 * @param middle The identifier for the middle partition.
 * @param right  The identifier for the right partition.
 * @param base   The base for the ratio.
 *
 * @note The hash code modulo `base` is used for comparison with lower/upper. While the position is assigned to the
 *       partition `left` if it is less or equal to `lower`, `middle` if it is less of equal to `upper` or else to
 *       `right`.
 */
case class TernaryHashSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  lower: Int,
  upper: Int,
  left: S,
  middle: S,
  right: S,
  base: Int = 100
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    val hash = math.abs(cell.position(dim).hashCode % base)

    Some(if (hash <= lower) left else if (hash <= upper) middle else right)
  }
}

/**
 * Partition based on the hash code of a coordinate.
 *
 * @param dim    The dimension to partition on.
 * @param ranges A `Map` holding the partitions and hash code ranges (relative to `base`) for each partition.
 * @param base   The base for hash code.
 *
 * @note The hash code modulo `base` is used for comparison with the range. While a position falls in a range if it is
 *       (strictly) greater than the lower value (first value in tuple) and less or equal to the upper value (second
 *       value in tuple).
 */
case class HashSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  ranges: Map[S, (Int, Int)],
  base: Int = 100
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    val hash = math.abs(cell.position(dim).hashCode % base)
    ranges
      .flatMap {
        case (k, (l, u)) if (hash > l && hash <= u) => Some(k)
        case _ => None
      }
  }
}

/**
 * Binary partition based on the date of a coordinate.
 *
 * @param dim   The dimension to partition on.
 * @param date  The date around which to split.
 * @param left  The identifier for the left partition.
 * @param right The identifier for the right partition.
 * @param codec The date codec used for comparison.
 *
 * @note The position is assigned to the `left` partition if it is less or equal to the `date` value, to `right`
 *       otherwise.
 */
case class BinaryDateSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  date: Date,
  left: S,
  right: S,
  codec: DateCodec
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    codec.compare(cell.position(dim), DateValue(date, codec)).map { case cmp => if (cmp <= 0) left else right }
  }
}

/**
 * Ternary partition based on the date of a coordinate.
 *
 * @param dim    The dimension to partition on.
 * @param lower  The lower date around which to split.
 * @param upper  The upper date around which to split.
 * @param left   The identifier for the left partition.
 * @param middle The identifier for the middle partition.
 * @param right  The identifier for the right partition.
 * @param codec  The date codec used for comparison.
 *
 * @note The position is assigned to the partition `left` if it is less or equal to `lower`, `middle` if it is less or
 *       equal to `upper` or else to `right`.
 */
case class TernaryDateSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  lower: Date,
  upper: Date,
  left: S,
  middle: S,
  right: S,
  codec: DateCodec
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    (codec.compare(cell.position(dim), DateValue(lower, codec)),
      codec.compare(cell.position(dim), DateValue(upper, codec))) match {
        case (Some(l), Some(u)) => Some(if (l <= 0) left else if (u <= 0) middle else right)
        case _ => None
      }
  }
}

/**
 * Partition based on the date of a coordinate.
 *
 * @param dim    The dimension to partition on.
 * @param ranges A `Map` holding the partitions and date ranges for each partition.
 * @param codec  The date codec used for comparison.
 *
 * @note A position falls in a range if it is (strictly) greater than the lower value (first value in tuple) and less
 *       or equal to the upper value (second value in tuple).
 */
case class DateSplit[
  P <: Position[P],
  S
](
  dim: Dimension,
  ranges: Map[S, (Date, Date)],
  codec: DateCodec
) extends Partitioner[P, S] {
  def assign(cell: Cell[P]): TraversableOnce[S] = {
    ranges
      .flatMap {
        case (k, (lower, upper)) =>
          (codec.compare(cell.position(dim), DateValue(lower, codec)),
            codec.compare(cell.position(dim), DateValue(upper, codec))) match {
              case (Some(l), Some(u)) if (l > 0 && u <= 0) => Some(k)
              case _ => None
            }
      }
  }
}

