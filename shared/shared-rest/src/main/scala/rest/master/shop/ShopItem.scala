package rest.master.shop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class ShopItem @JsonCreator()(@JsonProperty("item") _item: Integer,
                              @JsonProperty("price") _price: Integer, @JsonProperty("pitch") _pitch: Integer,
                              @JsonProperty("position") _position: Integer) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("price")
  def price: Integer = _price

  @JsonGetter("pitch")
  def pitch: Integer = _pitch

  @JsonGetter("position")
  def position: Integer = _position
}
