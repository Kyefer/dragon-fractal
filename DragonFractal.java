
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Eddie
 */
public class DragonFractal {

    //    private final int WIDTH = 1900;
//    private final int HEIGHT = 1000;
//    // For 1080p, use 400, 1350, and 330 for START_X1, START_X2, and START_Y respectively
    private final int START_X1 = 400;
    private final int START_X2 = 1350;
    private final int START_Y = 330;

    private long lastFrame;
    private ArrayList<double[]> points;
    private long elapsedTime = 0;

    // Settings
    private boolean paused = false;
    private int iteration = 0;
    private boolean savePrevious = false;
    private int maxIterations = 18;
    private int speed = 1000;

    public static void main(String[] args) {
        DragonFractal fractal = new DragonFractal();
        fractal.draw();
    }

    public DragonFractal() {
        initGL();
    }


    private void initGL() {
        try {
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setFullscreen(true);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        glColor3f(0,0,0);
        glClearColor(1,1,1,1);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    private void drawLine(double[] point1, double[] point2) {
        glBegin(GL_LINES);
        glVertex2d(point1[0], point1[1]);
        glVertex2d(point2[0], point2[1]);
        glEnd();
    }

    public void draw() {
        lastFrame = getTime();
        updatePoints(iteration);


        while (!Display.isCloseRequested()) {
            if (!paused) {
                elapsedTime += getDelta();
            }

            drawFractal();

            if (elapsedTime >= speed && iteration < maxIterations) {
                updatePoints(++iteration);
                elapsedTime = 0;
            }

            if (iteration > maxIterations) {
                iteration = maxIterations;
                updatePoints(iteration);
            }

            pollInput();

            Display.update();
        }
        Display.destroy();
    }

    private void drawFractal() {
        if (!savePrevious) {
            glClear(GL_COLOR_BUFFER_BIT);
        }
        for (int i = 0; i < points.size() - 1; i++) {
            drawLine(points.get(i), points.get(i + 1));
        }
    }

    private void updatePoints(int n) {
        points = new ArrayList<>((int) Math.pow(2, n));
        points.add(new double[]{START_X1, START_Y});
        points.add(new double[]{START_X2, START_Y});
        for (int i = 0; i < n; i++) {
            int sign = 1;
            for (int j = 0; j < points.size() - 1; j += 2, sign *= -1) {

                double dx = points.get(j + 1)[0] - points.get(j)[0];
                double dy = points.get(j + 1)[1] - points.get(j)[1];

                double x = Math.sqrt(dx * dx + dy * dy) / Math.sqrt(2)
                        * Math.cos((Math.atan2(dy, dx) + sign * Math.PI / 4)) + points.get(j)[0];
                double y = Math.sqrt(dx * dx + dy * dy) / Math.sqrt(2)
                        * Math.sin((Math.atan2(dy, dx) + sign * Math.PI / 4)) + points.get(j)[1];

                points.add(j + 1, new double[]{x, y});
            }

        }
    }

    private void pollInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_ESCAPE:
                        System.exit(0);
                        break;
                    case Keyboard.KEY_P:
                        paused = !paused;
                        break;
                    case Keyboard.KEY_R:
                        iteration = 0;
                        elapsedTime = 0;
                        updatePoints(iteration);
                        break;
                    case Keyboard.KEY_UP:
                        maxIterations++;
                        break;
                    case Keyboard.KEY_DOWN:
                        maxIterations--;
                        break;
                    case Keyboard.KEY_RIGHT:
                        speed += 500;
                        break;
                    case Keyboard.KEY_LEFT:
                        if (speed >= 500)
                            speed -= 500;
                        break;
                }
            }
            logSettings();
        }
    }

    private void logSettings() {
        System.out.printf("Paused: %b\n", paused);
        System.out.printf("Speed: %d\n", speed);
        System.out.printf("Max: %d\n", maxIterations);
    }

    private int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;

    }

    private long getTime() {
        return System.nanoTime() / 1000000;
    }

}
