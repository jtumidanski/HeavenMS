package rest.cashshop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class GiftsResponse @JsonCreator()(@JsonProperty("gifts") _gifts: java.util.List[Gift]) {
  @JsonGetter("gifts")
  def gifts: java.util.List[Gift] = _gifts
}
