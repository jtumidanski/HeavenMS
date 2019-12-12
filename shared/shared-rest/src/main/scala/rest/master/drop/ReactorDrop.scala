package rest.master.drop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class ReactorDrop @JsonCreator()(@JsonProperty("reactor") _reactor: Integer, @JsonProperty("item") _item: Integer,
                                 @JsonProperty("chance") _chance: Integer, @JsonProperty("quest") _quest: Integer) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("reactor")
  def reactor: Integer = _reactor

  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("chance")
  def chance: Integer = _chance

  @JsonGetter("quest")
  def quest: Integer = _quest
}
