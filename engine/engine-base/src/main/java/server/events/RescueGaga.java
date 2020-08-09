/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server.events;

import client.MapleCharacter;
import client.SkillFactory;

public class RescueGaga extends MapleEvents {

   private int completed;

   public RescueGaga(int completed) {
      super();
      this.completed = completed;
   }

   public int getCompleted() {
      return completed;
   }

   public void complete() {
      completed++;
   }

   @Override
   public int getInfo() {
      return getCompleted();
   }

   public void giveSkill(MapleCharacter chr) {
      int skillId = switch (chr.getJobType()) {
         case 0 -> 1013;
         case 1, 2 -> 10001014;
         default -> 0;
      };

      long expiration = (System.currentTimeMillis() + 3600 * 24 * 20 * 1000);//20 days
      if (completed < 20) {
         SkillFactory.getSkill(skillId).ifPresent(skill -> chr.changeSkillLevel(skill, (byte) 1, 1, expiration));
         SkillFactory.getSkill(skillId + 1).ifPresent(skill -> chr.changeSkillLevel(skill, (byte) 1, 1, expiration));
         SkillFactory.getSkill(skillId + 2).ifPresent(skill -> chr.changeSkillLevel(skill, (byte) 1, 1, expiration));
      } else {
         final long skillExpiration = chr.getSkillExpiration(skillId);
         SkillFactory.getSkill(skillId).ifPresent(skill -> chr.changeSkillLevel(skill, (byte) 2, 2, skillExpiration));
      }
   }

}
