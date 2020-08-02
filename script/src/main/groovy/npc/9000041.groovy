package npc
import tools.I18nMessage

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
            cm.sendOk(I18nMessage.from("9000041_MEDAL_RANKING_UNAVAILABLE"))
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
         cm.sendGetText(I18nMessage.from("9000041_START").with(options[selectedType]))
      } else if (status == 2) {
         name = cm.getText()
         int res = cm.getPlayer().sellAllItemsFromName((byte) (selectedType + 1), name)

         if (res > -1) {
            cm.sendOk(I18nMessage.from("9000041_COMPLETE").with(cm.numberWithCommas(res)))
         } else {
            cm.sendOk(I18nMessage.from("9000041_NOT_IN_INVENTORY").with(name, options[selectedType]))
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