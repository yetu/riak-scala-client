/*
 * Copyright (C) 2012-2013 Age Mooij (http://scalapenos.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scalapenos.riak.serialization

import com.scalapenos.riak._
import spray.http.{ ContentTypes, MediaTypes }

trait PlayJsonSerialization {

  import play.api.libs.json._

  import scala.reflect._
  import scala.util._

  class PlayJsonSerializer[T] extends RiakSerializer[T] {

    implicit def writes: Writes[T] = ??? //TODO

    /** Serializes an instance of T to a Tuple2 of raw data (a String) and a ContentType. */
    override def serialize(t: T): (String, ContentType) = {
      (Json.toJson(t).toString(), ContentTypes.`application/json`)
    }
  }

  class PlayJsonDeserializer[T: ClassTag] extends RiakDeserializer[T] {
    implicit def reads: Reads[T] = ??? //TODO

    /**
     * Deserializes from some raw data and a ContentType to a type T.
     * @throws RiakDeserializationException if the content could not be converted to an instance of T.
     */
    override def deserialize(data: String, contentType: ContentType): T = {
      contentType match {
        case ContentType(MediaTypes.`application/json`, _) ⇒ parseAndConvert(data)
        case _                                             ⇒ throw RiakUnsupportedContentType(ContentTypes.`application/json`, contentType)
      }
    }

    private def parseAndConvert(data: String): T = {
      Try(Json.parse(data).as[T]) match {
        case Success(t)         ⇒ t.asInstanceOf[T] //TODO: asInstanceof?
        case Failure(throwable) ⇒ throw RiakDeserializationFailed(data, classTag[T].runtimeClass.getName, throwable)
      }
    }
  }

}

object PlayJsonSerialization extends PlayJsonSerialization
