package org.amirshamaei.IDV;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Axis extends Pane {

    Rectangle wall;
    Color gridColor;
    double lineWidth;

    public Axis(double size1, double size2, double size3, char dir) {

        wall = new Rectangle(size1, size2);
        getChildren().add(wall);

        lineWidth = 0.2;
        gridColor = Color.BLACK;



        for (int y = 0; y <= size2; y += size2 / 10) {

            Line line = new Line(0, 0, size1, 0);
            line.setStroke(gridColor);
            line.setFill(gridColor);
            line.setTranslateY(y);
            line.setStrokeWidth(lineWidth);
            getChildren().addAll(line);

        }

        for (int x = 0; x <= size1; x += size1 / 10) {

            Line line = new Line(0, 0, 0, size2);
            line.setStroke(gridColor);
            line.setFill(gridColor);
            line.setTranslateX(x);
            line.setStrokeWidth(lineWidth);

            getChildren().addAll(line);

        }
        switch (dir) {
            case 'x':
                setRotationAxis(Rotate.X_AXIS);
                setRotate(90);
                setTranslateY(size3 - 0.5 * size2);
                break;
            case 'y':

                setRotationAxis(Rotate.Y_AXIS);
                setRotate(90);
                setTranslateX(size3 - 0.5 * size1);
                break;
            case 'z':
                setTranslateZ(0.5 * size3);
                break;
        }


    }

    public void setFill(Paint paint) {
        wall.setFill(paint);
    }


    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }
}