package npc

import client.MapleCharacter
import config.YamlConfig
import net.server.channel.Channel
import net.server.channel.handlers.RingActionHandler
import scripting.AbstractPlayerInteraction
import scripting.event.EventInstanceManager
import scripting.event.EventManager
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


class NPC9201002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   int state
   EventInstanceManager eim
   String weddingEventName = "WeddingCathedral"
   boolean cathedralWedding = true
   boolean weddingIndoors
   int weddingBlessingExp = YamlConfig.config.server.WEDDING_BLESS_EXP

   static def isWeddingIndoors(int mapId) {
      return mapId >= 680000100 && mapId <= 680000500
   }

   def getMarriageInstance(MapleCharacter player) {
      EventManager em = cm.getEventManager(weddingEventName)

      for (Iterator<EventInstanceManager> iterator = em.getInstances().iterator(); iterator.hasNext();) {
         EventInstanceManager eim = iterator.next()
         if (eim.isEventLeader(player)) {
            return eim
         }
      }

      return null
   }

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

   static def isSuitedForWedding(MapleCharacter player, boolean equipped) {
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
      weddingIndoors = isWeddingIndoors(cm.getMapId())
      if (weddingIndoors) {
         eim = cm.getEventInstance()
      }

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

         if (!weddingIndoors) {
            if (status == 0) {
               boolean hasEngagement = false
               for (int x = 4031357; x <= 4031364; x++) {
                  if (cm.haveItem(x, 1)) {
                     hasEngagement = true
                     break
                  }
               }

               if (hasEngagement) {
                  String text = "Hi there. How can I help you?"
                  String[] choice = ["We're ready to get married."]
                  for (int x = 0; x < choice.length; x++) {
                     text += "\r\n#L" + x + "##b" + choice[x] + "#l"
                  }
                  cm.sendSimple(text)
               } else {
                  cm.sendOk(I18nMessage.from("9201002_TODAY_TWO"))

                  cm.dispose()
               }
            } else if (status == 1) {
               int wid = cm.getClient().getWorldServer().getRelationshipId(cm.getPlayer().getId())
               Channel channel = cm.getClient().getChannelServer()

               if (channel.isWeddingReserved(wid)) {
                  if (wid == channel.getOngoingWedding(cathedralWedding)) {
                     MapleCharacter partner = channel.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
                     if (!(partner == null || cm.getMap() != partner.getMap())) {
                        if (!cm.canHold(4000313)) {
                           cm.sendOk(I18nMessage.from("9201002_ETC_SPACE_NEEDED"))

                           cm.dispose()
                           return
                        } else if (!partner.canHold(4000313)) {
                           cm.sendOk(I18nMessage.from("9201002_PARTNER_NEEDS_ETC_SPACE"))

                           cm.dispose()
                           return
                        } else if (!isSuitedForWedding(cm.getPlayer(), false)) {
                           cm.sendOk(I18nMessage.from("9201002_PURCHASE_GARMENT"))

                           cm.dispose()
                           return
                        } else if (!isSuitedForWedding(partner, false)) {
                           cm.sendOk(I18nMessage.from("9201002_PARTNER_GARMENT_PURCHASE"))

                           cm.dispose()
                           return
                        }

                        cm.sendOk(I18nMessage.from("9201002_VERY_WELL_LONG"))

                     } else {
                        cm.sendOk(I18nMessage.from("9201002_MISSING_PARTNER"))

                        cm.dispose()
                     }
                  } else {
                     String placeTime = channel.getWeddingReservationTimeLeft(wid)

                     cm.sendOk(I18nMessage.from("9201002_HAVE_PATIENCE").with(placeTime))

                     cm.dispose()
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9201002_NO_RESERVATIONS_MADE"))

                  cm.dispose()
               }
            } else if (status == 2) {
               Channel channel = cm.getClient().getChannelServer()
               boolean weddingType = channel.getOngoingWeddingType(cathedralWedding)

               MapleCharacter partner = channel.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
               if (!(partner == null || cm.getMap() != partner.getMap())) {
                  if (channel.acceptOngoingWedding(cathedralWedding)) {
                     int wid = cm.getClient().getWorldServer().getRelationshipId(cm.getPlayer().getId())
                     if (wid > 0) {
                        EventManager em = cm.getEventManager(weddingEventName)
                        if (em.startInstance(cm.getPlayer())) {
                           eim = getMarriageInstance(cm.getPlayer())
                           if (eim != null) {
                              eim.setIntProperty("weddingId", wid)
                              eim.setIntProperty("groomId", cm.getPlayer().getId())
                              eim.setIntProperty("brideId", cm.getPlayer().getPartnerId())
                              eim.setIntProperty("isPremium", weddingType ? 1 : 0)

                              eim.registerPlayer(partner)
                           } else {
                              cm.sendOk(I18nMessage.from("9201002_UNEXPECTED_ERROR"))

                           }

                           cm.dispose()
                        } else {
                           cm.sendOk(I18nMessage.from("9201002_UNEXPECTED_ERROR_BEFORE_PREPARATIONS"))

                           cm.dispose()
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9201002_UNEXPECTED_ERROR_BEFORE_PREPARATIONS"))

                        cm.dispose()
                     }
                  } else {    // partner already decided to start
                     cm.dispose()
                  }
               } else {
                  cm.sendOk(I18nMessage.from("9201002_MISSING_PARTNER"))

                  cm.dispose()
               }
            }
         } else {
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
                     cm.sendYesNo(I18nMessage.from("9201002_VERY_WELL"))

                     state = 1
                  } else if (weddingStage == 1) {
                     cm.sendOk(I18nMessage.from("9201002_TIME_OF_HAPPINESS"))

                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("9201002_CONGRATULATIONS"))

                     cm.dispose()
                  }
               } else {
                  int weddingStage = eim.getIntProperty("weddingStage")
                  if (weddingStage == 1) {
                     if (eim.gridCheck(cm.getPlayer()) != -1) {
                        cm.sendOk(I18nMessage.from("9201002_GIVE_YOUR_BLESSINGS"))

                        cm.dispose()
                     } else {
                        if (eim.getIntProperty("guestBlessings") == 1) {
                           cm.sendYesNo(I18nMessage.from("9201002_DO_YOU_WANT_TO_BLESS"))

                           state = 0
                        } else {
                           cm.sendOk(I18nMessage.from("9201002_TODAY_WE_ARE_GATHERED"))

                           cm.dispose()
                        }
                     }
                  } else if (weddingStage == 3) {
                     cm.sendOk(I18nMessage.from("9201002_NOW_MARRIED"))

                     cm.dispose()
                  } else {
                     cm.sendOk(I18nMessage.from("9201002_BLESSING_TIME_ENDED"))

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

                  cm.sendOk(I18nMessage.from("9201002_WHAT_A_NOBLE_ACT"))

                  cm.dispose()
               } else {            // couple wants to complete the wedding
                  int weddingStage = eim.getIntProperty("weddingStage")

                  if (weddingStage == 2) {
                     int pid = cm.getPlayer().getPartnerId()
                     if (pid <= 0) {
                        cm.sendOk(I18nMessage.from("9201002_NO_LONGER_ENGAGED"))

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
                                    cm.sendOk(I18nMessage.from("9201002_ALREADY_CONFIRMED"))

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

                                    MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_SUCCESS"))
                                    eim.schedule("showMarriedMsg", 2 * 1000)
                                 }
                              } else {
                                 eim.setIntProperty("confirmedVows", player.getId())
                                 MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_ONE_LAST_STEP").with(player.getName()))
                              }

                              break

                           case -1:
                              cm.sendOk(I18nMessage.from("9201002_MISSING_RING_BOX"))

                              break

                           case -2:
                              cm.sendOk(I18nMessage.from("9201002_PARTNER_MISSING_RING_BOX"))

                              break

                           case -3:
                              cm.sendOk(I18nMessage.from("9201002_CANNOT_MARRY_YOU_WITHOUT_THAT_ITEM"))

                              break

                           case -4:
                              cm.sendOk(I18nMessage.from("9201002_GARMENTS_ARE_ESSENTIAL"))

                              break

                           case 1:
                              cm.sendOk(I18nMessage.from("9201002_MAKE_EQUIP_SPACE"))

                              break

                           case 2:
                              cm.sendOk(I18nMessage.from("9201002_PARTNER_MAKE_EQUIP_SPACE"))

                              break

                           case 3:
                              cm.sendOk(I18nMessage.from("9201002_PARTNER_CANNOT_MARRY_YOU_WITHOUT_THAT_ITEM"))

                              break

                           case 4:
                              cm.sendOk(I18nMessage.from("9201002_PARTNER_NOT_PROPERLY_DRESSED"))

                              break
                        }

                        cm.dispose()
                     } else {
                        cm.sendOk(I18nMessage.from("9201002_PARTNER_IS_NOT_HERE"))

                        cm.dispose()
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("9201002_NOW_HUSBAND_AND_WIFE"))

                     cm.dispose()
                  }
               }
            }
         }
      }
   }
}

NPC9201002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201002(cm: cm))
   }
   return (NPC9201002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }