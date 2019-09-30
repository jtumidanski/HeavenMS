package npc

import client.MapleCharacter
import constants.ServerConstants
import net.server.channel.Channel
import net.server.channel.handlers.RingActionHandler
import scripting.AbstractPlayerInteraction
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.MaplePacketCreator
import tools.MasterBroadcaster
import tools.MessageBroadcaster
import tools.PacketCreator
import tools.ServerNoticeType
import tools.packet.foreigneffect.ShowForeignEffect

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
   int weddingBlessingExp = ServerConstants.WEDDING_BLESS_EXP

   static def isWeddingIndoors(int mapid) {
      return mapid >= 680000100 && mapid <= 680000500
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

   static def detectPlayerItemid(MapleCharacter player) {
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
      int baseid = (player.getGender() == 0) ? 1050131 : 1051150

      if (equipped) {
         for (int i = 0; i < 4; i++) {
            if (player.haveItemEquipped(baseid + i)) {
               return true
            }
         }
      } else {
         for (int i = 0; i < 4; i++) {
            if (player.haveItemWithId(baseid + i, true)) {
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
                  cm.sendOk("Hmm, today two fluttering hearts are about to be joined together by the blessings of love!")
                  cm.dispose()
               }
            } else if (status == 1) {
               int wid = cm.getClient().getWorldServer().getRelationshipId(cm.getPlayer().getId())
               Channel cserv = cm.getClient().getChannelServer()

               if (cserv.isWeddingReserved(wid)) {
                  if (wid == cserv.getOngoingWedding(cathedralWedding)) {
                     MapleCharacter partner = cserv.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
                     if (!(partner == null || cm.getMap() != partner.getMap())) {
                        if (!cm.canHold(4000313)) {
                           cm.sendOk("Please have a free ETC slot available to get the #b#t4000313##k.")
                           cm.dispose()
                           return
                        } else if (!partner.canHold(4000313)) {
                           cm.sendOk("Please let your partner know they must have a free ETC slot available to get the #b#t4000313##k.")
                           cm.dispose()
                           return
                        } else if (!isSuitedForWedding(cm.getPlayer(), false)) {
                           cm.sendOk("Please purchase a #rwedding garment#k for the ceremony, quickly! Without it I am not able to marry you.")
                           cm.dispose()
                           return
                        } else if (!isSuitedForWedding(partner, false)) {
                           cm.sendOk("Please let your partner know they must have a #rwedding garment#k ready for the ceremony.")
                           cm.dispose()
                           return
                        }

                        cm.sendOk("Very well, the preparatives here are finished too. This indeed is a beautiful day, you two are truly blessed to marry on such a day. Let us begin the marriage!!")
                     } else {
                        cm.sendOk("Hmm, it seems your partner is elsewhere... Please let them come here before starting the ceremony.")
                        cm.dispose()
                     }
                  } else {
                     String placeTime = cserv.getWeddingReservationTimeLeft(wid)

                     cm.sendOk("Have patience. Your wedding is set to happen at the #r" + placeTime + "#k. Don't forget the wedding garment.")
                     cm.dispose()
                  }
               } else {
                  cm.sendOk("Hmm, I'm sorry but there are no reservations made for you at this channel for the time being.")
                  cm.dispose()
               }
            } else if (status == 2) {
               Channel cserv = cm.getClient().getChannelServer()
               boolean wtype = cserv.getOngoingWeddingType(cathedralWedding)

               MapleCharacter partner = cserv.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
               if (!(partner == null || cm.getMap() != partner.getMap())) {
                  if (cserv.acceptOngoingWedding(cathedralWedding)) {
                     int wid = cm.getClient().getWorldServer().getRelationshipId(cm.getPlayer().getId())
                     if (wid > 0) {
                        EventManager em = cm.getEventManager(weddingEventName)
                        if (em.startInstance(cm.getPlayer())) {
                           eim = getMarriageInstance(cm.getPlayer())
                           if (eim != null) {
                              eim.setIntProperty("weddingId", wid)
                              eim.setIntProperty("groomId", cm.getPlayer().getId())
                              eim.setIntProperty("brideId", cm.getPlayer().getPartnerId())
                              eim.setIntProperty("isPremium", wtype ? 1 : 0)

                              eim.registerPlayer(partner)
                           } else {
                              cm.sendOk("An unexpected error happened when locating the wedding event. Please try again later.")
                           }

                           cm.dispose()
                        } else {
                           cm.sendOk("An unexpected error happened before the wedding preparations. Please try again later.")
                           cm.dispose()
                        }
                     } else {
                        cm.sendOk("An unexpected error happened before the wedding preparations. Please try again later.")
                        cm.dispose()
                     }
                  } else {    // partner already decided to start
                     cm.dispose()
                  }
               } else {
                  cm.sendOk("Hmm, it seems your partner is elsewhere... Please let them come here before starting the ceremony.")
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
                  int wstg = eim.getIntProperty("weddingStage")

                  if (wstg == 2) {
                     cm.sendYesNo("Very well, the guests has bestowed all their blessings to you now. The time has come, #rshould I make you Husband and Wife#k?")
                     state = 1
                  } else if (wstg == 1) {
                     cm.sendOk("While you two are making your wedding vows to each other, your guests are currently giving their blessings to you. This is a time of happiness for both of you, please rejoice the ceremony.")
                     cm.dispose()
                  } else {
                     cm.sendOk("Congratulations on your wedding! Our ceremony is complete, you can head to #b#p9201007##k now, she will lead you and your guests to the afterparty. Cheers for your love!")
                     cm.dispose()
                  }
               } else {
                  int wstg = eim.getIntProperty("weddingStage")
                  if (wstg == 1) {
                     if (eim.gridCheck(cm.getPlayer()) != -1) {
                        cm.sendOk("Everyone give your blessings to this lovely couple!")
                        cm.dispose()
                     } else {
                        if (eim.getIntProperty("guestBlessings") == 1) {
                           cm.sendYesNo("Do you want to bless this couple?")
                           state = 0
                        } else {
                           cm.sendOk("Today we are gathered here to reunite this lively couple in matrimony!")
                           cm.dispose()
                        }
                     }
                  } else if (wstg == 3) {
                     cm.sendOk("The two loving birds are now married. What a lively day! Please #rget ready for the afterparty#k, it should start soon. Follow the married couple's lead.")
                     cm.dispose()
                  } else {
                     cm.sendOk("The guest's blessing time has ended. Hang on, the couple will renew their vows very soon now. What a sight to see!")
                     cm.dispose()
                  }
               }
            } else if (status == 1) {
               if (state == 0) {    // give player blessings
                  eim.gridInsert(cm.getPlayer(), 1)

                  if (ServerConstants.WEDDING_BLESSER_SHOWFX) {
                     MapleCharacter target = cm.getPlayer()
                     target.announce(MaplePacketCreator.showSpecialEffect(9))
                     MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), { character -> PacketCreator.create(new ShowForeignEffect(target.getId(), 9)) }, false, target)
                  } else {
                     MapleCharacter target = eim.getPlayerById(eim.getIntProperty("groomId"))
                     target.announce(MaplePacketCreator.showSpecialEffect(9))
                     MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), { character -> PacketCreator.create(new ShowForeignEffect(target.getId(), 9)) }, false, target)

                     target = eim.getPlayerById(eim.getIntProperty("brideId"))
                     target.announce(MaplePacketCreator.showSpecialEffect(9))
                     MasterBroadcaster.getInstance().sendToAllInMap(target.getMap(), { character -> PacketCreator.create(new ShowForeignEffect(target.getId(), 9)) }, false, target)
                  }

                  cm.sendOk("Your blessings have been added to their love. What a noble act for a lovely couple!")
                  cm.dispose()
               } else {            // couple wants to complete the wedding
                  int wstg = eim.getIntProperty("weddingStage")

                  if (wstg == 2) {
                     int pid = cm.getPlayer().getPartnerId()
                     if (pid <= 0) {
                        cm.sendOk("It seems you are no longer engaged to your partner, just before the altar... Where did all that happiness you two had sported a while ago went?")
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

                                    int playerItemId = detectPlayerItemid(player)
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

                                    MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, "High Priest John: By the power vested in me through the mighty Maple tree, I now pronounce you  Husband and Wife. You may kiss the bride!")
                                    eim.schedule("showMarriedMsg", 2 * 1000)
                                 }
                              } else {
                                 eim.setIntProperty("confirmedVows", player.getId())
                                 MessageBroadcaster.getInstance().sendMapServerNotice(cm.getMap(), ServerNoticeType.LIGHT_BLUE, "Wedding Assistant: " + player.getName() + " has confirmed vows! Alright, one step away to make it official. Tighten your seatbelts!")
                              }

                              break

                           case -1:
                              cm.sendOk("It seems you no longer have the ring/ring box you and your partner shared at the engagement time. Sorry, but that was needed for the wedding...")
                              break

                           case -2:
                              cm.sendOk("It seems your partner no longer has the ring/ring box you two shared at the engagement time. Sorry, but that was needed for the wedding...")
                              break

                           case -3:
                              cm.sendOk("It seems you don't have the #r#t4000313##k given at the entrance... Please find it, I can't marry you without that item in hands.")
                              break

                           case -4:
                              cm.sendOk("Pardon my rudiness, but the garments are a essential part of the ceremony. Please #rsuit yourself properly#k for a wedding.")
                              break

                           case 1:
                              cm.sendOk("Please make an EQUIP slot available to get the marriage ring, will you?")
                              break

                           case 2:
                              cm.sendOk("Please let your partner know to make an EQUIP slot available to get the marriage ring, will you?")
                              break

                           case 3:
                              cm.sendOk("It seems your partner don't have the #r#t4000313##k given at the entrance... Please find it, I can't marry you without that item in hands.")
                              break

                           case 4:
                              cm.sendOk("It seems your partner is not properly dressed for the wedding... Pardon my rudiness, but the garments are a essential part of the ceremony.")
                              break
                        }

                        cm.dispose()
                     } else {
                        cm.sendOk("Hmm, it seems your partner is not here, before the altar... It is a pity, but I can't fulfill the wedding if your partner is not here.")
                        cm.dispose()
                     }
                  } else {
                     cm.sendOk("You are now #bhusband and wife#k. Congratulations!")
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