package rest.master.maker

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class MakerReagent @JsonCreator()(@JsonProperty("item") _item: Integer, @JsonProperty("stat") _stat: String,
                                  @JsonProperty("value") _value: Integer) {
  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("stat")
  def stat: String = _stat

  @JsonGetter("value")
  def value: Integer = _value
}
