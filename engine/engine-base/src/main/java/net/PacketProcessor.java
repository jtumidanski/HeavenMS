package net;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.opcodes.RecvOpcode;
import net.server.HandlerFactory;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public final class PacketProcessor {

   private final static Map<String, PacketProcessor> instances = new LinkedHashMap<>();
   private Map<Integer, MaplePacketHandler> handlers;

   private PacketProcessor() {
      handlers = new HashMap<>();
   }

   public synchronized static PacketProcessor getProcessor(int world, int channel) {
      final String pair = world + " " + channel;
      PacketProcessor processor = instances.get(pair);
      if (processor == null) {
         processor = new PacketProcessor();
         HandlerFactory.getInstance().registerHandlers(processor, channel);
         instances.put(pair, processor);
      }
      return processor;
   }

   public MaplePacketHandler getHandler(short packetId) {
      return handlers.get((int) packetId);
   }

   public void registerHandler(RecvOpcode code, MaplePacketHandler handler) {
      try {
         handlers.put(code.getValue(), handler);
      } catch (ArrayIndexOutOfBoundsException e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, e, "Error registering handler - " + code.name());
      }
   }

   public void reset(int channel) {
      handlers = new HashMap<>();
   }
}