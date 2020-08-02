package npc
import tools.I18nMessage

import client.processor.CharacterProcessor
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NpcChangeName {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0 && mode == 1) {
            if (cm.haveItem(2430026)) {
               cm.sendYesNo("I can change your name for you if you would like?", (byte) 1)
            } else {
               cm.dispose()
            }
         } else if (status == 1) {
            cm.sendGetText(I18nMessage.from("changeName_INPUT_DESIRED_NAME"))
         } else if (status == 2) {
            String text = cm.getText()
            boolean canCreate = CharacterProcessor.getInstance().canCreateChar(text)
            if (canCreate) {
               cm.getPlayer().setName(text)
               cm.sendOk("Your name has been changed to #b" + text + "#k. You will have to login again for this to take effect.", (byte) 1)
               cm.gainItem(2430026, (short) -1)
            } else {
               cm.sendNext("I'm afraid you can't use the name #b" + text + "#k or it is already taken.", (byte) 1)
            }
         } else if (status == 3) {
            cm.dispose()
            cm.getClient().disconnect(false, false)
         }
      }
   }
}

NpcChangeName getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NpcChangeName(cm: cm))
   }
   return (NpcChangeName) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }