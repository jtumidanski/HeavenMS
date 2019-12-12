package rest.master

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class Shop @JsonCreator()(@JsonProperty("id") _id: Integer, @JsonProperty("npc") _npc: Integer) {
  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("npc")
  def npc: Integer = _npc
}
