package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventInstanceManager
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventInstanceManager eim
   EventManager em
   def pqArea

   def start() {
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

         if (cm.getMapId() != 193000000) {
            eim = cm.getEventInstance()

            if (status == 0) {
               if (!eim.isEventCleared()) {
                  int couponsNeeded = eim.getIntProperty("couponsNeeded")

                  if (cm.isEventLeader()) {
                     if (cm.haveItem(4001007, couponsNeeded)) {
                        cm.sendNext(I18nMessage.from("1052013_COLLECTED_ALL_THE_NEEDED_COUPONS"))
                        cm.gainItem(4001007, (short) couponsNeeded)
                        eim.clearPQ()

                        cm.dispose()
                     } else {
                        cm.sendYesNo(I18nMessage.from("1052013_COUPON_REQUIREMENT").with(couponsNeeded))
                     }
                  } else {
                     cm.sendYesNo(I18nMessage.from("1052013_COUPON_REQUIREMENT_MEMBER").with(couponsNeeded))
                  }
               } else {
                  if (!eim.giveEventReward(cm.getPlayer())) {
                     cm.sendOk(I18nMessage.from("1052013_MAKE_ETC_INVENTORY_ROOM"))
                     cm.dispose()
                  } else {
                     cm.warp(193000000)
                     cm.dispose()
                  }
               }
            } else if (status == 1) {
               cm.warp(193000000)
               cm.dispose()
            }
         } else {
            String[] levels = ["#m190000000#", "#m191000000#", "#m192000000#", "#m195000000#", "#m196000000#", "#m197000000#"]
            if (status == 0) {
               String sendStr = I18nMessage.from("1052013_PREMIUM_ROAD").to(cm.getClient()).evaluate()
               for (def i = 0; i < 6; i++) {
                  sendStr += "#L" + i + "#" + levels[i] + "#l\r\n"
               }

               cm.sendSimple(SimpleMessage.from(sendStr))
            } else if (status == 1) {
               pqArea = selection + 1

               em = cm.getEventManager("CafePQ_" + pqArea)
               if (em == null) {
                  cm.sendOk(I18nMessage.from("1052013_ENCOUNTERED_ERROR").with(pqArea))
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  status = 1
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("1052013_OPERATES_DIFFERENTLY").with(levels[selection], em.getProperty("party"), (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable")))
            } else if (status == 2) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("1052013_MUST_BE_IN_PARTY"))
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("1052013_PARTY_LEADER_MUST_START"))
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk(I18nMessage.from("1052013_ANOTHER_PARTY_INSIDE"))
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("1052013_PARTY_REQUIREMENTS"))
                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("1052013_PARTY_SEARCH_STATUS").with(psState ? "enabled" : "disabled"))
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("1052013_INFO"))
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC1052013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052013(cm: cm))
   }
   return (NPC1052013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }