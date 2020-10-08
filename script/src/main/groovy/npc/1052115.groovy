package npc

import client.MapleQuestStatus
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052115 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int section = 0

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 1) {
         if (cm.getMapId() == 910320001) {
            cm.warp(910320000, 0)
            cm.dispose()
         } else if (cm.getMapId() == 910330001) {
            int itemId = 4001321
            if (!cm.canHold(itemId)) {
               cm.sendOk(I18nMessage.from("1052115_MAKE_ETC_ROOM"))
            } else {
               cm.gainItem(itemId, (short) 1)
               cm.warp(910320000, 0)
            }
            cm.dispose()
         } else if (cm.getMapId() >= 910320100 && cm.getMapId() <= 910320304) {
            cm.sendYesNo(I18nMessage.from("1052115_WOULD_YOU_LIKE_TO_EXIT"))
            status = 99
         } else {
            cm.sendSimple(I18nMessage.from("1052115_HELLO"))
         }
      } else if (status == 2) {
         section = selection
         if (selection == 1) {
            if (cm.getPlayer().getLevel() < 25 || cm.getPlayer().getLevel() > 30 || !cm.isLeader()) {
               cm.sendOk(I18nMessage.from("1052115_LEVEL_RANGE"))
            } else {
               if (!cm.start_PyramidSubway(-1)) {
                  cm.sendOk(I18nMessage.from("1052115_FULL"))
               }
            }
            //TODO
         } else if (selection == 2) {
            if (cm.haveItem(4001321)) {
               if (cm.bonus_PyramidSubway(-1)) {
                  cm.gainItem(4001321, (short) -1)
               } else {
                  cm.sendOk(I18nMessage.from("1052115_TRAIN_999_FULL"))
               }
            } else {
               cm.sendOk(I18nMessage.from("1052115_NEED_BOARDING_PASS"))
            }
         } else if (selection == 3) {
            MapleQuestStatus record = cm.getQuestRecord(7662)
            String data = record.customData()
            if (data == null) {
               record = record.setCustomData("0")
               cm.addQuest(record)
               data = record.customData()
            }
            int mons = data.toInteger()
            if (mons < 10000) {
               cm.sendOk("Please defeat at least 10,000 monsters in the Station and look for me again. Kills : " + mons)
            } else if (cm.canHold(1142141) && !cm.haveItem(1142141)) {
               cm.gainItem(1142141, (short) 1)
               cm.startQuest(29931)
               cm.completeQuest(29931)
            } else {
               cm.sendOk(I18nMessage.from("1052115_MAKE_ROOM"))
            }
         }
         cm.dispose()
      } else if (status == 100) {
         cm.warp(910320000, 0)
         cm.dispose()
      }
   }
}

NPC1052115 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052115(cm: cm))
   }
   return (NPC1052115) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }