package blockbreak;

import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;

class ColoredRect extends Rectangle {

    public ColoredRect(double x, double y, double width, double height){
        super(x, y, width, height);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1);
    }

    public void setColor(int n){
        Color color;
        switch(n % 5){
            case 0: color = Color.RED;    break;
            case 1: color = Color.BLUE;   break;
            case 2: color = Color.YELLOW; break;
            case 3: color = Color.GREEN;  break;
            case 4: color = Color.ORANGE; break;
            default: color = Color.BLACK;
        }
        this.setFill(color);
    }
}
