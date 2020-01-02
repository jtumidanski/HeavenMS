package npc

import config.YamlConfig
import scripting.npc.NPCConversationManager

class NPC9000041 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   String[] options = ["EQUIP", "USE", "SET-UP", "ETC"]
   String name
   int selectedType = 0

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }

      if (status == 0) {
         if (!YamlConfig.config.server.USE_ENABLE_CUSTOM_NPC_SCRIPT) {
            cm.sendOk("The medal ranking system is currently unavailable...")
            cm.dispose()
            return
         }

         String selStr = "Hello, I am the #bBazaar NPC#k! Sell to me any item on your inventory you don't need. #rWARNING#b: Make sure you have your items ready to sell at the slots #rAFTER#b the item you have selected to sell.#k Any items #bunder#k the item selected will be sold thoroughly."
         for (int i = 0; i < options.length; i++) {
            selStr += "\r\n#L" + i + "# " + options[i] + "#l"
         }
         cm.sendSimple(selStr)
      } else if (status == 1) {
         selectedType = selection
         cm.sendGetText("From what item on your #r" + options[selectedType] + "#k inventory do you want to start the transaction?")
      } else if (status == 2) {
         name = cm.getText()
         int res = cm.getPlayer().sellAllItemsFromName((byte) (selectedType + 1), name)

         if (res > -1) {
            cm.sendOk("Transaction complete! You received #r" + cm.numberWithCommas(res) + " mesos#k from this action.")
         } else {
            cm.sendOk("There is no #b'" + name + "'#k in your #b" + options[selectedType] + "#k inventory!")
         }

         cm.dispose()
      }
   }
}

NPC9000041 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000041(cm: cm))
   }
   return (NPC9000041) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }