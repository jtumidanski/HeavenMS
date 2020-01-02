package server.life;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.StringUtil;

public class MobAttackInfoFactory {
   private static Map<String, MobAttackInfo> mobAttacks = new HashMap<>();
   private static MapleDataProvider dataSource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Mob.wz"));

   public static MobAttackInfo getMobAttackInfo(MapleMonster mob, int attack) {
      MobAttackInfo ret = mobAttacks.get(mob.id() + "" + attack);
      if (ret != null) {
         return ret;
      }
      synchronized (mobAttacks) {
         ret = mobAttacks.get(mob.id() + "" + attack);
         if (ret == null) {
            MapleData mobData = dataSource.getData(StringUtil.getLeftPaddedStr(mob.id() + ".img", '0', 11));
            if (mobData != null) {
//					MapleData infoData = mobData.getChildByPath("info");
               String linkedMob = MapleDataTool.getString("link", mobData, "");
               if (!linkedMob.equals("")) {
                  mobData = dataSource.getData(StringUtil.getLeftPaddedStr(linkedMob + ".img", '0', 11));
               }
               MapleData attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info");

               if (attackData == null) {
                  return null;
               }

               MapleData deadlyAttack = attackData.getChildByPath("deadlyAttack");
               int mpBurn = MapleDataTool.getInt("mpBurn", attackData, 0);
               int disease = MapleDataTool.getInt("disease", attackData, 0);
               int level = MapleDataTool.getInt("level", attackData, 0);
               int mpCon = MapleDataTool.getInt("conMP", attackData, 0);
               ret = new MobAttackInfo(mob.id(), attack, deadlyAttack != null, mpBurn, disease, level, mpCon);
            }
            mobAttacks.put(mob.id() + "" + attack, ret);
         }
         return ret;
      }
   }
}
