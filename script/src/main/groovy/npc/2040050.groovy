package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		Eurek the Alchemist
	Map(s): 		
	Description: 	
*/


class NPC2040050 {
   NPCConversationManager cm
   int status = 0
   int sel = -1
   String menu = ""
   int set
   int makeItem
   boolean access = true
   int[][] requiredItems = []
   int cost = 4000
   int[] makeItems = [4006000, 4006001]
   int[][][][] requiredSet = [[[[4000046, 20], [4000027, 20], [4021001, 1]],
                               [[4000025, 20], [4000049, 20], [4021006, 1]],
                               [[4000129, 15], [4000130, 15], [4021002, 1]],
                               [[4000074, 15], [4000057, 15], [4021005, 1]],
                               [[4000054, 7], [4000053, 7], [4021003, 1]]],
                              [[[4000046, 20], [4000027, 20], [4011001, 1]],
                          [[4000014, 20], [4000049, 20], [4011003, 1]],
                          [[4000132, 15], [4000128, 15], [4011005, 1]],
                          [[4000074, 15], [4000069, 15], [4011002, 1]],
                          [[4000080, 7], [4000079, 7], [4011004, 1]]]]

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && (status == 1 || status == 2))) {
         cm.dispose()
         return
      }
      if (mode == 0) {
         cm.sendNext(I18nMessage.from("2040050_NOT_ENOUGH_MATERIALS"))

         cm.dispose()
      }
      if (mode == 1) {
         status++
      }
      if (status == 0) {
         cm.sendNext(I18nMessage.from("2040050_ALRIGHT"))

      } else if (status == 1) {
         cm.sendSimple(I18nMessage.from("2040050_AS_YOU_CAN_SEE"))

      } else if (status == 2) {
         set = selection
         makeItem = makeItems[set]
         for (def i = 0; i < requiredSet[set].length; i++) {
            menu += "\r\n#L" + i + "##bMake it using #t" + requiredSet[set][i][0][0] + "# and #t" + requiredSet[set][i][1][0] + "##k#l"
         }
         cm.sendSimple("Haha... #b#t" + makeItem + "##k is a mystical rock that only I can make. Many travelers seems to need this for most powerful skills that require more than just MP and HP. There are 5 ways to make #t" + makeItem + "#. Which way do you want to make it?" + menu)
      } else if (status == 3) {
         int[][] set2 = requiredSet[set][selection]
         requiredItems[0] = [set2[0][0], set2[0][1]]
         requiredItems[1] = [set2[1][0], set2[1][1]]
         requiredItems[2] = [set2[2][0], set2[2][1]]
         menu = ""
         for (def i = 0; i < requiredItems.length; i++) {
            menu += "\r\n#v" + requiredItems[i][0] + "# #b" + requiredItems[i][1] + " #t" + requiredItems[i][0] + "#s#k"
         }
         menu += "\r\n#i4031138# #b" + cost + " mesos#k"
         cm.sendYesNo("To make #b5 #t" + makeItem + "##k, I'll need the following items. Most of them can be obtained through hunting, so it won't be terribly difficult for you to get them. What do you think? Do you want some?\r\n" + menu)
      } else if (status == 4) {
         for (def i = 0; i < requiredItems.length; i++) {
            if (!cm.haveItem(requiredItems[i][0], requiredItems[i][1])) {
               access = false
            }
         }
         if (!access || !cm.canHold(makeItem) || cm.getMeso() < cost) {
            cm.sendNext(I18nMessage.from("2040050_MISSING_ITEMS_OR_INVENTORY_FULL"))

         } else {
            cm.sendOk(I18nMessage.from("2040050_5_PIECES").with(makeItem))

            cm.gainItem(requiredItems[0][0], (short) -requiredItems[0][1])
            cm.gainItem(requiredItems[1][0], (short) -requiredItems[1][1])
            cm.gainItem(requiredItems[2][0], (short) -requiredItems[2][1])
            cm.gainMeso(-cost)
            cm.gainItem(makeItem, (short) 5)
         }
         cm.dispose()
      }
   }
}

NPC2040050 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040050(cm: cm))
   }
   return (NPC2040050) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }