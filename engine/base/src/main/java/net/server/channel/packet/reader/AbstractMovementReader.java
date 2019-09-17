package net.server.channel.packet.reader;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.server.AbsoluteMovementData;
import net.server.MovementData;
import net.server.PacketReader;
import net.server.RelativeMovementData;
import net.server.channel.packet.movement.BaseMovementPacket;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;

public abstract class AbstractMovementReader<T extends BaseMovementPacket> implements PacketReader<T> {
   protected Point readStartingPosition(SeekableLittleEndianAccessor accessor) {
      return new Point(accessor.readShort(), accessor.readShort());
   }

   interface TriFunction<T, U , V, W> {
      T apply(U u, V v, W w);
   }

   protected T producePacket(SeekableLittleEndianAccessor accessor, int yOffset, TriFunction<T, Boolean, List<MovementData>, List<Byte>> function) {
      long movementDataStart = accessor.getPosition();
      List<MovementData> movementDataList = updatePosition(accessor, yOffset);
      long movementDataLength = accessor.getPosition() - movementDataStart; //how many bytes were read by updatePosition
      boolean hasMovement = movementDataLength > 0;

      List<Byte> movementList = new ArrayList<>();
      if (hasMovement) {
         accessor.seek(movementDataStart);
         for (long i = 0; i < movementDataLength; i++) {
            movementList.add(accessor.readByte());
         }
      }
      return function.apply(hasMovement, movementDataList, movementList);
   }

   protected List<MovementData> updatePosition(LittleEndianAccessor accessor, int yOffset) {
      List<MovementData> movementDataList = new ArrayList<>();

      byte numCommands = accessor.readByte();
      for (byte i = 0; i < numCommands; i++) {
         byte command = accessor.readByte();
         switch (command) {
            case 0: // normal move
            case 5:
            case 17: { // Float
               //Absolute movement - only this is important for the server, other movement can be passed to the client
               short xpos = accessor.readShort(); //is signed fine here?
               short ypos = accessor.readShort();
               accessor.skip(6); //xwobble = lea.readShort(); ywobble = lea.readShort(); fh = lea.readShort();
               byte newstate = accessor.readByte();
               accessor.readShort(); //duration
               movementDataList.add(new AbsoluteMovementData(new Point(xpos, ypos + yOffset), newstate));
               break;
            }
            case 1:
            case 2:
            case 6: // fj
            case 12:
            case 13: // Shot-jump-back thing
            case 16: // Float
            case 18:
            case 19: // Springs on maps
            case 20: // Aran Combat Step
            case 22: {
               //Relative movement - server only cares about stance
               accessor.skip(4); //xpos = lea.readShort(); ypos = lea.readShort();
               byte newstate = accessor.readByte();
               accessor.readShort(); //duration
               movementDataList.add(new RelativeMovementData(newstate));
               break;
            }
            case 3:
            case 4: // tele... -.-
            case 7: // assaulter
            case 8: // assassinate
            case 9: // rush
            case 11: //chair
            {
//                case 14: {
               //Teleport movement - same as above
               accessor.skip(8); //xpos = lea.readShort(); ypos = lea.readShort(); xwobble = lea.readShort(); ywobble = lea.readShort();
               byte newstate = accessor.readByte();
               movementDataList.add(new RelativeMovementData(newstate));
               break;
            }
            case 14:
               accessor.skip(9); // jump down (?)
               break;
            case 10: // Change Equip
               //ignored by server
               accessor.readByte();
               break;
                /*case 11: { // Chair
                    short xpos = lea.readShort();
                    short ypos = lea.readShort();
                    short fh = lea.readShort();
                    byte newstate = lea.readByte();
                    short duration = lea.readShort();
                    ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setFh(fh);
                    res.add(cm);
                    break;
                }*/
            case 15: {
               //Jump down movement - stance only
               accessor.skip(12); //short xpos = lea.readShort(); ypos = lea.readShort(); xwobble = lea.readShort(); ywobble = lea.readShort(); fh = lea.readShort(); ofh = lea.readShort();
               byte newstate = accessor.readByte();
               movementDataList.add(new RelativeMovementData(newstate));
               accessor.readShort(); // duration
               break;
            }
            case 21: {//Causes aran to do weird stuff when attacking o.o
                    /*byte newstate = lea.readByte();
                     short unk = lea.readShort();
                     AranMovement am = new AranMovement(command, null, unk, newstate);
                     res.add(am);*/
               accessor.skip(3);
               break;
            }
            default:
               System.out.println("Unhandled Case:" + command);
         }
      }
      return movementDataList;
   }
}
