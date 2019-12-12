package rest.master.maker

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class MakerRecipe @JsonCreator()(@JsonProperty("item") _item: Integer,
                                 @JsonProperty("required-item") _requiredItem: Integer,
                                 @JsonProperty("count") _count: Integer) {
  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("required-item")
  def requiredItem: Integer = _requiredItem

  @JsonGetter("count")
  def count: Integer = _count
}
