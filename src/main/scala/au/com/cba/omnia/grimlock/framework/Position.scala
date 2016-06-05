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

package au.com.cba.omnia.grimlock.framework.position

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.encoding._

import scala.reflect.ClassTag
import scala.util.matching.Regex

import shapeless.=:!=

/** Base trait for dealing with positions. */
trait Position[P <: Position[P]] {
  /** List of coordinates of the position. */
  val coordinates: List[Value]

  /**
   * Return the coordinate at dimension (index) `dim`.
   *
   * @param dim Dimension of the coordinate to get.
   */
  def apply(dim: Dimension): Value = coordinates(getIndex(dim))

  /**
   * Update the coordinate at `dim` with `value`.
   *
   * @param dim   The dimension to set.
   * @param value The coordinate to set.
   *
   * @return A position of the same size as `this` but with `value` set at index `dim`.
   */
  def update(dim: Dimension, value: Valueable): P = same(coordinates.updated(getIndex(dim), value()))

  /**
   * Converts the position to a consise (terse) string.
   *
   * @param separator The separator to use between the coordinates.
   *
   * @return Short string representation.
   */
  def toShortString(separator: String): String = coordinates.map(_.toShortString).mkString(separator)

  override def toString = "Position" + coordinates.length + "D(" + coordinates.map(_.toString).mkString(",") + ")"

  /** Return this position as an option. */
  def toOption(): Option[this.type] = Some(this)

  /**
   * Compare this object with another position.
   *
   * @param that Position to compare against.
   *
   * @return x < 0 iff this < that, x = 0 iff this = that, x > 0 iff this > that.
   *
   * @note If the comparison is between two positions with different dimensions, then a comparison on the number of
   *       dimensions is performed.
   */
  def compare(that: Position[_]): Int = {
    (coordinates.length == that.coordinates.length) match {
      case true =>
        val cmp = coordinates.zip(that.coordinates).map { case (m, t) => Value.Ordering.compare(m, t) }

        cmp.indexWhere(_ != 0) match {
          case idx if (idx < 0) => 0
          case idx => cmp(idx)
        }
      case false => coordinates.length.compare(that.coordinates.length)
    }
  }

  protected def same(cl: List[Value]): P

  protected def getIndex(dim: Dimension): Int = if (dim.index < 0) coordinates.length - 1 else dim.index
}

/** Trait for capturing the dependency between a position and its expansion. */
trait PosExpDep[P <: Position[P], Q <: Position[Q]] extends java.io.Serializable

/** Trait for capturing the dependency between a position and its increment (which may be the same). */
trait PosIncDep[P <: Position[P], Q <: Position[Q]] extends java.io.Serializable

object Position {
  /** Define an ordering between 2 position. Only use with position of the same type coordinates. */
  def Ordering[P <: Position[P]](ascending: Boolean = true): Ordering[P] = {
    new Ordering[P] { def compare(x: P, y: P): Int = x.compare(y) * (if (ascending) { 1 } else { -1 }) }
  }

  /**
   * Return function that returns a string representation of a position.
   *
   * @param descriptive Indicator if descriptive string is required or not.
   * @param separator   The separator to use between various fields (only used if descriptive is `false`).
   */
  def toString[P <: Position[P]](descriptive: Boolean = false,
    separator: String = "|"): (P) => TraversableOnce[String] = {
    (t: P) => if (descriptive) { Some(t.toString) } else { Some(t.toShortString(separator)) }
  }

  /** Define dependency between expansion from `Position0D` to `Position1D`. */
  implicit object P0EP1 extends PosExpDep[Position0D, Position1D]
  /** Define dependency between expansion from `Position0D` to `Position2D`. */
  implicit object P0EP2 extends PosExpDep[Position0D, Position2D]
  /** Define dependency between expansion from `Position0D` to `Position3D`. */
  implicit object P0EP3 extends PosExpDep[Position0D, Position3D]
  /** Define dependency between expansion from `Position0D` to `Position4D`. */
  implicit object P0EP4 extends PosExpDep[Position0D, Position4D]
  /** Define dependency between expansion from `Position0D` to `Position5D`. */
  implicit object P0EP5 extends PosExpDep[Position0D, Position5D]
  /** Define dependency between expansion from `Position0D` to `Position6D`. */
  implicit object P0EP6 extends PosExpDep[Position0D, Position6D]
  /** Define dependency between expansion from `Position0D` to `Position7D`. */
  implicit object P0EP7 extends PosExpDep[Position0D, Position7D]
  /** Define dependency between expansion from `Position0D` to `Position8D`. */
  implicit object P0EP8 extends PosExpDep[Position0D, Position8D]
  /** Define dependency between expansion from `Position0D` to `Position9D`. */
  implicit object P0EP9 extends PosExpDep[Position0D, Position9D]

  /** Define dependency between expansion from `Position1D` to `Position2D`. */
  implicit object P1EP2 extends PosExpDep[Position1D, Position2D]
  /** Define dependency between expansion from `Position1D` to `Position3D`. */
  implicit object P1EP3 extends PosExpDep[Position1D, Position3D]
  /** Define dependency between expansion from `Position1D` to `Position4D`. */
  implicit object P1EP4 extends PosExpDep[Position1D, Position4D]
  /** Define dependency between expansion from `Position1D` to `Position5D`. */
  implicit object P1EP5 extends PosExpDep[Position1D, Position5D]
  /** Define dependency between expansion from `Position1D` to `Position6D`. */
  implicit object P1EP6 extends PosExpDep[Position1D, Position6D]
  /** Define dependency between expansion from `Position1D` to `Position7D`. */
  implicit object P1EP7 extends PosExpDep[Position1D, Position7D]
  /** Define dependency between expansion from `Position1D` to `Position8D`. */
  implicit object P1EP8 extends PosExpDep[Position1D, Position8D]
  /** Define dependency between expansion from `Position1D` to `Position9D`. */
  implicit object P1EP9 extends PosExpDep[Position1D, Position9D]

  /** Define dependency between expansion from `Position2D` to `Position3D`. */
  implicit object P2EP3 extends PosExpDep[Position2D, Position3D]
  /** Define dependency between expansion from `Position2D` to `Position4D`. */
  implicit object P2EP4 extends PosExpDep[Position2D, Position4D]
  /** Define dependency between expansion from `Position2D` to `Position5D`. */
  implicit object P2EP5 extends PosExpDep[Position2D, Position5D]
  /** Define dependency between expansion from `Position2D` to `Position6D`. */
  implicit object P2EP6 extends PosExpDep[Position2D, Position6D]
  /** Define dependency between expansion from `Position2D` to `Position7D`. */
  implicit object P2EP7 extends PosExpDep[Position2D, Position7D]
  /** Define dependency between expansion from `Position2D` to `Position8D`. */
  implicit object P2EP8 extends PosExpDep[Position2D, Position8D]
  /** Define dependency between expansion from `Position2D` to `Position9D`. */
  implicit object P2EP9 extends PosExpDep[Position2D, Position9D]

  /** Define dependency between expansion from `Position3D` to `Position4D`. */
  implicit object P3EP4 extends PosExpDep[Position3D, Position4D]
  /** Define dependency between expansion from `Position3D` to `Position5D`. */
  implicit object P3EP5 extends PosExpDep[Position3D, Position5D]
  /** Define dependency between expansion from `Position3D` to `Position6D`. */
  implicit object P3EP6 extends PosExpDep[Position3D, Position6D]
  /** Define dependency between expansion from `Position3D` to `Position7D`. */
  implicit object P3EP7 extends PosExpDep[Position3D, Position7D]
  /** Define dependency between expansion from `Position3D` to `Position8D`. */
  implicit object P3EP8 extends PosExpDep[Position3D, Position8D]
  /** Define dependency between expansion from `Position3D` to `Position9D`. */
  implicit object P3EP9 extends PosExpDep[Position3D, Position9D]

  /** Define dependency between expansion from `Position4D` to `Position5D`. */
  implicit object P4EP5 extends PosExpDep[Position4D, Position5D]
  /** Define dependency between expansion from `Position4D` to `Position6D`. */
  implicit object P4EP6 extends PosExpDep[Position4D, Position6D]
  /** Define dependency between expansion from `Position4D` to `Position7D`. */
  implicit object P4EP7 extends PosExpDep[Position4D, Position7D]
  /** Define dependency between expansion from `Position4D` to `Position8D`. */
  implicit object P4EP8 extends PosExpDep[Position4D, Position8D]
  /** Define dependency between expansion from `Position4D` to `Position9D`. */
  implicit object P4EP9 extends PosExpDep[Position4D, Position9D]

  /** Define dependency between expansion from `Position5D` to `Position6D`. */
  implicit object P5EP6 extends PosExpDep[Position5D, Position6D]
  /** Define dependency between expansion from `Position5D` to `Position7D`. */
  implicit object P5EP7 extends PosExpDep[Position5D, Position7D]
  /** Define dependency between expansion from `Position5D` to `Position8D`. */
  implicit object P5EP8 extends PosExpDep[Position5D, Position8D]
  /** Define dependency between expansion from `Position5D` to `Position9D`. */
  implicit object P5EP9 extends PosExpDep[Position5D, Position9D]

  /** Define dependency between expansion from `Position6D` to `Position7D`. */
  implicit object P6EP7 extends PosExpDep[Position6D, Position7D]
  /** Define dependency between expansion from `Position6D` to `Position8D`. */
  implicit object P6EP8 extends PosExpDep[Position6D, Position8D]
  /** Define dependency between expansion from `Position6D` to `Position9D`. */
  implicit object P6EP9 extends PosExpDep[Position6D, Position9D]

  /** Define dependency between expansion from `Position7D` to `Position8D`. */
  implicit object P7EP8 extends PosExpDep[Position7D, Position8D]
  /** Define dependency between expansion from `Position7D` to `Position9D`. */
  implicit object P7EP9 extends PosExpDep[Position7D, Position9D]

  /** Define dependency between expansion from `Position8D` to `Position9D`. */
  implicit object P8EP9 extends PosExpDep[Position8D, Position9D]

  /** Define dependency between an expandable position and its expansion. */
  implicit def PEPM[
    P <: Position[P] with ExpandablePosition[P, M],
    M <: Position[M] with ReduceablePosition[M, P]
  ] = new PosExpDep[P, M] {}

  /** Define dependency between an increase from `Position0D` to `Position1D`. */
  implicit object P0IP1 extends PosIncDep[Position0D, Position1D]
  /** Define dependency between an increase from `Position0D` to `Position2D`. */
  implicit object P0IP2 extends PosIncDep[Position0D, Position2D]
  /** Define dependency between an increase from `Position0D` to `Position3D`. */
  implicit object P0IP3 extends PosIncDep[Position0D, Position3D]
  /** Define dependency between an increase from `Position0D` to `Position4D`. */
  implicit object P0IP4 extends PosIncDep[Position0D, Position4D]
  /** Define dependency between an increase from `Position0D` to `Position5D`. */
  implicit object P0IP5 extends PosIncDep[Position0D, Position5D]
  /** Define dependency between an increase from `Position0D` to `Position6D`. */
  implicit object P0IP6 extends PosIncDep[Position0D, Position6D]
  /** Define dependency between an increase from `Position0D` to `Position7D`. */
  implicit object P0IP7 extends PosIncDep[Position0D, Position7D]
  /** Define dependency between an increase from `Position0D` to `Position8D`. */
  implicit object P0IP8 extends PosIncDep[Position0D, Position8D]
  /** Define dependency between an increase from `Position0D` to `Position9D`. */
  implicit object P0IP9 extends PosIncDep[Position0D, Position9D]

  /** Define dependency between an increase from `Position1D` to `Position1D`. */
  implicit object P1IP1 extends PosIncDep[Position1D, Position1D]
  /** Define dependency between an increase from `Position1D` to `Position2D`. */
  implicit object P1IP2 extends PosIncDep[Position1D, Position2D]
  /** Define dependency between an increase from `Position1D` to `Position3D`. */
  implicit object P1IP3 extends PosIncDep[Position1D, Position3D]
  /** Define dependency between an increase from `Position1D` to `Position4D`. */
  implicit object P1IP4 extends PosIncDep[Position1D, Position4D]
  /** Define dependency between an increase from `Position1D` to `Position5D`. */
  implicit object P1IP5 extends PosIncDep[Position1D, Position5D]
  /** Define dependency between an increase from `Position1D` to `Position6D`. */
  implicit object P1IP6 extends PosIncDep[Position1D, Position6D]
  /** Define dependency between an increase from `Position1D` to `Position7D`. */
  implicit object P1IP7 extends PosIncDep[Position1D, Position7D]
  /** Define dependency between an increase from `Position1D` to `Position8D`. */
  implicit object P1IP8 extends PosIncDep[Position1D, Position8D]
  /** Define dependency between an increase from `Position1D` to `Position9D`. */
  implicit object P1IP9 extends PosIncDep[Position1D, Position9D]

  /** Define dependency between an increase from `Position2D` to `Position2D`. */
  implicit object P2IP2 extends PosIncDep[Position2D, Position2D]
  /** Define dependency between an increase from `Position2D` to `Position3D`. */
  implicit object P2IP3 extends PosIncDep[Position2D, Position3D]
  /** Define dependency between an increase from `Position2D` to `Position4D`. */
  implicit object P2IP4 extends PosIncDep[Position2D, Position4D]
  /** Define dependency between an increase from `Position2D` to `Position5D`. */
  implicit object P2IP5 extends PosIncDep[Position2D, Position5D]
  /** Define dependency between an increase from `Position2D` to `Position6D`. */
  implicit object P2IP6 extends PosIncDep[Position2D, Position6D]
  /** Define dependency between an increase from `Position2D` to `Position7D`. */
  implicit object P2IP7 extends PosIncDep[Position2D, Position7D]
  /** Define dependency between an increase from `Position2D` to `Position8D`. */
  implicit object P2IP8 extends PosIncDep[Position2D, Position8D]
  /** Define dependency between an increase from `Position2D` to `Position9D`. */
  implicit object P2IP9 extends PosIncDep[Position2D, Position9D]

  /** Define dependency between an increase from `Position3D` to `Position3D`. */
  implicit object P3IP3 extends PosIncDep[Position3D, Position3D]
  /** Define dependency between an increase from `Position3D` to `Position4D`. */
  implicit object P3IP4 extends PosIncDep[Position3D, Position4D]
  /** Define dependency between an increase from `Position3D` to `Position5D`. */
  implicit object P3IP5 extends PosIncDep[Position3D, Position5D]
  /** Define dependency between an increase from `Position3D` to `Position6D`. */
  implicit object P3IP6 extends PosIncDep[Position3D, Position6D]
  /** Define dependency between an increase from `Position3D` to `Position7D`. */
  implicit object P3IP7 extends PosIncDep[Position3D, Position7D]
  /** Define dependency between an increase from `Position3D` to `Position8D`. */
  implicit object P3IP8 extends PosIncDep[Position3D, Position8D]
  /** Define dependency between an increase from `Position3D` to `Position9D`. */
  implicit object P3IP9 extends PosIncDep[Position3D, Position9D]

  /** Define dependency between an increase from `Position4D` to `Position4D`. */
  implicit object P4IP4 extends PosIncDep[Position4D, Position4D]
  /** Define dependency between an increase from `Position4D` to `Position5D`. */
  implicit object P4IP5 extends PosIncDep[Position4D, Position5D]
  /** Define dependency between an increase from `Position4D` to `Position6D`. */
  implicit object P4IP6 extends PosIncDep[Position4D, Position6D]
  /** Define dependency between an increase from `Position4D` to `Position7D`. */
  implicit object P4IP7 extends PosIncDep[Position4D, Position7D]
  /** Define dependency between an increase from `Position4D` to `Position8D`. */
  implicit object P4IP8 extends PosIncDep[Position4D, Position8D]
  /** Define dependency between an increase from `Position4D` to `Position9D`. */
  implicit object P4IP9 extends PosIncDep[Position4D, Position9D]

  /** Define dependency between an increase from `Position5D` to `Position5D`. */
  implicit object P5IP5 extends PosIncDep[Position5D, Position5D]
  /** Define dependency between an increase from `Position5D` to `Position6D`. */
  implicit object P5IP6 extends PosIncDep[Position5D, Position6D]
  /** Define dependency between an increase from `Position5D` to `Position7D`. */
  implicit object P5IP7 extends PosIncDep[Position5D, Position7D]
  /** Define dependency between an increase from `Position5D` to `Position8D`. */
  implicit object P5IP8 extends PosIncDep[Position5D, Position8D]
  /** Define dependency between an increase from `Position5D` to `Position9D`. */
  implicit object P5IP9 extends PosIncDep[Position5D, Position9D]

  /** Define dependency between an increase from `Position6D` to `Position6D`. */
  implicit object P6IP6 extends PosIncDep[Position6D, Position6D]
  /** Define dependency between an increase from `Position6D` to `Position7D`. */
  implicit object P6IP7 extends PosIncDep[Position6D, Position7D]
  /** Define dependency between an increase from `Position6D` to `Position8D`. */
  implicit object P6IP8 extends PosIncDep[Position6D, Position8D]
  /** Define dependency between an increase from `Position6D` to `Position9D`. */
  implicit object P6IP9 extends PosIncDep[Position6D, Position9D]

  /** Define dependency between an increase from `Position7D` to `Position7D`. */
  implicit object P7IP7 extends PosIncDep[Position7D, Position7D]
  /** Define dependency between an increase from `Position7D` to `Position8D`. */
  implicit object P7IP8 extends PosIncDep[Position7D, Position8D]
  /** Define dependency between an increase from `Position7D` to `Position9D`. */
  implicit object P7IP9 extends PosIncDep[Position7D, Position9D]

  /** Define dependency between an increase from `Position8D` to `Position8D`. */
  implicit object P8IP8 extends PosIncDep[Position8D, Position8D]
  /** Define dependency between an increase from `Position8D` to `Position9D`. */
  implicit object P8IP9 extends PosIncDep[Position8D, Position9D]

  /** Define dependency between an increase from `Position9D` to `Position9D`. */
  implicit object P9IP9 extends PosIncDep[Position9D, Position9D]

  /** Define dependency between an increase from `P` to `P`. */
  implicit def PIP[P <: Position[P]] = new PosIncDep[P, P] {}

  /** Define dependency between an increase from `P` to `M`. */
  implicit def PIPM[
    P <: Position[P] with ExpandablePosition[P, M],
    M <: Position[M] with ReduceablePosition[M, P]] = new PosIncDep[P, M] {}
}

/** Trait for compacting position and content. */
trait CompactablePosition[P <: Position[P]] { self: Position[P] =>
  /** Type of the compacted position and content. */
  type C[_]

  /**
   * Compact position and content to the value in a `Map`.
   *
   * @param rem Cell's remainder value to compact.
   * @param con Cell's content to compact.
   *
   * @return The compacted position and content.
   */
  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R]
}

/** Trait for operations that expand a position by one dimension. */
trait ExpandablePosition[
  P <: Position[P] with ExpandablePosition[P, M],
  M <: Position[M] with ReduceablePosition[M, P]] { self: Position[P] =>
  /**
   * Prepend a coordinate to the position.
   *
   * @param value The coordinate to prepend.
   *
   * @return A new position with the coordinate `value` prepended.
   */
  def prepend(value: Valueable): M = more(value() +: coordinates)

  /**
   * Append a coordinate to the position.
   *
   * @param value The coordinate to append.
   *
   * @return A new position with the coordinate `value` appended.
   */
  def append(value: Valueable): M = more(coordinates :+ value())

  protected def more(cl: List[Value]): M
}

/** Trait for operations that modify a position (but keep the number of dimensions the same). */
trait PermutablePosition[P <: Position[P]] { self: Position[P] =>
  /**
   * Permute the order of coordinates.
   *
   * @param order The new ordering of the coordinates.
   *
   * @return A position of the same size as `this` but with the coordinates ordered according to `order`.
   *
   * @note The ordering must contain each dimension exactly once.
   */
  def permute(order: List[Dimension]): P = same(order.map { case d => coordinates(getIndex(d)) })
}

/** Trait for operations that reduce a position by one dimension. */
trait ReduceablePosition[
  P <: Position[P] with ReduceablePosition[P, L],
  L <: Position[L] with ExpandablePosition[L, P]] { self: Position[P] =>
  /**
   * Remove the coordinate at dimension `dim`.
   *
   * @param dim The dimension to remove.
   *
   * @return A new position with dimension `dim` removed.
   */
  def remove(dim: Dimension): L = {
    val (h, t) = coordinates.splitAt(getIndex(dim))

    less(h ++ t.tail)
  }

  /**
   * Melt dimension `dim` into `into`.
   *
   * @param dim   The dimension to remove.
   * @param into  The dimension into which to melt.
   * @param merge The function to use for merging coordinates
   *
   * @return A new position with dimension `dim` removed. The coordinate at `unto` will be a string value consisting of
   *         the string representations of the coordinates `dim` and `unto` separated by `separator`.
   *
   * @note `dim` and `into` must not be the same.
   */
  def melt(dim: Dimension, into: Dimension, merge: (Value, Value) => Valueable): L = {
    val iidx = getIndex(into)
    val didx = getIndex(dim)

    less(coordinates
      .updated(iidx, merge(coordinates(iidx), coordinates(didx))())
      .zipWithIndex
      .filter(_._2 != didx)
      .map(_._1))
  }

  protected def less(cl: List[Value]): L
}

/**
 * Position for zero dimensions.
 *
 * @note Position0D exists so things like `names(Over(First))` work.
 */
case class Position0D() extends Position[Position0D] with ExpandablePosition[Position0D, Position1D] {
  val coordinates = List()

  protected def same(cl: List[Value]): Position0D = Position0D()
  protected def more(cl: List[Value]): Position1D = Position1DImpl(cl)
}

/** Position for 1 dimensional data. */
trait Position1D extends Position[Position1D]
  with CompactablePosition[Position1D]
  with ExpandablePosition[Position1D, Position2D]
  with ReduceablePosition[Position1D, Position0D] {
  type C[_] = Content

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = con

  protected def less(cl: List[Value]): Position0D = Position0D()
  protected def same(cl: List[Value]): Position1D = Position1DImpl(cl)
  protected def more(cl: List[Value]): Position2D = Position2DImpl(cl)
}

/** Companion object to `Position1D`. */
object Position1D {
  /**
   * Construct a `Position1D` from a coordinate.
   *
   * @param first Coordinate for the first dimension.
   */
  def apply(first: Valueable): Position1D = Position1DImpl(List(first()))

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(pos: Position1D): Option[Value] = Some(pos(First))
}

private case class Position1DImpl(coordinates: List[Value]) extends Position1D

/** Position for 2 dimensional data. */
trait Position2D extends Position[Position2D]
  with CompactablePosition[Position2D]
  with ExpandablePosition[Position2D, Position3D]
  with PermutablePosition[Position2D]
  with ReduceablePosition[Position2D, Position1D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position1D = Position1DImpl(cl)
  protected def same(cl: List[Value]): Position2D = Position2DImpl(cl)
  protected def more(cl: List[Value]): Position3D = Position3DImpl(cl)
}

/** Companion object to `Position2D`. */
object Position2D {
  /**
   * Construct a `Position2D` from two coordinates.
   *
   * @param first  Coordinate for the first dimension.
   * @param second Coordinate for the second dimension.
   */
  def apply(first: Valueable, second: Valueable): Position2D = Position2DImpl(List(first(), second()))

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position2D): Option[(Value, Value)] = Some((p(First), p(Second)))
}

private case class Position2DImpl(coordinates: List[Value]) extends Position2D

/** Position for 3 dimensional data. */
trait Position3D extends Position[Position3D]
  with CompactablePosition[Position3D]
  with ExpandablePosition[Position3D, Position4D]
  with PermutablePosition[Position3D]
  with ReduceablePosition[Position3D, Position2D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position2D = Position2DImpl(cl)
  protected def same(cl: List[Value]): Position3D = Position3DImpl(cl)
  protected def more(cl: List[Value]): Position4D = Position4DImpl(cl)
}

/** Companion object to `Position3D`. */
object Position3D {
  /**
   * Construct a `Position3D` from three coordinates.
   *
   * @param first  Coordinate for the first dimension.
   * @param second Coordinate for the second dimension.
   * @param third  Coordinate for the third dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable): Position3D = {
    Position3DImpl(List(first(), second(), third()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position3D): Option[(Value, Value, Value)] = Some((p(First), p(Second), p(Third)))
}

private case class Position3DImpl(coordinates: List[Value]) extends Position3D

/** Position for 4 dimensional data. */
trait Position4D extends Position[Position4D]
  with CompactablePosition[Position4D]
  with ExpandablePosition[Position4D, Position5D]
  with PermutablePosition[Position4D]
  with ReduceablePosition[Position4D, Position3D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position3D = Position3DImpl(cl)
  protected def same(cl: List[Value]): Position4D = Position4DImpl(cl)
  protected def more(cl: List[Value]): Position5D = Position5DImpl(cl)
}

/** Companion object to `Position4D`. */
object Position4D {
  /**
   * Construct a `Position4D` from four coordinates.
   *
   * @param first  Coordinate for the first dimension.
   * @param second Coordinate for the second dimension.
   * @param third  Coordinate for the third dimension.
   * @param fourth Coordinate for the fourth dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable): Position4D = {
    Position4DImpl(List(first(), second(), third(), fourth()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position4D): Option[(Value, Value, Value, Value)] = Some((p(First), p(Second), p(Third), p(Fourth)))
}

private case class Position4DImpl(coordinates: List[Value]) extends Position4D

/** Position for 5 dimensional data. */
trait Position5D extends Position[Position5D]
  with CompactablePosition[Position5D]
  with ExpandablePosition[Position5D, Position6D]
  with PermutablePosition[Position5D]
  with ReduceablePosition[Position5D, Position4D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position4D = Position4DImpl(cl)
  protected def same(cl: List[Value]): Position5D = Position5DImpl(cl)
  protected def more(cl: List[Value]): Position6D = Position6DImpl(cl)
}

/** Companion object to `Position5D`. */
object Position5D {
  /**
   * Construct a `Position5D` from five coordinates.
   *
   * @param first  Coordinate for the first dimension.
   * @param second Coordinate for the second dimension.
   * @param third  Coordinate for the third dimension.
   * @param fourth Coordinate for the fourth dimension.
   * @param fifth  Coordinate for the fifth dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable, fifth: Valueable): Position5D = {
    Position5DImpl(List(first(), second(), third(), fourth(), fifth()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position5D): Option[(Value, Value, Value, Value, Value)] = {
    Some((p(First), p(Second), p(Third), p(Fourth), p(Fifth)))
  }
}

private case class Position5DImpl(coordinates: List[Value]) extends Position5D

/** Position for 6 dimensional data. */
trait Position6D extends Position[Position6D]
  with CompactablePosition[Position6D]
  with ExpandablePosition[Position6D, Position7D]
  with PermutablePosition[Position6D]
  with ReduceablePosition[Position6D, Position5D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position5D = Position5DImpl(cl)
  protected def same(cl: List[Value]): Position6D = Position6DImpl(cl)
  protected def more(cl: List[Value]): Position7D = Position7DImpl(cl)
}

/** Companion object to `Position6D`. */
object Position6D {
  /**
   * Construct a `Position6D` from six coordinates.
   *
   * @param first  Coordinate for the first dimension.
   * @param second Coordinate for the second dimension.
   * @param third  Coordinate for the third dimension.
   * @param fourth Coordinate for the fourth dimension.
   * @param fifth  Coordinate for the fifth dimension.
   * @param sixth  Coordinate for the sixth dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable, fifth: Valueable,
    sixth: Valueable): Position6D = Position6DImpl(List(first(), second(), third(), fourth(), fifth(), sixth()))

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position6D): Option[(Value, Value, Value, Value, Value, Value)] = {
    Some((p(First), p(Second), p(Third), p(Fourth), p(Fifth), p(Sixth)))
  }
}

private case class Position6DImpl(coordinates: List[Value]) extends Position6D

/** Position for 7 dimensional data. */
trait Position7D extends Position[Position7D]
  with CompactablePosition[Position7D]
  with ExpandablePosition[Position7D, Position8D]
  with PermutablePosition[Position7D]
  with ReduceablePosition[Position7D, Position6D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position6D = Position6DImpl(cl)
  protected def same(cl: List[Value]): Position7D = Position7DImpl(cl)
  protected def more(cl: List[Value]): Position8D = Position8DImpl(cl)
}

/** Companion object to `Position7D`. */
object Position7D {
  /**
   * Construct a `Position7D` from seven coordinates.
   *
   * @param first   Coordinate for the first dimension.
   * @param second  Coordinate for the second dimension.
   * @param third   Coordinate for the third dimension.
   * @param fourth  Coordinate for the fourth dimension.
   * @param fifth   Coordinate for the fifth dimension.
   * @param sixth   Coordinate for the sixth dimension.
   * @param seventh Coordinate for the seventh dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable, fifth: Valueable,
    sixth: Valueable, seventh: Valueable): Position7D = {
    Position7DImpl(List(first(), second(), third(), fourth(), fifth(), sixth(), seventh()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position7D): Option[(Value, Value, Value, Value, Value, Value, Value)] = {
    Some((p(First), p(Second), p(Third), p(Fourth), p(Fifth), p(Sixth), p(Seventh)))
  }
}

private case class Position7DImpl(coordinates: List[Value]) extends Position7D

/** Position for 8 dimensional data. */
trait Position8D extends Position[Position8D]
  with CompactablePosition[Position8D]
  with ExpandablePosition[Position8D, Position9D]
  with PermutablePosition[Position8D]
  with ReduceablePosition[Position8D, Position7D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position7D = Position7DImpl(cl)
  protected def same(cl: List[Value]): Position8D = Position8DImpl(cl)
  protected def more(cl: List[Value]): Position9D = Position9DImpl(cl)
}

/** Companion object to `Position8D`. */
object Position8D {
  /**
   * Construct a `Position8D` from eight coordinates.
   *
   * @param first   Coordinate for the first dimension.
   * @param second  Coordinate for the second dimension.
   * @param third   Coordinate for the third dimension.
   * @param fourth  Coordinate for the fourth dimension.
   * @param fifth   Coordinate for the fifth dimension.
   * @param sixth   Coordinate for the sixth dimension.
   * @param seventh Coordinate for the seventh dimension.
   * @param eighth  Coordinate for the eighth dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable, fifth: Valueable,
    sixth: Valueable, seventh: Valueable, eighth: Valueable): Position8D = {
    Position8DImpl(List(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position8D): Option[(Value, Value, Value, Value, Value, Value, Value, Value)] = {
    Some((p(First), p(Second), p(Third), p(Fourth), p(Fifth), p(Sixth), p(Seventh), p(Eighth)))
  }
}

private case class Position8DImpl(coordinates: List[Value]) extends Position8D

/** Position for 9 dimensional data.  */
trait Position9D extends Position[Position9D]
  with CompactablePosition[Position9D]
  with PermutablePosition[Position9D]
  with ReduceablePosition[Position9D, Position8D] {
  type C[R] = Map[R, Content]

  def toMapValue[R <: Position[R] with ExpandablePosition[R, _]](rem: R, con: Content): C[R] = Map(rem -> con)

  protected def less(cl: List[Value]): Position8D = Position8DImpl(cl)
  protected def same(cl: List[Value]): Position9D = Position9DImpl(cl)
}

/** Companion object to `Position9D`. */
object Position9D {
  /**
   * Construct a `Position9D` from nine coordinates.
   *
   * @param first   Coordinate for the first dimension.
   * @param second  Coordinate for the second dimension.
   * @param third   Coordinate for the third dimension.
   * @param fourth  Coordinate for the fourth dimension.
   * @param fifth   Coordinate for the fifth dimension.
   * @param sixth   Coordinate for the sixth dimension.
   * @param seventh Coordinate for the seventh dimension.
   * @param eighth  Coordinate for the eighth dimension.
   * @param ninth   Coordinate for the ninth dimension.
   */
  def apply(first: Valueable, second: Valueable, third: Valueable, fourth: Valueable, fifth: Valueable,
    sixth: Valueable, seventh: Valueable, eighth: Valueable, ninth: Valueable): Position9D = {
    Position9DImpl(List(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth(), ninth()))
  }

  /**
   * Unapply method to permit pattern matching on coordinates values.
   *
   * @param p Position to pattern match on.
   */
  def unapply(p: Position9D): Option[(Value, Value, Value, Value, Value, Value, Value, Value, Value)] = {
    Some((p(First), p(Second), p(Third), p(Fourth), p(Fifth), p(Sixth), p(Seventh), p(Eighth), p(Ninth)))
  }
}

private case class Position9DImpl(coordinates: List[Value]) extends Position9D

/** Base trait that represents the positions of a matrix. */
trait Positions[
  L <: Position[L] with ExpandablePosition[L, P],
  P <: Position[P] with ReduceablePosition[P, L]] extends Persist[P] {

  /** Specifies tuners permitted on a call to `names`. */
  type NamesTuners[_]

  /**
   * Returns the distinct position(s) (or names) for a given `slice`.
   *
   * @param slice Encapsulates the dimension(s) for which the names are to be returned.
   * @param tuner The tuner for the job.
   *
   * @return A `U[(S, Long)]` of the distinct position(s) together with a unique index.
   *
   * @note The position(s) are returned with an index so the return value can be used in various `save` methods. The
   *       index itself is unique for each position but no ordering is defined.
   */
  def names[
    S <: Position[S] with ExpandablePosition[S, _],
    R <: Position[R] with ExpandablePosition[R, _],
    T <: Tuner : NamesTuners
  ](
    slice: Slice[L, P, S, R],
    tuner: T
  )(implicit
    ev1: S =:!= Position0D,
    ev2: ClassTag[S]
  ): U[S]

  /**
   * Persist to disk.
   *
   * @param file   Name of the output file.
   * @param writer Writer that converts `P` to string.
   *
   * @return A `U[P]` which is this object's data.
   */
  def saveAsText(file: String, writer: TextWriter = Position.toString())(implicit ctx: C): U[P]

  /**
   * Slice the positions using a regular expression.
   *
   * @param regex     The regular expression to match on.
   * @param keep      Indicator if the matched positions should be kept or removed.
   * @param spearator Separator used to convert each position to string.
   *
   * @return A `U[P]` with only the positions of interest.
   *
   * @note The matching is done by converting each position to its short string reprensentation and then applying the
   *       regular expression.
   */
  def slice(regex: Regex, keep: Boolean, separator: String)(implicit ev: ClassTag[P]): U[P] = {
    slice(keep, p => regex.pattern.matcher(p.toShortString(separator)).matches)
  }

  /**
   * Slice the positions using one or more positions.
   *
   * @param positions The positions to slice on.
   * @param keep      Indicator if the matched positions should be kept or removed.
   *
   * @return A `U[P]` with only the positions of interest.
   */
  def slice(positions: PositionListable[P], keep: Boolean)(implicit ev1: ClassTag[P]): U[P] = {
    slice(keep, p => positions().contains(p))
  }

  protected def slice(keep: Boolean, f: P => Boolean)(implicit ev: ClassTag[P]): U[P]
}

/** Type class for transforming a type `T` into a `Position`. */
trait Positionable[P <: Position[P]] extends java.io.Serializable {
  /** Returns a position for this type `T`. */
  def apply(): P
}

/** Companion object for the `Positionable` trait. */
object Positionable {
  /** Converts a position to a position; that is, it's a pass through. */
  implicit def P2P[T <: Position[T]](t: T): Positionable[T] = new Positionable[T] { def apply(): T = t }

  /** Converts a `Value` to a position. */
  implicit def V2P[T <: Value](t: T): Positionable[Position1D] = {
    new Positionable[Position1D] { def apply(): Position1D = Position1D(t) }
  }

  /** Converts a `Valueable` to a position. */
  implicit def Va2P[T <% Valueable](t: T): Positionable[Position1D] = {
    new Positionable[Position1D] { def apply(): Position1D = Position1D(t()) }
  }
}

/** Type class for transforming a type `T` into a `List[Position]`. */
trait PositionListable[P <: Position[P]] extends java.io.Serializable {
  /** Returns a `List[Position]` for this type `T`. */
  def apply(): List[P]
}

/** Companion object for the `PositionListable` trait. */
object PositionListable {
  /** Converts a `List[Positionable]` to a `List[Position]`. */
  implicit def LP2LP[T <% Positionable[P], P <: Position[P]](t: List[T]): PositionListable[P] = {
    new PositionListable[P] { def apply(): List[P] = t.map { case p => p() } }
  }

  /** Converts a `Positionable` to a `List[Position]`. */
  implicit def P2PL[T <% Positionable[P], P <: Position[P]](t: T): PositionListable[P] = {
    new PositionListable[P] { def apply(): List[P] = List(t()) }
  }
}

/** Type class for transforming a type `T` into a `U[Position]`. */
trait PositionDistributable[T, P <: Position[P], U[_]] {
  /**
   * Returns a `U[Position]` for type `T`.
   *
   * @param t Object that can be converted to a `U[Position]`.
   */
  def convert(t: T): U[P]
}

/** Define implicit ordering. */
private[grimlock] trait PositionOrdering {
  protected implicit def ImplicitPositionOrdering[P <: Position[P]] = Position.Ordering[P]()
}

