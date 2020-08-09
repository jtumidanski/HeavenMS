package client.processor.action;

import accessor.AbstractQueryExecutor;
import client.MapleCharacter;

public class MakerProcessor extends AbstractQueryExecutor {
   private static MakerProcessor instance;

   public static MakerProcessor getInstance() {
      if (instance == null) {
         instance = new MakerProcessor();
      }
      return instance;
   }

   private MakerProcessor() {
   }

   public int getMakerSkillLevel(MapleCharacter chr) {
      return chr.getSkillLevel((chr.getJob().getId() / 1000) * 10000000 + 1007);
   }
}