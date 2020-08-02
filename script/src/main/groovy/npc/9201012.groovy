package npc
import tools.I18nMessage

import client.MapleCharacter
import net.server.channel.Channel
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201012 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int state
   EventInstanceManager eim
   String weddingEventName = "WeddingChapel"
   boolean cathedralWedding = false

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
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
            boolean hasEngagement = false
            for (int x = 4031357; x <= 4031364; x++) {
               if (cm.haveItem(x, 1)) {
                  hasEngagement = true
                  break
               }
            }

            if (hasEngagement) {
               String text = "Hi there. How about skyrocket the day with your fiancee baby~?"
               String[] choice = ["We're ready to get married."]
               for (int x = 0; x < choice.length; x++) {
                  text += "\r\n#L" + x + "##b" + choice[x] + "#l"
               }
               cm.sendSimple(text)
            } else {
               cm.sendOk(I18nMessage.from("9201012_HELLO"))
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
                        cm.sendOk(I18nMessage.from("9201012_NEED_ETC_SPACE"))
                        cm.dispose()
                        return
                     } else if (!partner.canHold(4000313)) {
                        cm.sendOk(I18nMessage.from("9201012_PARTNER_NEED_ETC_SPACE"))
                        cm.dispose()
                        return
                     } else if (!isSuitedForWedding(cm.getPlayer(), false)) {
                        cm.sendOk(I18nMessage.from("9201012_FASHIONABLE_CLOTHES"))
                        cm.dispose()
                        return
                     } else if (!isSuitedForWedding(partner, false)) {
                        cm.sendOk(I18nMessage.from("9201012_PARTNER_FASHIONABLE_CLOTHES"))
                        cm.dispose()
                        return
                     }

                     cm.sendOk(I18nMessage.from("9201012_ALRIGHT"))
                  } else {
                     cm.sendOk(I18nMessage.from("9201012_PARTNER_ELSEWHERE"))
                     cm.dispose()
                  }
               } else {
                  String placeTime = channel.getWeddingReservationTimeLeft(wid)

                  cm.sendOk(I18nMessage.from("9201012_WEDDING_TIME").with(placeTime))
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("9201012_NO_RESERVATIONS"))
               cm.dispose()
            }
         } else if (status == 2) {
            Channel channel = cm.getClient().getChannelServer()
            boolean weddingIsOnGoing = channel.getOngoingWeddingType(cathedralWedding)

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
                           eim.setIntProperty("isPremium", weddingIsOnGoing ? 1 : 0)

                           eim.registerPlayer(partner)
                        } else {
                           cm.sendOk(I18nMessage.from("9201012_UNEXPECTED_ERROR"))
                        }

                        cm.dispose()
                     } else {
                        cm.sendOk(I18nMessage.from("9201012_UNEXPECTED_ERROR_DURING_PREPARATIONS"))
                        cm.dispose()
                     }
                  } else {
                     cm.sendOk(I18nMessage.from("9201012_UNEXPECTED_ERROR_DURING_PREPARATIONS"))
                     cm.dispose()
                  }
               } else {    // partner already decided to start
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("9201012_PARTNER_ELSEWHERE"))
               cm.dispose()
            }
         }
      }
   }
}

NPC9201012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201012(cm: cm))
   }
   return (NPC9201012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }