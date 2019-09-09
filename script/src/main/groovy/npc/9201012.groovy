package npc

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
               cm.sendOk("Hi there, folks. Even thought of having a wedding held on Amoria? When the talk is about wedding, everyone firstly thinks about Amoria, there is no miss to it. Our chapel here is renowned around the Maple world for offering the best wedding services for maplers!")
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
                        cm.sendOk("Please purchase fashionable #rwedding clothes#k for the wedding, quickly! It's time to shine, baby~!")
                        cm.dispose()
                        return
                     } else if (!isSuitedForWedding(partner, false)) {
                        cm.sendOk("Your partner must know they must have fashionable #rwedding clothes#k for the wedding. It's time to shine, baby~!")
                        cm.dispose()
                        return
                     }

                     cm.sendOk("Alright! The couple appeared here stylish as ever. Let's go folks, let's rock 'n' roll!!!")
                  } else {
                     cm.sendOk("Aww, your partner is elsewhere... Both must be here for the wedding, else it's going to be sooooo lame.")
                     cm.dispose()
                  }
               } else {
                  String placeTime = cserv.getWeddingReservationTimeLeft(wid)

                  cm.sendOk("Yo. Your wedding is set to happen at the #r" + placeTime + "#k, get a decent apparel don't be late will you?")
                  cm.dispose()
               }
            } else {
               cm.sendOk("Aawww, I'm sorry but there are no reservations made for you at this channel for the time being.")
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
               cm.sendOk("Aww, it seems your partner is elsewhere... Both must be here for the wedding, else it's going to be sooooo lame.")
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