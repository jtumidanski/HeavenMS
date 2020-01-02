package net.server.services.task.channel;

import java.util.Collections;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.services.BaseScheduler;
import net.server.services.BaseService;
import server.maps.MapleMap;
import tools.packet.character.FacialExpression;

public class FaceExpressionService extends BaseService {

   private FaceExpressionScheduler[] faceExpressionSchedulers = new FaceExpressionScheduler[YamlConfig.config.server.CHANNEL_LOCKS];
   private MonitoredReentrantLock[] faceLock = new MonitoredReentrantLock[YamlConfig.config.server.CHANNEL_LOCKS];

   public FaceExpressionService() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         faceLock[i] = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHANNEL_FACEEXPRS, true);
         faceExpressionSchedulers[i] = new FaceExpressionScheduler(faceLock[i]);
      }
   }

   private void emptyLocks() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         faceLock[i] = faceLock[i].dispose();
      }
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   public void dispose() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         if (faceExpressionSchedulers[i] != null) {
            faceExpressionSchedulers[i].dispose();
            faceExpressionSchedulers[i] = null;
         }
      }

      disposeLocks();
   }

   public void registerFaceExpression(final MapleMap map, final MapleCharacter chr, int emote) {
      int lockId = getChannelSchedulerIndex(map.getId());

      Runnable cancelAction = () -> {
         if (chr.isLoggedInWorld()) {
            map.broadcastMessage(chr, new FacialExpression(chr.getId(), 0));
         }
      };

      faceLock[lockId].lock();
      try {
         if (!chr.isLoggedInWorld()) {
            return;
         }

         faceExpressionSchedulers[lockId].registerFaceExpression(chr.getId(), cancelAction);
      } finally {
         faceLock[lockId].unlock();
      }

      map.broadcastMessage(chr, new FacialExpression(chr.getId(), emote));
   }

   public void unregisterFaceExpression(int mapId, MapleCharacter chr) {
      int lockId = getChannelSchedulerIndex(mapId);

      faceLock[lockId].lock();
      try {
         faceExpressionSchedulers[lockId].unregisterFaceExpression(chr.getId());
      } finally {
         faceLock[lockId].unlock();
      }
   }

   private class FaceExpressionScheduler extends BaseScheduler {

      public FaceExpressionScheduler(final MonitoredReentrantLock channelFaceLock) {
         super(MonitoredLockType.CHANNEL_FACESCHDL, Collections.singletonList(channelFaceLock));
      }

      public void registerFaceExpression(Integer characterId, Runnable runAction) {
         registerEntry(characterId, runAction, 5000);
      }

      public void unregisterFaceExpression(Integer characterId) {
         interruptEntry(characterId);
      }

   }

}