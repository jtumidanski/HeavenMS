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

   private FaceExpressionScheduler faceExpressionSchedulers[] = new FaceExpressionScheduler[YamlConfig.config.server.CHANNEL_LOCKS];
   private MonitoredReentrantLock faceLock[] = new MonitoredReentrantLock[YamlConfig.config.server.CHANNEL_LOCKS];

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
      LockCollector.getInstance().registerDisposeAction(new Runnable() {
         @Override
         public void run() {
            emptyLocks();
         }
      });
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
      int lockid = getChannelSchedulerIndex(map.getId());

      Runnable cancelAction = new Runnable() {
         @Override
         public void run() {
            if (chr.isLoggedinWorld()) {
               map.broadcastMessage(chr, new FacialExpression(chr.getId(), 0));
            }
         }
      };

      faceLock[lockid].lock();
      try {
         if (!chr.isLoggedinWorld()) {
            return;
         }

         faceExpressionSchedulers[lockid].registerFaceExpression(chr.getId(), cancelAction);
      } finally {
         faceLock[lockid].unlock();
      }

      map.broadcastMessage(chr, new FacialExpression(chr.getId(), emote));
   }

   public void unregisterFaceExpression(int mapid, MapleCharacter chr) {
      int lockid = getChannelSchedulerIndex(mapid);

      faceLock[lockid].lock();
      try {
         faceExpressionSchedulers[lockid].unregisterFaceExpression(chr.getId());
      } finally {
         faceLock[lockid].unlock();
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