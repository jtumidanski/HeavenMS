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
                  cm.sendYesNo("Awhoooooooooosh~, the guests have proclaimed their love to y'all. The time has come baby~, #rshould I make you Husband and Wife#k?")
                  state = 1
               } else if (weddingStage == 1) {
                  cm.sendOk("W-whoah wait a bit alright? Your guests are currently giving their love to y'all. Let's shake this place up, baby~~.")
                  cm.dispose()
               } else {
                  cm.sendOk("Wheeeeeeeeeeeeeew! Our festival here is now complete, give a sweet talk to #b#p9201009##k, she will lead you and your folks to the after party. Cheers for your love!")
                  cm.dispose()
               }
            } else {
               int weddingStage = eim.getIntProperty("weddingStage")
               if (weddingStage == 1) {
                  if (eim.gridCheck(cm.getPlayer()) != -1) {
                     cm.sendOk("Everyone let's shake this place up! Let's rock 'n' roll!!")
                     cm.dispose()
                  } else {
                     if (eim.getIntProperty("guestBlessings") == 1) {
                        cm.sendYesNo("Will you manifest your love to the superstars here present?")
                        state = 0
                     } else {
                        cm.sendOk("Our superstars are gathered down here. Everyone, let's give them some nice, nicey party~!")
                        cm.dispose()
                     }
                  }
               } else if (weddingStage == 3) {
                  cm.sendOk("Whooooooo-hoo! The couple's love now are like one super big shiny heart right now! And it shall go on ever after this festival. Please #rget ready for the after party#k, baby~. Follow the married couple's lead!")
                  cm.dispose()
               } else {
                  cm.sendOk("It's now guys... Stay with your eyes and ears keened up! They are about to smooch it all over the place!!!")
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

               cm.sendOk("Way to go, my friend! Your LOVE has been added to theirs, now in one bigger heart-shaped sentiment that will remain lively in our hearts forever! Who-hoo~!")
               cm.dispose()
            } else {            // couple wants to complete the wedding
               int weddingStage = eim.getIntProperty("weddingStage")

               if (weddingStage == 2) {
                  int pid = cm.getPlayer().getPartnerId()
                  if (pid <= 0) {
                     cm.sendOk("Huh~.... Wait wait, did you just break that thing you had right now?? Oh my, what happened?")
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
                                 cm.sendOk("You have already confirmed your vows. All that is left is for your partner to confirm now.")
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
                           cm.sendOk("Well, it seems you no longer have the ring/ring box you guys exchanged at the engagement. Awww man~")
                           break

                        case -2:
                           cm.sendOk("Well, it seems your partner no longer has the ring/ring box you guys exchanged at the engagement. Awww man~")
                           break

                        case -3:
                           cm.sendOk("Well, it seems you don't have the #r#t4000313##k given at the entrance... Please find it, baby~")
                           break

                        case -4:
                           cm.sendOk("Aww I know that shucks, but the fashionable wedding clothes does a essential part here. Please wear it before talking to me.")
                           break

                        case 1:
                           cm.sendOk("Please make an EQUIP slot available to get the marriage ring, will you?")
                           break

                        case 2:
                           cm.sendOk("Please let your partner know to make an EQUIP slot available to get the marriage ring, will you?")
                           break

                        case 3:
                           cm.sendOk("Well, it seems your partner don't have the #r#t4000313##k given at the entrance... Please find it, I can't call the finally without it.")
                           break

                        case 4:
                           cm.sendOk("Aww I know that shucks, but it seems your partner is not using the fashionable wedding clothes. Please tell them to wear it before talking to me.")
                           break
                     }

                     cm.dispose()
                  } else {
                     cm.sendOk("Oof, is that it that your partner is not here, right now? ... Oh noes, I'm afraid I can't call the finally if your partner is not here.")
                     cm.dispose()
                  }
               } else {
                  cm.sendOk("Wheeeeeeeeeeeeew~ You are now #bofficially one couple#k, and a brilliant one. Your moves fitted in outstandingly, congratulations!")
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