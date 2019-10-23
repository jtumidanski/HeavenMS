package npc

import net.server.processor.MapleGuildProcessor
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple("What would you like to do?\r\n#b#L0#Create a Guild#l\r\n#L1#Disband your Guild#l\r\n#L2#Increase your Guild's capacity#l#k")
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
         if (status == 1) {
            sel = selection
            if (selection == 0) {
               if (cm.getPlayer().getGuildId() > 0) {
                  cm.sendOk("You may not create a new Guild while you are in one.")
                  cm.dispose()
               } else {
                  cm.sendYesNo("Creating a Guild costs #b 1500000 mesos#k, are you sure you want to continue?")
               }
            } else if (selection == 1) {
               if (cm.getPlayer().getGuildId() < 1 || cm.getPlayer().getGuildRank() != 1) {
                  cm.sendOk("You can only disband a Guild if you are the leader of that Guild.")
                  cm.dispose()
               } else {
                  cm.sendYesNo("Are you sure you want to disband your Guild? You will not be able to recover it afterward and all your GP will be gone.")
               }
            } else if (selection == 2) {
               if (cm.getPlayer().getGuildId() < 1 || cm.getPlayer().getGuildRank() != 1) {
                  cm.sendOk("You can only increase your Guild's capacity if you are the leader.")
                  cm.dispose()
               } else {
                  cm.sendYesNo("Increasing your Guild capacity by #b5#k costs #b " + MapleGuildProcessor.getInstance().getIncreaseGuildCost(cm.getPlayer().getGuild().get().getCapacity()) + " mesos#k, are you sure you want to continue?")
               }
            }
         } else if (status == 2) {
            if (sel == 0 && cm.getPlayer().getGuildId() <= 0) {
               cm.getPlayer().genericGuildMessage(1)
               cm.dispose()
            } else if (cm.getPlayer().getGuildId() > 0 && cm.getPlayer().getGuildRank() == 1) {
               if (sel == 1) {
                  cm.getPlayer().disbandGuild()
                  cm.dispose()
               } else if (sel == 2) {
                  MapleGuildProcessor.getInstance().increaseGuildCapacity(cm.getPlayer())
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2010007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010007(cm: cm))
   }
   return (NPC2010007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }