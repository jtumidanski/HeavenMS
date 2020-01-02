package server.maps;

import java.awt.Point;

import client.MapleClient;

public interface MaplePortal {
   int TELEPORT_PORTAL = 1;
   int MAP_PORTAL = 2;
   int DOOR_PORTAL = 6;
   boolean OPEN = true;
   boolean CLOSED = false;

   int getType();

   int getId();

   Point getPosition();

   String getName();

   String getTarget();

   String getScriptName();

   void setScriptName(String newName);

   boolean getPortalStatus();

   void setPortalStatus(boolean newStatus);

   int getTargetMapId();

   void enterPortal(MapleClient c);

   boolean getPortalState();

   void setPortalState(boolean state);
}
