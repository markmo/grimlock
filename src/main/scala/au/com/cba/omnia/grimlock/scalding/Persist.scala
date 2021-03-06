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

package au.com.cba.omnia.grimlock.scalding

import au.com.cba.omnia.grimlock.framework.{ Persist => FwPersist }

import au.com.cba.omnia.grimlock.scalding.environment._

import com.twitter.scalding.TextLine
import com.twitter.scalding.typed.TypedSink

/** Trait for peristing a Scalding `TypedPipe`. */
trait Persist[T] extends FwPersist[T] with DistributedData with Environment {
  protected def saveText(file: String, writer: TextWriter)(implicit ctx: C): U[T] = {
    import ctx._

    data
      .flatMap(writer(_))
      .write(TypedSink(TextLine(file)))

    data
  }
}

