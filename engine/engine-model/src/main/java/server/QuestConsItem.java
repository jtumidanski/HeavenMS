package server;

import java.util.Map;

public record QuestConsItem(Integer questId, Integer exp, Integer grade, Map<Integer, Integer> items) {
}
