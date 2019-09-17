package npc

import client.processor.MakerProcessor
import constants.ServerConstants
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000040 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int mergeFee = 50000
   String name

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

         if (status == 0) {
            if (!ServerConstants.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
               cm.sendOk("The medal ranking system is currently unavailable...")
               cm.dispose()
               return
            }

            int levelLimit = !cm.getPlayer().isCygnus() ? 160 : 110
            String selStr = "The medal ranking system is currently unavailable... Therefore, I am providing the #bEquipment Merge#k service! "

            if (!ServerConstants.USE_STARTER_MERGE && (cm.getPlayer().getLevel() < levelLimit || MakerProcessor.getInstance().getMakerSkillLevel(cm.getPlayer()) < 3)) {
               selStr += "However, you must have #rMaker level 3#k and at least #rlevel 110#k (Cygnus Knight), #rlevel 160#k (other classes) and a fund of #r" + cm.numberWithCommas(mergeFee) + " mesos#k to use the service."
               cm.sendOk(selStr)
               cm.dispose()
            } else if (cm.getMeso() < mergeFee) {
               selStr += "I'm sorry, but this service tax is of #r" + cm.numberWithCommas(mergeFee) + " mesos#k, which it seems you unfortunately don't have right now... Please, stop by again later."
               cm.sendOk(selStr)
               cm.dispose()
            } else {
               selStr += "For the fee of #r" + cm.numberWithCommas(mergeFee) + "#k mesos, merge unnecessary equipments in your inventory into your currently equipped gears to get stat boosts into them, statups based on the attributes of the items used on the merge!"
               cm.sendNext(selStr)
            }
         } else if (status == 1) {
            String selStr = "#rWARNING#b: Make sure you have your items ready to merge at the slots #rAFTER#b the item you have selected to merge.#k Any items #bunder#k the item selected will be merged thoroughly.\r\n\r\nNote that equipments receiving bonuses from merge are going to become #rUntradeable#k thereon, and equipments that already received the merge bonus #rcannot be used for merge#k.\r\n\r\n"
            cm.sendGetText(selStr)
         } else if (status == 2) {
            name = cm.getText()

            if (cm.getPlayer().mergeAllItemsFromName(name)) {
               cm.gainMeso(-mergeFee)
               cm.sendOk("Merging complete! Thanks for using the service and enjoy your new equipment stats.")
            } else {
               cm.sendOk("There is no #b'" + name + "'#k in your #bEQUIP#k inventory!")
            }

            cm.dispose()
         }
      }
   }
}

NPC9000040 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000040(cm: cm))
   }
   return (NPC9000040) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }