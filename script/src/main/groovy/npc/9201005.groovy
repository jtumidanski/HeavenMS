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
                     cm.sendOk("Firstly you need to be #bengaged#k to someone. #p9201000# makes the engagement ring. Once attained the engagement status, purchase a #b#t" + weddingEntryTicketCommon + "##k.\r\nShow me your engagement ring and a wedding ticket, and I will book a reservation for you along with #r15 Wedding Tickets#k. Use them to invite your guests into the wedding. They need 1 each to enter.")
                     cm.dispose()
                     break
                  case 1:
                     if (hasEngagement) {
                        int weddingId = world.getRelationshipId(cm.getPlayer().getId())
                        if (weddingId > 0) {
                           if (channel.isWeddingReserved(weddingId)) {    // registration check
                              String placeTime = channel.getWeddingReservationTimeLeft(weddingId)
                              cm.sendOk("Your wedding is set to start at the #r" + placeTime + "#k. Get formally dressed and don't be late!")
                           } else {
                              MapleCharacter partner = world.getPlayerStorage().getCharacterById(cm.getPlayer().getPartnerId()).get()
                              if (partner == null) {
                                 cm.sendOk("Your partner seems to be offline right now... Make sure to get both gathered here when the time comes!")
                                 cm.dispose()
                                 return
                              }

                              if (hasWeddingRing(cm.getPlayer()) || hasWeddingRing(partner)) {
                                 cm.sendOk("Either you or your partner already has a marriage ring.")
                                 cm.dispose()
                                 return
                              }

                              if (cm.getMap() != partner.getMap()) {
                                 cm.sendOk("Please let your partner come here as well to register the reservation.")
                                 cm.dispose()
                                 return
                              }

                              if (!cm.canHold(weddingSendTicket, 15) || !partner.canHold(weddingSendTicket, 15)) {
                                 cm.sendOk("Either you or your partner doesn't have a free ETC slot for the Wedding tickets! Please make some room before trying to register a reservation.")
                                 cm.dispose()
                                 return
                              }

                              if (!cm.getUnclaimedMarriageGifts().isEmpty() || !partner.getAbstractPlayerInteraction().getUnclaimedMarriageGifts().isEmpty()) {
                                 cm.sendOk("Eerhm... I'm sorry, something doesn't seem right according to the Amoria's Wedding Gift Registry reserve. Please check in the situation with #b#p9201014##k.")
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
                                    cm.sendOk("You both have received 15 Wedding Tickets, to be given to your guests. #bDouble-click the ticket#k to send it to someone. Invitations can only be sent #rbefore the wedding start time#k. Your #b" + wedType + " wedding#k is set to start at the #r" + placeTime + "#k. Get formally dressed and don't be late!")

                                    MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Wedding Assistant: You both have received 15 Wedding Tickets. Invitations can only be sent before the wedding start time. Your " + wedType + " wedding is set to start at the " + placeTime + ". Get dressed and don't be late!")
                                    MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.LIGHT_BLUE, "Wedding Assistant: You both have received 15 Wedding Tickets. Invitations can only be sent before the wedding start time. Your " + wedType + " wedding is set to start at the " + placeTime + ". Get dressed and don't be late!")

                                    if (!hasSuitForWedding(player)) {
                                       MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Wedding Assistant: Please purchase a wedding garment before showing up for the ceremony. One can be bought at the Wedding Shop left-most Amoria.")
                                    }

                                    if (!hasSuitForWedding(partner)) {
                                       MessageBroadcaster.getInstance().sendServerNotice(partner, ServerNoticeType.PINK_TEXT, "Wedding Assistant: Please purchase a wedding garment before showing up for the ceremony. One can be bought at the Wedding Shop left-most Amoria.")
                                    }
                                 } else {
                                    cm.sendOk("Your wedding reservation must have been processed recently. Please try again later.")
                                 }
                              } else {
                                 cm.sendOk("Please have a #b#t" + weddingEntryTicketCommon + "##k available on your CASH inventory before trying to register a reservation.")
                              }
                           }
                        } else {
                           cm.sendOk("Wedding reservation encountered an error, try again later.")
                        }

                        cm.dispose()
                     } else {
                        cm.sendOk("You do not have an engagement ring.")
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
                                 cm.sendOk("Enjoy the wedding. Don't drop your Gold Maple Leaf or you won't be able to finish the whole wedding.")
                              } else {
                                 cm.sendOk("Please wait a moment while the couple get ready to enter the Cathedral.")
                                 cm.dispose()
                              }
                           } else {
                              cm.sendOk("Sorry, but you have not been invited for this wedding.")
                              cm.dispose()
                           }
                        } else {
                           cm.sendOk("There is no wedding booked right now.")
                           cm.dispose()
                        }
                     } else {
                        cm.sendOk("You do not have a #b#t" + weddingGuestTicket + "##k.")
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
                           cm.sendOk("Please have a free ETC slot available to get more invitations.")
                        }
                     } else {
                        cm.sendOk("You're not currently booked on the Cathedral to make additional invitations.")
                     }

                     cm.dispose()
               }
            } else if (status == 2) {   // registering guest
               EventInstanceManager eim = getMarriageInstance(wid)

               if (eim != null) {
                  cm.gainItem(weddingGuestTicket, (short) -1)
                  eim.registerPlayer(cm.getPlayer())     //cm.warp(680000210, 0);
               } else {
                  cm.sendOk("The marriage event could not be found.")
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
                     cm.sendOk("Welcome to the #b#m" + cm.getMapId() + "##k. Please hang around with the groom and bride while the other guests are gathering here.\r\n\r\nWhen the timer reach it's end the couple will head to the altar, at that time you will be allowed to root over them from the #bguests area#k.")
                  } else {
                     cm.sendOk("Welcome to the #b#m" + cm.getMapId() + "##k. Please greet the guests that are already here while the others are coming. When the timer reach it's end the couple will head to the altar.")
                  }

                  cm.dispose()
               } else {
                  cm.sendYesNo("The #bbride and groom#k are already on their way to the altar. Would you like to join them now?")
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