package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamilyEntitlement;
import client.MapleFamilyEntry;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilyUsePacket;
import net.server.channel.packet.reader.FamilyUseReader;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import tools.PacketCreator;
import tools.packet.family.FamilyMessage;
import tools.packet.family.FamilySummonRequest;
import tools.packet.family.GetFamilyInfo;

public final class FamilyUseHandler extends AbstractPacketHandler<FamilyUsePacket> {
   @Override
   public Class<FamilyUseReader> getReaderClass() {
      return FamilyUseReader.class;
   }

   @Override
   public void handlePacket(FamilyUsePacket packet, MapleClient client) {
      if (!YamlConfig.config.server.USE_FAMILY_SYSTEM) {
         return;
      }
      MapleFamilyEntitlement type = MapleFamilyEntitlement.values()[packet.entitlementId()];
      int cost = type.getRepCost();
      MapleFamilyEntry entry = client.getPlayer().getFamilyEntry();
      if (entry.getReputation() < cost || entry.isEntitlementUsed(type)) {
         return; // shouldn't even be able to request it
      }
      PacketCreator.announce(client, new GetFamilyInfo(entry));
      Optional<MapleCharacter> victim;
      if (type == MapleFamilyEntitlement.FAMILY_REUNION || type == MapleFamilyEntitlement.SUMMON_FAMILY) {
         victim = client.getChannelServer().getPlayerStorage().getCharacterByName(packet.characterName());
         if (victim.isPresent() && victim.get() != client.getPlayer()) {
            if (victim.get().getFamily() == client.getPlayer().getFamily()) {
               MapleMap targetMap = victim.get().getMap();
               MapleMap ownMap = client.getPlayer().getMap();
               if (targetMap != null) {
                  if (type == MapleFamilyEntitlement.FAMILY_REUNION) {
                     if (!FieldLimit.CANNOT_MIGRATE.check(ownMap.getFieldLimit()) && !FieldLimit.CANNOT_VIP_ROCK.check(targetMap.getFieldLimit())
                           && (targetMap.getForcedReturnId() == 999999999 || targetMap.getId() < 100000000) && targetMap.getEventInstance() == null) {

                        client.getPlayer().changeMap(victim.get().getMap(), victim.get().getMap().getPortal(0));
                        useEntitlement(entry, type);
                     } else {
                        PacketCreator.announce(client, new FamilyMessage(75, 0)); // wrong message, but close enough. (client should check this first anyway)
                     }
                  } else {
                     if (!FieldLimit.CANNOT_MIGRATE.check(targetMap.getFieldLimit()) && !FieldLimit.CANNOT_VIP_ROCK.check(ownMap.getFieldLimit())
                           && (ownMap.getForcedReturnId() == 999999999 || ownMap.getId() < 100000000) && ownMap.getEventInstance() == null) {

                        if (MapleInviteCoordinator.hasInvite(InviteType.FAMILY_SUMMON, victim.get().getId())) {
                           PacketCreator.announce(client, new FamilyMessage(74, 0));
                           return;
                        }
                        MapleInviteCoordinator.createInvite(InviteType.FAMILY_SUMMON, client.getPlayer(), victim.get(), victim.get().getId(), client.getPlayer().getMap());
                        PacketCreator.announce(victim.get(), new FamilySummonRequest(client.getPlayer().getFamily().getName(), client.getPlayer().getName()));
                        useEntitlement(entry, type);
                     } else {
                        PacketCreator.announce(client, new FamilyMessage(75, 0));
                     }
                  }
               }
            } else {
               PacketCreator.announce(client, new FamilyMessage(67, 0));
            }
         }
      } else if (type == MapleFamilyEntitlement.FAMILY_BONDING) {
         //not implemented
      } else {
         boolean party = false;
         boolean isExp = false;
         float rate = 1.5f;
         int duration = 15;
         do {
            switch (type) {
               case PARTY_EXP_2_30MIN:
                  party = true;
                  isExp = true;
                  type = MapleFamilyEntitlement.SELF_EXP_2_30MIN;
                  continue;
               case PARTY_DROP_2_30MIN:
                  party = true;
                  type = MapleFamilyEntitlement.SELF_DROP_2_30MIN;
                  continue;
               case SELF_DROP_2_30MIN:
                  duration = 30;
               case SELF_DROP_2:
                  rate = 2.0f;
               case SELF_DROP_1_5:
                  break;
               case SELF_EXP_2_30MIN:
                  duration = 30;
               case SELF_EXP_2:
                  rate = 2.0f;
               case SELF_EXP_1_5:
                  isExp = true;
               default:
                  break;
            }
            break;
         } while (true);
         //not implemented
      }
   }

   private boolean useEntitlement(MapleFamilyEntry entry, MapleFamilyEntitlement entitlement) {
      if (entry.useEntitlement(entitlement)) {
         entry.gainReputation(-entitlement.getRepCost(), false);
         PacketCreator.announce(entry.getChr(), new GetFamilyInfo(entry));
         return true;
      }
      return false;
   }
}
