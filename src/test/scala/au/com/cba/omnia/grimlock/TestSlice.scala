// Copyright 2015,2016 Commonwealth Bank of Australia
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

package au.com.cba.omnia.grimlock

import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.content.metadata._
import au.com.cba.omnia.grimlock.framework.encoding._
import au.com.cba.omnia.grimlock.framework.position._

trait TestSlice extends TestGrimlock {
  val dfmt = new java.text.SimpleDateFormat("yyyy-MM-dd")
  val con1 = Content(ContinuousSchema[Long](), 1)
  val con2 = Content(ContinuousSchema[Long](), 2)
}

trait TestSlicePosition1D extends TestSlice {
  val pos1 = Position1D(1)
  val pos2 = Position1D(-1)
}

class TestOverPosition1D extends TestSlicePosition1D {

  val over = Over[Position0D, Position1D](First)

  "A Over[Position1D]" should "return a Position1D for the selected dimension" in {
    over.selected(pos1) shouldBe Position1D(pos1(First))
  }

  it should "return a Position0D for the remainder" in {
    over.remainder(pos1) shouldBe pos1.remove(First)
  }
}

class TestAlongPosition1D extends TestSlicePosition1D {

  val along = Along[Position0D, Position1D](First)

  "A Along[Position1D]" should "return a Position0D for the selected dimension" in {
    along.selected(pos1) shouldBe pos1.remove(First)
  }

  it should "return a Position1D for the remainder" in {
    along.remainder(pos1) shouldBe Position1D(pos1(First))
  }
}

trait TestSlicePosition2D extends TestSlice {
  val pos1 = Position2D(2, "a")
  val pos2 = Position2D(-2, "z")
}

class TestOverPosition2D extends TestSlicePosition2D {

  val list = List((Over[Position1D, Position2D](First), First), (Over[Position1D, Position2D](Second), Second))

  "A Over[Position2D]" should "return a Position1D for the selected dimension" in {
    list.foreach { case (o, d) => o.selected(pos1) shouldBe Position1D(pos1(d)) }
  }

  it should "return a Position1D for the remainder" in {
    list.foreach { case (o, d) => o.remainder(pos1) shouldBe pos1.remove(d) }
  }
}

class TestAlongPosition2D extends TestSlicePosition2D {

  val list = List((Along[Position1D, Position2D](First), First), (Along[Position1D, Position2D](Second), Second))

  "A Along[Position2D]" should "return a Position1D for the selected dimension" in {
    list.foreach { case (a, d) => a.selected(pos1) shouldBe pos1.remove(d) }
  }

  it should "return a Position1D for the remainder" in {
    list.foreach { case (a, d) => a.remainder(pos1) shouldBe Position1D(pos1(d)) }
  }
}

trait TestSlicePosition3D extends TestSlice {
  val pos1 = Position3D(3, "b", DateValue(dfmt.parse("2001-01-01"), DateCodec()))
  val pos2 = Position3D(-3, "y", DateValue(dfmt.parse("1999-01-01"), DateCodec()))
}

class TestOverPosition3D extends TestSlicePosition3D {

  val list = List((Over[Position2D, Position3D](First), First), (Over[Position2D, Position3D](Second), Second),
    (Over[Position2D, Position3D](Third), Third))

  "A Over[Position3D]" should "return a Position1D for the selected dimension" in {
    list.foreach { case (o, d) => o.selected(pos1) shouldBe Position1D(pos1(d)) }
  }

  it should "return a Position2D for the remainder" in {
    list.foreach { case (o, d) => o.remainder(pos1) shouldBe pos1.remove(d) }
  }
}

class TestAlongPosition3D extends TestSlicePosition3D {

  val list = List((Along[Position2D, Position3D](First), First), (Along[Position2D, Position3D](Second), Second),
    (Along[Position2D, Position3D](Third), Third))

  "A Along[Position3D]" should "return a Position2D for the selected dimension" in {
    list.foreach { case (a, d) => a.selected(pos1) shouldBe pos1.remove(d) }
  }

  it should "return a Position1D for the remainder" in {
    list.foreach { case (a, d) => a.remainder(pos1) shouldBe Position1D(pos1(d)) }
  }
}

trait TestSlicePosition4D extends TestSlice {
  val pos1 = Position4D(4, "c", DateValue(dfmt.parse("2002-01-01"), DateCodec()), "foo")
  val pos2 = Position4D(-4, "x", DateValue(dfmt.parse("1998-01-01"), DateCodec()), "oof")
}

class TestOverPosition4D extends TestSlicePosition4D {

  val list = List((Over[Position3D, Position4D](First), First), (Over[Position3D, Position4D](Second), Second),
    (Over[Position3D, Position4D](Third), Third), (Over[Position3D, Position4D](Fourth), Fourth))

  "A Over[Position4D]" should "return a Position1D for the selected dimension" in {
    list.foreach { case (o, d) => o.selected(pos1) shouldBe Position1D(pos1(d)) }
  }

  it should "return a Position3D for the remainder" in {
    list.foreach { case (o, d) => o.remainder(pos1) shouldBe pos1.remove(d) }
  }
}

class TestAlongPosition4D extends TestSlicePosition4D {

  val list = List((Along[Position3D, Position4D](First), First), (Along[Position3D, Position4D](Second), Second),
    (Along[Position3D, Position4D](Third), Third), (Along[Position3D, Position4D](Fourth), Fourth))

  "A Along[Position4D]" should "return a Position3D for the selected dimension" in {
    list.foreach { case (a, d) => a.selected(pos1) shouldBe pos1.remove(d) }
  }

  it should "return a Position1D for the remainder" in {
    list.foreach { case (a, d) => a.remainder(pos1) shouldBe Position1D(pos1(d)) }
  }
}

trait TestSlicePosition5D extends TestSlice {
  val pos1 = Position5D(5, "d", DateValue(dfmt.parse("2003-01-01"), DateCodec()), "bar", 3.1415)
  val pos2 = Position5D(-5, "w", DateValue(dfmt.parse("1997-01-01"), DateCodec()), "rab", -3.1415)
}

class TestOverPosition5D extends TestSlicePosition5D {

  val list = List((Over[Position4D, Position5D](First), First), (Over[Position4D, Position5D](Second), Second),
    (Over[Position4D, Position5D](Third), Third), (Over[Position4D, Position5D](Fourth), Fourth),
    (Over[Position4D, Position5D](Fifth), Fifth))

  "A Over[Position5D]" should "return a Position1D for the selected dimension" in {
    list.foreach { case (o, d) => o.selected(pos1) shouldBe Position1D(pos1(d)) }
  }

  it should "return a Position4D for the remainder" in {
    list.foreach { case (o, d) => o.remainder(pos1) shouldBe pos1.remove(d) }
  }
}

class TestAlongPosition5D extends TestSlicePosition5D {

  val list = List((Along[Position4D, Position5D](First), First), (Along[Position4D, Position5D](Second), Second),
    (Along[Position4D, Position5D](Third), Third), (Along[Position4D, Position5D](Fourth), Fourth),
    (Along[Position4D, Position5D](Fifth), Fifth))

  "A Along[Position5D]" should "return a Position4D for the selected dimension" in {
    list.foreach { case (a, d) => a.selected(pos1) shouldBe pos1.remove(d) }
  }

  it should "return a Position1D for the remainder" in {
    list.foreach { case (a, d) => a.remainder(pos1) shouldBe Position1D(pos1(d)) }
  }
}

