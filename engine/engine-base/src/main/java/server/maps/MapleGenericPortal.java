package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import constants.game.GameConstants;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import scripting.portal.PortalScriptManager;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

public class MapleGenericPortal implements MaplePortal {

   private String name;
   private String target;
   private Point position;
   private int targetMap;
   private int type;
   private boolean status = true;
   private int id;
   private String scriptName;
   private boolean portalState;
   private MonitoredReentrantLock scriptLock = null;

   public MapleGenericPortal(int type) {
      this.type = type;
   }

   @Override
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public Point getPosition() {
      return position;
   }

   public void setPosition(Point position) {
      this.position = position;
   }

   @Override
   public String getTarget() {
      return target;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   @Override
   public boolean getPortalStatus() {
      return status;
   }

   @Override
   public void setPortalStatus(boolean newStatus) {
      this.status = newStatus;
   }

   @Override
   public int getTargetMapId() {
      return targetMap;
   }

   public void setTargetMapId(int targetMapId) {
      this.targetMap = targetMapId;
   }

   @Override
   public int getType() {
      return type;
   }

   @Override
   public String getScriptName() {
      return scriptName;
   }

   @Override
   public void setScriptName(String scriptName) {
      this.scriptName = scriptName;

      if (scriptName != null) {
         if (scriptLock == null) {
            scriptLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.PORTAL, true);
         }
      } else {
         scriptLock = null;
      }
   }

   @Override
   public void enterPortal(MapleClient c) {
      boolean changed = false;
      if (getScriptName() != null) {
         try {
            scriptLock.lock();
            try {
               changed = PortalScriptManager.getInstance().executePortalScript(this, c);
            } finally {
               scriptLock.unlock();
            }
         } catch (NullPointerException npe) {
            npe.printStackTrace();
         }
      } else if (getTargetMapId() != 999999999) {
         MapleCharacter chr = c.getPlayer();
         if (!(chr.getChalkboard() != null && GameConstants.isFreeMarketRoom(getTargetMapId()))) {
            MapleMap to = chr.getEventInstance() == null ? c.getChannelServer().getMapFactory().getMap(getTargetMapId()) : chr.getEventInstance().getMapInstance(getTargetMapId());
            MaplePortal pto = to.getPortal(getTarget());
            if (pto == null) {// fallback for missing portals - no real life case anymore - interesting for not implemented areas
               pto = to.getPortal(0);
            }
            chr.changeMap(to, pto); //late resolving makes this harder but prevents us from loading the whole world at once
            changed = true;
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You cannot enter this map with the chalkboard opened.");
         }
      }
      if (!changed) {
         PacketCreator.announce(c, new EnableActions());
      }
   }

   @Override
   public boolean getPortalState() {
      return portalState;
   }

   @Override
   public void setPortalState(boolean state) {
      this.portalState = state;
   }
}
