package server.maps;

import java.awt.Point;

import provider.MapleData;
import provider.MapleDataTool;

public class MaplePortalFactory {
   private int nextDoorPortal;

   public MaplePortalFactory() {
      nextDoorPortal = 0x80;
   }

   public MaplePortal makePortal(int type, MapleData portal) {
      MapleGenericPortal ret;
      if (type == MaplePortal.MAP_PORTAL) {
         ret = new MapleMapPortal();
      } else {
         ret = new MapleGenericPortal(type);
      }
      loadPortal(ret, portal);
      return ret;
   }

   private void loadPortal(MapleGenericPortal myPortal, MapleData portal) {
      myPortal.setName(MapleDataTool.getString(portal.getChildByPath("pn")));
      myPortal.setTarget(MapleDataTool.getString(portal.getChildByPath("tn")));
      myPortal.setTargetMapId(MapleDataTool.getInt(portal.getChildByPath("tm")));
      int x = MapleDataTool.getInt(portal.getChildByPath("x"));
      int y = MapleDataTool.getInt(portal.getChildByPath("y"));
      myPortal.setPosition(new Point(x, y));
      String script = MapleDataTool.getString("script/src/main/groovy", portal, null);
      if (script != null && script.equals("")) {
         script = null;
      }
      myPortal.setScriptName(script);
      if (myPortal.getType() == MaplePortal.DOOR_PORTAL) {
         myPortal.setId(nextDoorPortal);
         nextDoorPortal++;
      } else {
         myPortal.setId(Integer.parseInt(portal.getName()));
      }
   }
}
