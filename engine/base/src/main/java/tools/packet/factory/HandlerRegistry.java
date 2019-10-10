package tools.packet.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import tools.packet.PacketInput;

public class HandlerRegistry {
   private static class Holder<T> {
      private final Class<T> type;
      private final Function<PacketInput, byte[]> handler;

      Holder(Class<T> c, Function<PacketInput, byte[]> h) {
         type = Objects.requireNonNull(c);
         handler = h;
      }

      <U> Holder<U> as(Class<U> expected) {
         if (type != expected) {
            throw new ClassCastException();
         }
         @SuppressWarnings("unchecked")
         Holder<U> h = (Holder) this;
         return h;
      }

      public Function<PacketInput, byte[]> getHandler() {
         return handler;
      }
   }

   private Map<Class<?>, Holder<?>> handlers = new HashMap<>();

   <T extends PacketInput> void setHandler(Class<T> type, Function<PacketInput, byte[]> handler) {
      handlers.put(type, new Holder<>(type, handler));
   }

   <T extends PacketInput> Optional<Function<PacketInput, byte[]>> getHandler(Class<T> type) {
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
