package server.maps;

import java.awt.Point;
import java.util.concurrent.locks.Lock;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;

public class MapleMapItem extends AbstractMapleMapObject {
   protected MapleClient ownerClient;
   protected Item item;
   protected MapleMapObject dropper;
   protected int characterOwnerId, partyOwnerId, meso, questId = -1;
   protected byte type;
   protected boolean pickedUp = false, playerDrop, partyDrop;
   protected long dropTime;
   private Lock itemLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MAP_ITEM);

   public MapleMapItem(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, MapleClient ownerClient, byte type, boolean playerDrop) {
      position_$eq(position);
      this.item = item;
      this.dropper = dropper;
      this.characterOwnerId = owner.getId();
      this.partyOwnerId = owner.getPartyId();
      this.partyDrop = this.partyOwnerId != -1;
      this.ownerClient = owner.getClient();
      this.meso = 0;
      this.type = type;
      this.playerDrop = playerDrop;
   }

   public MapleMapItem(Item item, Point position, MapleMapObject dropper, MapleCharacter owner, MapleClient ownerClient, byte type, boolean playerDrop, int questId) {
      position_$eq(position);
      this.item = item;
      this.dropper = dropper;
      this.characterOwnerId = owner.getId();
      this.partyOwnerId = owner.getPartyId();
      this.partyDrop = this.partyOwnerId != -1;
      this.ownerClient = owner.getClient();
      this.meso = 0;
      this.type = type;
      this.playerDrop = playerDrop;
      this.questId = questId;
   }

   public MapleMapItem(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, MapleClient ownerClient, byte type, boolean playerDrop) {
      position_$eq(position);
      this.item = null;
      this.dropper = dropper;
      this.characterOwnerId = owner.getId();
      this.partyOwnerId = owner.getPartyId();
      this.partyDrop = this.partyOwnerId != -1;
      this.ownerClient = owner.getClient();
      this.meso = meso;
      this.type = type;
      this.playerDrop = playerDrop;
   }

   public final Item getItem() {
      return item;
   }

   public final int getQuest() {
      return questId;
   }

   public final int getItemId() {
      if (meso > 0) {
         return meso;
      }
      return item.id();
   }

   public final MapleMapObject getDropper() {
      return dropper;
   }

   public final int getOwnerId() {
      return characterOwnerId;
   }

   public final int getPartyOwnerId() {
      return partyOwnerId;
   }

   public final void setPartyOwnerId(int partyId) {
      partyOwnerId = partyId;
   }

   public final int getClientSideOwnerId() {
      if (this.partyOwnerId == -1) {
         return this.characterOwnerId;
      } else {
         return this.partyOwnerId;
      }
   }

   public final boolean hasClientSideOwnership(MapleCharacter player) {
      return this.characterOwnerId == player.getId() || this.partyOwnerId == player.getPartyId() || hasExpiredOwnershipTime();
   }

   public final boolean isFFADrop() {
      return type == 2 || type == 3 || hasExpiredOwnershipTime();
   }

   public final boolean hasExpiredOwnershipTime() {
      return System.currentTimeMillis() - dropTime >= 15 * 1000;
   }

   public final boolean canBePickedBy(MapleCharacter chr) {
      if (characterOwnerId <= 0 || isFFADrop()) {
         return true;
      }

      if (partyOwnerId == -1) {
         if (chr.getId() == characterOwnerId) {
            return true;
         } else if (chr.isPartyMember(characterOwnerId)) {
            partyOwnerId = chr.getPartyId();
            return true;
         }
      } else {
         if (chr.getPartyId() == partyOwnerId) {
            return true;
         } else if (chr.getId() == characterOwnerId) {
            partyOwnerId = chr.getPartyId();
            return true;
         }
      }

      return hasExpiredOwnershipTime();
   }

   public final MapleClient getOwnerClient() {
      return (ownerClient.isLoggedIn() && !ownerClient.getPlayer().isAwayFromWorld()) ? ownerClient : null;
   }

   public final int getMeso() {
      return meso;
   }

   public final boolean isPlayerDrop() {
      return playerDrop;
   }

   public final boolean isPickedUp() {
      return pickedUp;
   }

   public void setPickedUp(final boolean pickedUp) {
      this.pickedUp = pickedUp;
   }

   public long getDropTime() {
      return dropTime;
   }

   public void setDropTime(long time) {
      this.dropTime = time;
   }

   public byte getDropType() {
      return type;
   }

   public void lockItem() {
      itemLock.lock();
   }

   public void unlockItem() {
      itemLock.unlock();
   }

   @Override
   public final MapleMapObjectType type() {
      return MapleMapObjectType.ITEM;
   }
}