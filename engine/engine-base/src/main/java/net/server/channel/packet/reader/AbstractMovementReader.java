package net.server.channel.packet.reader;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.server.AbsoluteMovementData;
import net.server.MovementData;
import net.server.PacketReader;
import net.server.RelativeMovementData;
import net.server.channel.packet.movement.BaseMovementPacket;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.TriFunction;
import tools.data.input.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.exceptions.EmptyMovementException;

public abstract class AbstractMovementReader<T extends BaseMovementPacket> implements PacketReader<T> {
   protected Point readStartingPosition(SeekableLittleEndianAccessor accessor) {
      return new Point(accessor.readShort(), accessor.readShort());
   }

   protected T producePacket(SeekableLittleEndianAccessor accessor, int yOffset, TriFunction<T, Boolean, List<MovementData>, List<Byte>> function) {
      long movementDataStart = accessor.getPosition();

      List<MovementData> movementDataList;
      try {
         movementDataList = updatePosition(accessor, yOffset);
      } catch (EmptyMovementException exception) {
         return null;
      }

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

   protected List<MovementData> updatePosition(LittleEndianAccessor accessor, int yOffset) throws EmptyMovementException {
      List<MovementData> movementDataList = new ArrayList<>();

      byte numCommands = accessor.readByte();
      if (numCommands < 1) {
         throw new EmptyMovementException(accessor);
      }

      for (byte i = 0; i < numCommands; i++) {
         byte command = accessor.readByte();
         switch (command) {
            case 0: // normal move
            case 5:
            case 17: { // Float
               //Absolute movement - only this is important for the server, other movement can be passed to the client
               short xPosition = accessor.readShort(); //is signed fine here?
               short yPosition = accessor.readShort();
               accessor.skip(6); //x wobble = lea.readShort(); y wobble = lea.readShort(); fh = lea.readShort();
               byte newState = accessor.readByte();
               accessor.readShort(); //duration
               movementDataList.add(new AbsoluteMovementData(new Point(xPosition, yPosition + yOffset), newState));
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
               accessor.skip(4); //x pos = lea.readShort(); y pos = lea.readShort();
               byte newState = accessor.readByte();
               accessor.readShort(); //duration
               movementDataList.add(new RelativeMovementData(newState));
               break;
            }
            case 3:
            case 4: // teleport... -.-
            case 7: // assaulter
            case 8: // assassinate
            case 9: // rush
            case 11: //chair
            {
//                case 14: {
               //Teleport movement - same as above
               accessor.skip(8); //x pos = lea.readShort(); y pos = lea.readShort(); x wobble = lea.readShort(); y wobble = lea.readShort();
               byte newState = accessor.readByte();
               movementDataList.add(new RelativeMovementData(newState));
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
                    short xPosition = lea.readShort();
                    short yPosition = lea.readShort();
                    short fh = lea.readShort();
                    byte newState = lea.readByte();
                    short duration = lea.readShort();
                    ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos), duration, newstate);
                    cm.setFh(fh);
                    res.add(cm);
                    break;
                }*/
            case 15: {
               //Jump down movement - stance only
               accessor.skip(12); //short xPosition = lea.readShort(); yPosition = lea.readShort(); xWobble = lea.readShort(); yWobble = lea.readShort(); fh = lea.readShort(); ofh = lea.readShort();
               byte newState = accessor.readByte();
               movementDataList.add(new RelativeMovementData(newState));
               accessor.readShort(); // duration
               break;
            }
            case 21: {//Causes aran to do weird stuff when attacking o.o
                    /*byte newState = lea.readByte();
                     short unk = lea.readShort();
                     AranMovement am = new AranMovement(command, null, unk, newstate);
                     res.add(am);*/
               accessor.skip(3);
               break;
            }
            default:
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.UNHANDLED_EVENT, "Unhandled Case:" + command);
         }
      }
      return movementDataList;
   }
}
