package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1012112 {
   NPCConversationManager cm
   int status = -1
   int sel = -1
   EventManager em = null

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)

   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (cm.getMapId() == 100000200) {
            if (status == 0) {
               em = cm.getEventManager("HenesysPQ")
               if (em == null) {
                  cm.sendOk(I18nMessage.from("1012112_PQ_ERROR"))
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple(I18nMessage.from("1012112_HELLO").with(em.getProperty("party"), (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable")))
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk(I18nMessage.from("1012112_HELLO_NO_PARTY"))
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk(I18nMessage.from("1012112_HELLO_NOT_LEADER"))
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk(I18nMessage.from("1012112_SOMEONE_ALREADY_ATTEMPTING"))
                        }
                     } else {
                        cm.sendOk(I18nMessage.from("1012112_PARTY_NOT_ELLIGIBLE"))
                     }

                     cm.dispose()
                  }
               } else if (selection == 1) {
                  boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
                  cm.sendOk(I18nMessage.from("1012112_PARTY_SEARCH_TOGGLE").with(psState ? "enabled" : "disabled"))
                  cm.dispose()
               } else if (selection == 2) {
                  cm.sendOk(I18nMessage.from("1012112_MISSION_INFO"))
                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("1012112_HAT_EXCHANGE"))
               }
            } else {
               if (cm.hasItem(4001158, 20)) {
                  if (cm.canHold(1002798)) {
                     cm.gainItem(4001158, (short) -20)
                     cm.gainItem(1002798, (short) 20)
                     cm.sendNext(I18nMessage.from("1012112_GIVE_ITEM"))
                  }
               } else {
                  cm.sendNext(I18nMessage.from("1012112_NEED_MORE_OF_ITEM"))
               }

               cm.dispose()
            }
         } else if (cm.getMapId() == 910010100) {
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("1012112_THANK_YOU"))
            } else if (status == 1) {
               if (cm.getEventInstance().giveEventReward(cm.getPlayer())) {
                  cm.warp(100000200)
               } else {
                  cm.sendOk(I18nMessage.from("1012112_CHECK_INVENTORY_SPACE"))
               }
               cm.dispose()
            }
         } else if (cm.getMapId() == 910010400) {
            if (status == 0) {
               cm.sendYesNo(I18nMessage.from("1012112_RETURN"))
            } else if (status == 1) {
               if (cm.getEventInstance() == null) {
                  cm.warp(100000200)
               } else if (cm.getEventInstance().giveEventReward(cm.getPlayer())) {
                  cm.warp(100000200)
               } else {
                  cm.sendOk(I18nMessage.from("1012112_CHECK_INVENTORY_SPACE"))
               }
               cm.dispose()
            }
         }
      }
   }
}

NPC1012112 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012112(cm: cm))
   }
   return (NPC1012112) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }