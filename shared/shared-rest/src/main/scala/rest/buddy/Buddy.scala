package rest.buddy

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class Buddy @JsonCreator()(@JsonProperty("id") _id: Integer, @JsonProperty("group") _group: String) {
  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("group")
  def group: String = _group
}
