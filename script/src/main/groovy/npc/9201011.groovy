package npc

import client.MapleCharacter
import config.YamlConfig
import net.server.channel.handlers.RingActionHandler
import scripting.AbstractPlayerInteraction
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.MasterBroadcaster
import tools.MessageBroadcaster
import tools.PacketCreator
import tools.ServerNoticeType
import tools.packet.foreigneffect.ShowForeignEffect
import tools.packet.showitemgaininchat.ShowSpecialEffect

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201011 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   EventInstanceManager eim
   String weddingEventName = "WeddingChapel"
   boolean cathedralWedding = false
   boolean weddingIndoors
   int weddingBlessingExp = YamlConfig.config.server.WEDDING_BLESS_EXP

   static def detectPlayerItemId(MapleCharacter player) {
      for (int x = 4031357; x <= 4031364; x++) {
         if (player.haveItem(x)) {
            return x
         }
      }

      return -1
   }

   static def getRingId(boxItemId) {
      return boxItemId == 4031357 ? 1112803 : (boxItemId == 4031359 ? 1112806 : (boxItemId == 4031361 ? 1112807 : (boxItemId == 4031363 ? 1112809 : -1)))
   }

   static def isSuitedForWedding(MapleCharacter player, equipped) {
      int baseId = (player.getGender() == 0) ? 1050131 : 1051150

      if (equipped) {
         for (int i = 0; i < 4; i++) {
            if (player.haveItemEquipped(baseId + i)) {
               return true
            }
         }
      } else {
         for (int i = 0; i < 4; i++) {
            if (player.haveItemWithId(baseId + i, true)) {
               return true
            }
         }
      }

      return false
   }

   static def getWeddingPreparationStatus(MapleCharacter player, MapleCharacter partner) {
      if (!player.haveItem(4000313)) {
         return -3
      }
      if (!partner.haveItem(4000313)) {
         return 3
      }

      if (!isSuitedForWedding(player, true)) {
         return -4
      }
      if (!isSuitedForWedding(partner, true)) {
         return 4
      }

      boolean hasEngagement = false
      for (int x = 4031357; x <= 4031364; x++) {
         if (player.haveItem(x)) {
            hasEngagement = true
            break
         }
      }
      if (!hasEngagement) {
         return -1
      }

      hasEngagement = false
      for (int x = 4031357; x <= 4031364; x++) {
         if (partner.haveItem(x)) {
            hasEngagement = true
            break
         }
      }
      if (!hasEngagement) {
         return -2
      }

      if (!player.canHold(1112803)) {
         return 1
      }
      if (!partner.canHold(1112803)) {
         return 2
      }

      return 0
   }

   def giveCoupleBlessings(EventInstanceManager eim, MapleCharacter player, MapleCharacter partner) {
      int blessCount = eim.gridSize()

      player.gainExp(blessCount * weddingBlessingExp)
      partner.gainExp(blessCount * weddingBlessingExp)
   }

   def start() {
      eim = cm.getEventInstance()
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (eim == null) {
               cm.warp(680000000, 0)
               cm.dispose()
               return
            }

            int playerId = cm.getPlayer().getId()
            if (playerId == eim.getIntProperty("groomId") || playerId == eim.getIntProperty("brideId")) {
               int weddingStage = eim.getIntProperty("weddingStage")

               if (weddingStage == 2) {
                  cm.sendYesNo(I18nMessage.from("9201011_SHOULD_I"))
                  state = 1
               } else if (weddingStage == 1) {
                  cm.sendOk(I18nMessage.from("9201011_WAIT_A_BIT"))
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9201011_FESTIVAL_NOW_COMPLETE"))
                  cm.dispose()
               }
            } else {
               int weddingStage = eim.getIntProperty("weddingStage")
               if (weddingStage == 1) {
                  if (eim.gridCheck(cm.getPlayer()) != -1) {
                     cm.sendOk(I18nMessage.from("9201011_SHAKE_THIS_PLACE_UP"))
                     cm.dispose()
                  } else {
                     if (eim.getIntProperty("guestBlessings") == 1) {
                        cm.sendYesNo(I18nMessage.from("9201011_WILL_YOU"))
                        state = 0
                     } else {
                        cm.sendOk(I18nMessage.from("9201011_NICE_PARTY"))
                        cm.dispose()
                     }
                  }
               } else if (weddingStage == 3) {
                  cm.sendOk(I18nMessage.from("9201011_GET_READY"))
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9201011_ALL_OVER_THE_PLACE"))
                  cm.dispose()
               }
            }
         } else if (status == 1) {
            if (state == 0) {    // give player blessings
               eim.gridInsert(cm.getPlayer(), 1)

               if (YamlConfig.config.server.WEDDING_BLESSER_SHOWFX) {
                  MapleCharacter target = cm.getPlayer()
                  PacketCreator.announce(target, new ShowSpecialEffect(9))
                  MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), new ShowForeignEffect(target.getId(), 9), false, target)
               } else {
                  MapleCharacter target = eim.getPlayerById(eim.getIntProperty("groomId"))
                  PacketCreator.announce(target, new ShowSpecialEffect(9))
                  MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), new ShowForeignEffect(target.getId(), 9), false, target)

                  target = eim.getPlayerById(eim.getIntProperty("brideId"))
                  PacketCreator.announce(target, new ShowSpecialEffect(9))
                  MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), new ShowForeignEffect(target.getId(), 9), false, target)
               }

               cm.sendOk(I18nMessage.from("9201011_WAY_TO_GO"))
               cm.dispose()
            } else {            // couple wants to complete the wedding
               int weddingStage = eim.getIntProperty("weddingStage")

               if (weddingStage == 2) {
                  int pid = cm.getPlayer().getPartnerId()
                  if (pid <= 0) {
                     cm.sendOk(I18nMessage.from("9201011_WHAT_HAPPENED"))
                     cm.dispose()
                     return
                  }

                  MapleCharacter player = cm.getPlayer()
                  MapleCharacter partner = cm.getMap().getCharacterById(cm.getPlayer().getPartnerId())
                  if (partner != null) {
                     state = getWeddingPreparationStatus(player, partner)

                     switch (state) {
                        case 0:
                           pid = eim.getIntProperty("confirmedVows")
                           if (pid != -1) {
                              if (pid == player.getId()) {
                                 cm.sendOk(I18nMessage.from("9201011_ALREADY_CONFIRMED"))
                              } else {
                                 eim.setIntProperty("weddingStage", 3)
                                 AbstractPlayerInteraction cmPartner = partner.getAbstractPlayerInteraction()

                                 int playerItemId = detectPlayerItemId(player)
                                 int partnerItemId = (playerItemId % 2 == 1) ? playerItemId + 1 : playerItemId - 1

                                 int marriageRingId = getRingId((playerItemId % 2 == 1) ? playerItemId : partnerItemId)

                                 cm.gainItem(playerItemId, (short) -1)
                                 cmPartner.gainItem(partnerItemId, (short) -1)

                                 RingActionHandler.giveMarriageRings(player, partner, marriageRingId)
                                 player.setMarriageItemId(marriageRingId)
                                 partner.setMarriageItemId(marriageRingId)

                                 //var marriageId = eim.getIntProperty("weddingId");
                                 //player.announce(Wedding.OnMarriageResult(marriageId, player, true));
                                 //partner.announce(Wedding.OnMarriageResult(marriageId, player, true));

                                 giveCoupleBlessings(eim, player, partner)

                                 MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_WAYNE"))
                                 eim.schedule("showMarriedMsg", 2 * 1000)
                              }
                           } else {
                              eim.setIntProperty("confirmedVows", player.getId())
                              MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_ONE_LAST_STEP").with(player.getName()))
                           }

                           break

                        case -1:
                           cm.sendOk(I18nMessage.from("9201011_MISSING_RING_BOX"))
                           break

                        case -2:
                           cm.sendOk(I18nMessage.from("9201011_PARTNER_MISSING_RING_BOX"))
                           break

                        case -3:
                           cm.sendOk(I18nMessage.from("9201011_PLEASE_FIND_IT"))
                           break

                        case -4:
                           cm.sendOk(I18nMessage.from("9201011_FASHIONABLE_CLOTHES"))
                           break

                        case 1:
                           cm.sendOk(I18nMessage.from("9201011_MAKE_EQUIP_SLOT"))
                           break

                        case 2:
                           cm.sendOk(I18nMessage.from("9201011_PARTNER_MAKE_EQUIP_SLOT"))
                           break

                        case 3:
                           cm.sendOk(I18nMessage.from("9201011_PARTNER_PLEASE_FIND_IT"))
                           break

                        case 4:
                           cm.sendOk(I18nMessage.from("9201011_PARTNER_FASHIONABLE_CLOTHES"))
                           break
                     }

                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("9201011_PARTNER_NOT_HERE"))
                     cm.dispose()
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9201011_OFFICIALLY_ONE_COUPLE"))
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC9201011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201011(cm: cm))
   }
   return (NPC9201011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }