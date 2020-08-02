package npc

import client.MapleCharacter
import net.server.channel.Channel
import net.server.channel.processor.WeddingProcessor
import net.server.world.World
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int wid
   boolean isMarrying

   boolean cathedralWedding = true
   String weddingEventName = "WeddingCathedral"
   int weddingEntryTicketCommon = 5251000
   int weddingEntryTicketPremium = 5251003
   int weddingSendTicket = 4031395
   int weddingGuestTicket = 4031407
   int weddingAltarMapId = 680000210
   boolean weddingIndoors

   def start() {
      weddingIndoors = isWeddingIndoors(cm.getMapId())
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   static def isWeddingIndoors(mapId) {
      return mapId >= 680000100 && mapId <= 680000500
   }

   static def hasSuitForWedding(MapleCharacter player) {
      int baseId = (player.getGender() == 0) ? 1050131 : 1051150

      for (int i = 0; i < 4; i++) {
         if (player.haveItemWithId(baseId + i, true)) {
            return true
         }
      }

      return false
   }

   def getMarriageInstance(int weddingId) {
      EventManager em = cm.getEventManager(weddingEventName)

      for (Iterator<EventInstanceManager> iterator = em.getInstances().iterator(); iterator.hasNext();) {
         EventInstanceManager eim = iterator.next()

         if (eim.getIntProperty("weddingId") == weddingId) {
            return eim
         }
      }

      return null
   }

   static def hasWeddingRing(MapleCharacter player) {
      int[] rings = [1112806, 1112803, 1112807, 1112809]
      for (int i = 0; i < rings.length; i++) {
         if (player.haveItemWithId(rings[i], true)) {
            return true
         }
      }

      return false
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
            boolean hasEngagement = false
            for (int x = 4031357; x <= 4031364; x++) {
               if (cm.haveItem(x, 1)) {
                  hasEngagement = true
                  break
               }
            }

            if (status == 0) {
               String text = "Welcome to the #bCathedral#k! How can I help you?"
               String[] choice = ["How do I prepare a wedding?", "I have an engagement and want to arrange the wedding", "I am the guest and I'd like to go into the wedding"]
               for (int x = 0; x < choice.length; x++) {
                  text += "\r\n#L" + x + "##b" + choice[x] + "#l"
               }

               if (cm.haveItem(5251100)) {
                  text += "\r\n#L" + x + "##bMake additional invitation cards#l"
               }

               cm.sendSimple(text)
            } else if (status == 1) {
               World world = cm.getClient().getWorldServer()
               Channel channel = cm.getClient().getChannelServer()
               switch (selection) {
                  case 0:
                     cm.sendOk(I18nMessage.from("9201005_ENGAGE_INFO").with(weddingEntryTicketCommon))
                     cm.dispose()
                     break
                  case 1:
                     if (hasEngagement) {
                        int weddingId = world.getRelationshipId(cm.getPlayer().getId())
                        if (weddingId > 0) {
                           if (channel.isWeddingReserved(weddingId)) {    // registration check
                              String placeTime = channel.getWeddingReservationTimeLeft(weddingId)
                              cm.sendOk(I18nMessage.from("9201005_WEDDING_START_TIME").with(placeTime))
                           } else {
                              MapleCharacter partner = world.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
                              if (partner == null) {
                                 cm.sendOk(I18nMessage.from("9201005_PARTNER_OFFLINE"))
                                 cm.dispose()
                                 return
                              }

                              if (hasWeddingRing(cm.getPlayer()) || hasWeddingRing(partner)) {
                                 cm.sendOk(I18nMessage.from("9201005_YOU_OR_YOUR_PARTNER_ALREADY_MARRIED"))
                                 cm.dispose()
                                 return
                              }

                              if (cm.getMap() != partner.getMap()) {
                                 cm.sendOk(I18nMessage.from("9201005_PARTNER_MUST_REGISTER"))
                                 cm.dispose()
                                 return
                              }

                              if (!cm.canHold(weddingSendTicket, 15) || !partner.canHold(weddingSendTicket, 15)) {
                                 cm.sendOk(I18nMessage.from("9201005_YOU_OR_PARTNER_NEED_FREE_ETC_SPACE"))
                                 cm.dispose()
                                 return
                              }

                              if (!cm.getUnclaimedMarriageGifts().isEmpty() || !partner.getAbstractPlayerInteraction().getUnclaimedMarriageGifts().isEmpty()) {
                                 cm.sendOk(I18nMessage.from("9201005_SOMETHING_DOES_NOT_SEEM_RIGHT"))
                                 cm.dispose()
                                 return
                              }

                              boolean hasCommon = cm.haveItem(weddingEntryTicketCommon)
                              boolean hasPremium = cm.haveItem(weddingEntryTicketPremium)

                              if (hasCommon || hasPremium) {
                                 boolean weddingType = hasPremium

                                 MapleCharacter player = cm.getPlayer()
                                 int resStatus = channel.pushWeddingReservation(weddingId, cathedralWedding, weddingType, player.getId(), player.getPartnerId())
                                 if (resStatus > 0) {
                                    cm.gainItem((weddingType) ? weddingEntryTicketPremium : weddingEntryTicketCommon, (short) -1)

                                    long expirationTime = WeddingProcessor.getInstance().getRelativeWeddingTicketExpireTime(resStatus)
                                    cm.gainItem(weddingSendTicket, (short) 15, false, true, expirationTime)
                                    partner.getAbstractPlayerInteraction().gainItem(weddingSendTicket, (short) 15, false, true, expirationTime)

                                    String placeTime = channel.getWeddingReservationTimeLeft(weddingId)

                                    String wedType = weddingType ? "Premium" : "Regular"
                                    cm.sendOk(I18nMessage.from("9201005_BOTH_RECEIVED_TICKETS").with(wedType, placeTime))

                                    MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_ASSISTANT").with(wedType, placeTime))
                                    MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MARRIAGE_WEDDING_ASSISTANT").with(wedType, placeTime))

                                    if (!hasSuitForWedding(player)) {
                                       MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_WEDDING_ASSISTANT_GARMENT"))
                                    }

                                    if (!hasSuitForWedding(partner)) {
                                       MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, I18nMessage.from("MARRIAGE_WEDDING_ASSISTANT_GARMENT"))
                                    }
                                 } else {
                                    cm.sendOk(I18nMessage.from("9201005_PLEASE_TRY_AGAIN_LATER"))
                                 }
                              } else {
                                 cm.sendOk(I18nMessage.from("9201005_BEFORE_TRYING_TO_REGISTER").with(weddingEntryTicketCommon))
                              }
                           }
                        } else {
                           cm.sendOk(I18nMessage.from("9201005_ENCOUNTERED_ERROR_DURING_RESERVATION"))
                        }

                        cm.dispose()
                     } else {
                        cm.sendOk(I18nMessage.from("9201005_NEED_ENGAGEMENT_RING"))
                        cm.dispose()
                     }
                     break

                  case 2:
                     if (cm.haveItem(weddingGuestTicket)) {
                        wid = channel.getOngoingWedding(cathedralWedding)
                        if (wid > 0) {
                           if (channel.isOngoingWeddingGuest(cathedralWedding, cm.getPlayer().getId())) {
                              EventInstanceManager eim = getMarriageInstance(wid)
                              if (eim != null) {
                                 cm.sendOk(I18nMessage.from("9201005_ENJOY_THE_WEDDING"))
                              } else {
                                 cm.sendOk(I18nMessage.from("9201005_PLEASE_WAIT_A_MOMENT"))
                                 cm.dispose()
                              }
                           } else {
                              cm.sendOk(I18nMessage.from("9201005_YOU_ARE_NOT_INVITED"))
                              cm.dispose()
                           }
                        } else {
                           cm.sendOk(I18nMessage.from("9201005_NO_WEDDING_BOOKED"))
                           cm.dispose()
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9201005_YOU_NEED_TICKET").with(weddingGuestTicket))
                        cm.dispose()
                     }
                     break
                  default:
                     int weddingId = world.getRelationshipId(cm.getPlayer().getId())
                     int resStatus = channel.getWeddingReservationStatus(weddingId, cathedralWedding)
                     if (resStatus > 0) {
                        if (cm.canHold(weddingSendTicket, 3)) {
                           cm.gainItem(5251100, (short) -1)

                           long expirationTime = WeddingProcessor.getInstance().getRelativeWeddingTicketExpireTime(resStatus)
                           cm.gainItem(weddingSendTicket, (short) 3, false, true, expirationTime)
                        } else {
                           cm.sendOk(I18nMessage.from("9201005_NEED_ETC_SPACE"))
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("9201005_NOT_CURRENTLY_BOOKED"))
                     }

                     cm.dispose()
               }
            } else if (status == 2) {   // registering guest
               EventInstanceManager eim = getMarriageInstance(wid)

               if (eim != null) {
                  cm.gainItem(weddingGuestTicket, (short) -1)
                  eim.registerPlayer(cm.getPlayer())     //cm.warp(680000210, 0);
               } else {
                  cm.sendOk(I18nMessage.from("9201005_MARRIAGE_COULD_NOT_BE_FOUND"))
               }

               cm.dispose()
            }
         } else {
            if (status == 0) {
               EventInstanceManager eim = cm.getEventInstance()
               if (eim == null) {
                  cm.warp(680000000, 0)
                  cm.dispose()
                  return
               }

               isMarrying = (cm.getPlayer().getId() == eim.getIntProperty("groomId") || cm.getPlayer().getId() == eim.getIntProperty("brideId"))

               if (eim.getIntProperty("weddingStage") == 0) {
                  if (!isMarrying) {
                     cm.sendOk(I18nMessage.from("9201005_WELCOME").with(cm.getMapId()))
                  } else {
                     cm.sendOk(I18nMessage.from("9201005_WELCOME_SHORT").with(cm.getMapId()))
                  }

                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("9201005_BRIDE_AND_GROOM_READY"))
               }
            } else if (status == 1) {
               cm.warp(weddingAltarMapId, "sp")
               cm.dispose()
            }
         }

      }
   }
}

NPC9201005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201005(cm: cm))
   }
   return (NPC9201005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }