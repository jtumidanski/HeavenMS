package rest.master.monstercard

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class MonsterCard @JsonCreator()(@JsonProperty("card") _card: Integer, @JsonProperty("mob") _mob: Integer) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("card")
  def card: Integer = _card

  @JsonGetter("mob")
  def mob: Integer = _mob
}
