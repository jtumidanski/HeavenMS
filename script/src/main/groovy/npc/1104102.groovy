package npc

import scripting.npc.NPCConversationManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap

import java.awt.*

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1104102 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   int npcid = 1104102
   int baseJob = 13

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
            if (Math.floor(cm.getJobId() / 100) != baseJob) {
               cm.sendOk("Hello there, #h0#. Are you helping us finding the intruder? He is not in this area, I've already searched here.")
               cm.dispose()
               return
            }

            cm.sendOk("Darn, you found me! Then, there's only one way out! Let's fight, like #rBlack Wings#k should!")
         } else if (status == 1) {
            MapleMap mapobj = cm.getMap()
            Point npcpos = mapobj.getMapObject(cm.getNpcObjectId()).position()

            spawnMob(npcpos.x, npcpos.y, 9001009, mapobj)
            mapobj.destroyNPC(npcid)
            cm.dispose()
         }
      }
   }

   static def spawnMob(double x, double y, int id, MapleMap map) {
      if (map.getMonsterById(id) != null) {
         return
      }

      MapleMonster mob = MapleLifeFactory.getMonster(id)
      map.spawnMonsterOnGroundBelow(mob, new Point((int) x, (int) y))
   }
}

NPC1104102 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1104102(cm: cm))
   }
   return (NPC1104102) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }