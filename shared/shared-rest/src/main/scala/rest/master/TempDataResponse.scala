package rest.master

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class TempDataResponse @JsonCreator()(@JsonProperty("results") _results: java.util.List[TempData]) {
  @JsonGetter("results")
  def results: java.util.List[TempData] = _results
}
