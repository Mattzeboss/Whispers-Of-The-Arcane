package src;

import java.util.ArrayList;
import java.util.HashMap;

public class Field {
    public static class FieldPosition {
        public int x;
        public int y;

        public FieldPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public FieldPosition add(FieldPosition other) {
            return new FieldPosition(other.x + x, other.y + y);
        }


        public FieldPosition sub(FieldPosition other) {
            return new FieldPosition( x - other.x, y - other.y);
        }

        @Override
        public boolean equals(Object obj) {
            FieldPosition other = (FieldPosition) obj;
            return other.x == x && other.y == y;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(x+y);
        }
    }

    private final HashMap<FieldPosition, ArrayList<GridEntity>> position_to_entity = new HashMap<>();
    private final HashMap<GridEntity, FieldPosition> entity_to_position = new HashMap<>();

    public FieldPosition get_pos(GridEntity e) {
        return entity_to_position.get(e);
    }

    public void add_entity(GridEntity e, FieldPosition pos) {
        //an entity can take up multiple tiles, if it does, we put it in every tile
        for (int i = 0; i < e.getWidth(); i++) {
            for (int j = 0; j < e.getHeight(); j++) {
                position_to_entity.computeIfAbsent(pos.add(new FieldPosition(i, j)), (_a) -> new ArrayList<>()).add(e);
            }
        }

        entity_to_position.put(e, pos);
    }

    public void remove_entity(GridEntity e) {
        for (int i = 0; i < e.getWidth(); i++) {
            for (int j = 0; j < e.getHeight(); j++) {
                //we only remove if we already inserted
                position_to_entity.get(get_pos(e).add(new FieldPosition(i, j))).remove(e);
            }
        }
        entity_to_position.remove(e);
    }

    public void teleport_entity(GridEntity e, FieldPosition pos) {
        remove_entity(e);
        add_entity(e, pos);
    }

    //moves the entity by delta amount
    public void move_entity(GridEntity e, FieldPosition delta) {
        teleport_entity(e, get_pos(e).add(delta));
    }

    public ArrayList<GridEntity> get_overlapping_entities(GridEntity e) {
        ArrayList<GridEntity> overlapping = new ArrayList<>();
        for (int i = 0; i <  e.getWidth(); i++) {
            for (int j = 0; j < e.getHeight(); j++) {
                //we only remove if we already inserted
                position_to_entity.get(get_pos(e).add(new FieldPosition(i, j))).forEach((ent) -> {
                    //we won't add ourselves or anything we have already marked
                    //we use a list because, presumably, the # of elements will be small enough for a list to be faster than a set
                    if (!ent.equals(e) && !overlapping.contains(ent)) overlapping.add(ent);
                });
            }
        }
        return overlapping;
    }

}
