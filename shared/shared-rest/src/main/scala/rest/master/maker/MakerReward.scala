package rest.master.maker

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class MakerReward @JsonCreator()(@JsonProperty("item") _item: Integer, @JsonProperty("reward") _reward: Integer,
                                 @JsonProperty("quantity") _quantity: Integer,
                                 @JsonProperty("probability") _probability: Integer) {
  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("reward")
  def reward: Integer = _reward

  @JsonGetter("quantity")
  def quantity: Integer = _quantity

  @JsonGetter("probability")
  def probability: Integer = _probability
}
