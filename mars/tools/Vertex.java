package mars.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

class Vertex {
    static final int movingUpside = 1;
    static final int movingDownside = 2;
    static final int movingLeft = 3;
    static final int movingRight = 4;

    private int numIndex;
    private int init;
    private int end;
    private int current;
    private String name;
    int direction;
    int oppositeAxis;
    private boolean isMovingXaxis;
    private Color color;
    private boolean active;
    private boolean isText;
    private ArrayList<Integer> targetVertex;

    Vertex(int index, int init, int end, String name, int oppositeAxis, boolean isMovingXaxis,
           String listOfColors, String listTargetVertex, boolean isText) {
        this.numIndex = index;
        this.init = init;
        this.current = this.init;
        this.end = end;
        this.name = name;
        this.oppositeAxis = oppositeAxis;
        this.isMovingXaxis = isMovingXaxis;
        this.active = false;
        this.isText = isText;
        if (isMovingXaxis) {
            if (init < end)
                direction = movingLeft;
            else
                direction = movingRight;

        } else {
            if (init < end)
                direction = movingUpside;
            else
                direction = movingDownside;
        }
        String[] list = listTargetVertex.split("#");
        targetVertex = new ArrayList<>();
        for (String s : list) {
            targetVertex.add(Integer.parseInt(s));
        }
        String[] listColor = listOfColors.split("#");
        this.color = new Color(Integer.parseInt(listColor[0]), Integer.parseInt(listColor[1]), Integer.parseInt(listColor[2]));
    }

    int getDirection() {
        return direction;
    }

    boolean isText() {
        return this.isText;
    }


    ArrayList<Integer> getTargetVertex() {
        return targetVertex;
    }

    int getNumIndex() {
        return numIndex;
    }

    void setNumIndex(int numIndex) {
        this.numIndex = numIndex;
    }

    int getInit() {
        return init;
    }

    void setInit(int init) {
        this.init = init;
    }

    int getEnd() {
        return end;
    }

    void setEnd(int end) {
        this.end = end;
    }

    int getCurrent() {
        return current;
    }

    void setCurrent(int current) {
        this.current = current;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getOppositeAxis() {
        return oppositeAxis;
    }

    void setOppositeAxis(int oppositeAxis) {
        this.oppositeAxis = oppositeAxis;
    }

    boolean isMovingXaxis() {
        return isMovingXaxis;
    }

    void setMovingXaxis(boolean isMovingXaxis) {
        this.isMovingXaxis = isMovingXaxis;
    }

    Color getColor() {
        return color;
    }

    void setColor(Color color) {
        this.color = color;
    }

    boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }


    void drawVertex(Graphics2D g2d) {
        int start = Math.min(getInit(), getCurrent());
        int dist = Math.abs(getCurrent() - getInit());
        g2d.setColor(getColor());
        if (isMovingXaxis()) {
            g2d.fillRect(start, getOppositeAxis(), dist + 3, 3);
        } else {
            g2d.fillRect(getOppositeAxis(), start, 3, dist + 3);
        }
        if (isActive()) {
            setCurrent(getCurrent() + Integer.signum(getEnd() - getInit()));
            setActive(getCurrent() != getEnd());
        }
    }
}
