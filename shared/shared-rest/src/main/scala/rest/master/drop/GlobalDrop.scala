package rest.master.drop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class GlobalDrop @JsonCreator()(@JsonProperty("continent") _continent: Integer, @JsonProperty("item") _itemId: Integer,
                                @JsonProperty("minimum-quantity") _minimumQuantity: Integer,
                                @JsonProperty("maximum-quantity") _maximumQuantity: Integer,
                                @JsonProperty("quest") _questId: Integer, @JsonProperty("chance") _chance: Integer,
                                @JsonProperty("comment") _comment: String) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("continent")
  def continent: Integer = _continent

  @JsonGetter("item")
  def itemId: Integer = _itemId

  @JsonGetter("minimum-quantity")
  def minimumQuantity: Integer = _minimumQuantity

  @JsonGetter("maximum-quantity")
  def maximumQuantity: Integer = _maximumQuantity

  @JsonGetter("quest")
  def questId: Integer = _questId

  @JsonGetter("chance")
  def chance: Integer = _chance

  @JsonGetter("comment")
  def comment: String = _comment
}
