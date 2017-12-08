package render;

import game.Aeroplane;
import game.CollisionPoint;
import game.TerrainSegment;
import game.World;
import util.ImageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * Created by Pontus on 2017-12-06.
 */
public class Renderer {
    private final int WIDTH = 960;
    private final int HEIGHT = 540;

    private JPanel surface;
    private World world;
    private BufferedImage aeroplaneImage;

    private double scale = 0.3;
    private double planeX = WIDTH * 0.5;
    private double planeY = HEIGHT * 0.5;

    public Renderer(World world) {
        this.world = world;
    }

    public void start(KeyListener keyListener) {
        aeroplaneImage = ImageHandler.scaleImage(ImageHandler.loadImage("aeroplane_anim"), scale);

        JFrame frame = new JFrame("AeroTrials");
        frame.setSize((int)(WIDTH), (int)(HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        surface = new JPanel() {
            @Override
            public void paintComponent(Graphics graphics) {
                render((Graphics2D) graphics);
            }
        };
        surface.setFocusable(true);
        surface.setBackground(Color.black);
        surface.addKeyListener(keyListener);

        frame.add(surface);
        frame.setVisible(true);
    }

    public void update() {
        surface.repaint();
    }

    private void render(Graphics2D g) {
        configGraphics(g);
        g.setColor(new Color(53, 53, 53));
        g.fillRect(0,0,WIDTH, HEIGHT);
        renderTerrain(g);
        renderAeroplane(g);
       // renderCollisionPoints(g);
        renderStats(g);
    }

    private void configGraphics(Graphics2D g) {
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);
    }

    private void renderAeroplane(Graphics2D g) {
        Aeroplane aeroplane = world.getAeroplane();
        int topLeftX = (int)((planeX - (Aeroplane.CG_X) * scale));
        int topLeftY = (int)((planeY - (Aeroplane.CG_Y) * scale));
        g.rotate(aeroplane.getRotation(), (int)(planeX), (int)(planeY));
        g.drawImage(aeroplaneImage, topLeftX, topLeftY, surface);
        g.rotate(-aeroplane.getRotation(), (int)(planeX), (int)(planeY));
    }

    private void renderCollisionPoints(Graphics2D g) {
        Aeroplane aeroplane = world.getAeroplane();
        for (CollisionPoint cp : aeroplane.getCollisionPoints()) {
            if (cp.isColliding()) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GREEN);
            }
            g.fillRoundRect(
                    (int)((cp.getX() - aeroplane.getX()) * scale + planeX - 5),
                    (int)((cp.getY() - aeroplane.getY()) * scale + planeY - 5),
                    10, 10, 10, 10);
        }
    }

    private void renderTerrain(Graphics2D g) {
        g.setColor(Color.WHITE);
        Aeroplane aeroplane = world.getAeroplane();
        boolean other = true;
        for (TerrainSegment segment : world.getTerrain()) {
            if (other) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
            }
            other = !other;
            g.drawLine(
                    (int)((segment.getX1() - aeroplane.getX()) * scale + planeX),
                    (int)((segment.getY1() - aeroplane.getY()) * scale + planeY),
                    (int)((segment.getX2() - aeroplane.getX()) * scale + planeX),
                    (int)((segment.getY2() - aeroplane.getY()) * scale + planeY)
            );
        }
    }

    private void renderStats(Graphics2D g) {
        Aeroplane aeroplane = world.getAeroplane();

        g.setColor(Color.WHITE);
        g.drawString("Speed: " + (int)aeroplane.getSpeed() + "m/s (" +
                (int)(aeroplane.getSpeed() * 3.6) + " km/h)",
                10, 20);
        g.drawString("Throttle: " + (int)(aeroplane.getThrottle() * 100) + "%", 10, 35);
        g.drawString("Altitude: " + (int)(-aeroplane.getY() / World.ONE_METER) + " m", 10, 50);
        g.drawString("Score: " + (int)(aeroplane.getX() / World.ONE_METER) + " m", 10, 65);
    }
}
