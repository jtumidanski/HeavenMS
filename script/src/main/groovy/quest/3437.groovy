package quest
import tools.I18nMessage

import client.MapleJob
import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest3437 {
   QuestActionManager qm
   int status = -1

   int item
   MapleJob stance
   int[] vecItem

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         qm.dispose()
         return
      }
      status++

      if (status == 0) {
         qm.sendNext(I18nMessage.from("3437_WHAT_THE"))
      } else if (status == 1) {
         if (qm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            qm.sendOk(I18nMessage.from("3437_EQUIP_SLOT_NEEDED"))
            qm.dispose()
            return
         }

         String talkStr = "Do you like the glove? I've kept this for a while, and I was planning on using it someday, but it looks much better on you. Please put it to good use; besides, I got so much stuff from the Sector, that I don't need it anymore."
         stance = qm.getPlayer().getJobStyle()

         if (stance == MapleJob.WARRIOR) {
            item = 1082024
         } else if (stance == MapleJob.MAGICIAN) {
            item = 1082063
         } else if (stance == MapleJob.BOWMAN || stance == MapleJob.CROSSBOWMAN) {
            item = 1082072
         } else if (stance == MapleJob.THIEF) {
            item = 1082076
         } else if (stance == MapleJob.BRAWLER || stance == MapleJob.GUNSLINGER) {
            item = 1082195
         } else {
            item = 1082149
         }

         qm.sendNext(talkStr)
      } else if (status == 2) {
         qm.completeQuest()
         qm.gainItem(item, (short) 1)
         qm.gainItem(4000122, (short) -120)
         qm.gainExp(6100)
         qm.sendOk(I18nMessage.from("3437_THANK_YOU"))
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest3437 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3437(qm: qm))
   }
   return (Quest3437) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}