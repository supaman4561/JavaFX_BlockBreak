package blockbreak;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

class ColoredRect extends Rectangle{

    public ColoredRect(int x, int y, int width, int height){
	     super(x, y, width, height);
    }

    public void setColor(int num){
	Color color;
	switch(num % 5){
	case 0: color = Color.RED;    break;
	case 1: color = Color.BLUE;   break;
	case 2: color = Color.YELLOW; break;
	case 3: color = Color.GREEN;  break;
	case 4: color = Color.ORANGE; break;
	default: color = Color.BLACK;  break;
	}

	this.setFill(color);
	this.setStroke(Color.BLACK);
	this.setStrokeWidth(1);
    }
}
