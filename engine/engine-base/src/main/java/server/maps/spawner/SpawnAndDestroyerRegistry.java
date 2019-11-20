package server.maps.spawner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import server.maps.MapleMapObject;

public class SpawnAndDestroyerRegistry {
   private static class Holder<T extends MapleMapObject> {
      private final Class<T> type;
      private final MapObjectSpawnAndDestroyer<T> handler;

      Holder(Class<T> c, MapObjectSpawnAndDestroyer<T> h) {
         type = Objects.requireNonNull(c);
         handler = h;
      }

      <U extends MapleMapObject> Holder<U> as(Class<U> expected) {
         if (type != expected) {
            throw new ClassCastException();
         }
         @SuppressWarnings("unchecked")
         Holder<U> h = (Holder) this;
         return h;
      }

      public MapObjectSpawnAndDestroyer getHandler() {
         return handler;
      }
   }

   private Map<Class<?>, Holder<?>> handlers = new HashMap<>();

   public <T extends MapleMapObject> void setHandler(Class<T> type, MapObjectSpawnAndDestroyer<T> handler) {
      handlers.put(type, new Holder<>(type, handler));
   }

   public <T extends MapleMapObject> Optional<MapObjectSpawnAndDestroyer<T>> getHandler(Class<T> type) {
      try {
         if (!handlers.containsKey(type)) {
            return Optional.empty();
         }
         return Optional.of(handlers.get(type).as(type).getHandler());
      } catch (ClassCastException exception) {
         return Optional.empty();
      }
   }
}
