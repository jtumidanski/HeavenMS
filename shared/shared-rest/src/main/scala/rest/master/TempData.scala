package rest.master

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class TempData @JsonCreator()(@JsonProperty("dropper") _dropper: Integer, @JsonProperty("item") _item: Integer,
                              @JsonProperty("minimum-quantity") _minimumQuantity: Integer,
                              @JsonProperty("maximum-quantity") _maximumQuantity: Integer,
                              @JsonProperty("quest") _quest: Integer, @JsonProperty("chance") _chance: Integer) {
  @JsonGetter("dropper")
  def dropper: Integer = _dropper

  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("minimum-quantity")
  def minimumQuantity: Integer = _minimumQuantity

  @JsonGetter("maximum-quantity")
  def maximumQuantity: Integer = _maximumQuantity

  @JsonGetter("quest")
  def quest: Integer = _quest

  @JsonGetter("chance")
  def chance: Integer = _chance
}
