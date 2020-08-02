package npc

import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2112005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

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

         EventInstanceManager eim = cm.getEventInstance()

         if (!eim.isEventCleared()) {
            if (status == 0) {
               if (eim.getIntProperty("npcShocked") == 0 && cm.haveItem(4001130, 1)) {
                  cm.gainItem(4001130, (short) -1)
                  eim.setIntProperty("npcShocked", 1)

                  cm.sendNext(I18nMessage.from("2112005_SOMETHING_BIG"))
                  MessageBroadcaster.getInstance().sendServerNotice(eim.getPlayers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("JULIET_SHOCK"))

                  cm.dispose()
               } else if (eim.getIntProperty("statusStg4") == 1) {
                  MapleReactor door = cm.getMap().getReactorByName("jnr3_out3")

                  if (door.getState() == ((byte) 0)) {
                     cm.sendNext(I18nMessage.from("2112005_LET_ME_OPEN_THE_DOOR"))
                     door.hitReactor(cm.getClient())
                  } else {
                     cm.sendNext(I18nMessage.from("2112005_PLEASE_HURRY"))
                  }

                  cm.dispose()
               } else if (cm.haveItem(4001134, 1) && cm.haveItem(4001135, 1)) {
                  if (cm.isEventLeader()) {
                     cm.gainItem(4001134, (short) -1)
                     cm.gainItem(4001135, (short) -1)
                     cm.sendNext(I18nMessage.from("2112005_NOW_WE_CAN_PROCEED"))

                     eim.showClearEffect()
                     eim.giveEventPlayersStageReward(4)
                     eim.setIntProperty("statusStg4", 1)

                     cm.getMap().killAllMonsters()
                     cm.getMap().getReactorByName("jnr3_out3").hitReactor(cm.getClient())
                  } else {
                     cm.sendOk(I18nMessage.from("2112005_LET_LEADER_PASS"))
                  }

                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("2112005_MUST_KEEP_FIGHTING"))
               }
            } else {
               cm.warp(926110700, 0)
               cm.dispose()
            }
         } else {
            if (status == 0) {
               if (eim.getIntProperty("escortFail") == 0) {
                  cm.sendNext(I18nMessage.from("2112005_FINALLY"))
               } else {
                  cm.sendNext(I18nMessage.from("2112005_THANKS_TO_YOUR_EFFORTS"))
                  status = 2
               }
            } else if (status == 1) {
               cm.sendNext(I18nMessage.from("2112005_RECEIVE_THIS_GIFT"))
            } else if (status == 2) {
               if (cm.canHold(4001160)) {
                  cm.gainItem(4001160, (short) 1)

                  if (eim.getIntProperty("normalClear") == 1) {
                     cm.warp(926110600, 0)
                  } else {
                     cm.warp(926110500, 0)
                  }
               } else {
                  cm.sendOk(I18nMessage.from("2112005_MAKE_ETC_SPACE"))
               }

               cm.dispose()
            } else {
               cm.warp(926110600, 0)
               cm.dispose()
            }
         }
      }
   }
}

NPC2112005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2112005(cm: cm))
   }
   return (NPC2112005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }