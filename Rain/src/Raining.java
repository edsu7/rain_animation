
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Raining extends java.lang.Object
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame (); //creates instance of a window
        frame.setSize(800,300);
        final RPanel rPanel = new RPanel(); //???????
        frame.add(rPanel);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                rPanel.stop();
                System.exit(0);
            }
        });
}
}

class RPanel extends JPanel{
    //SETTINGS
    private double mRainChance = 0.99; //from 0 to 1
    private float mGravity = 9.8f;
        
    private float mRainWidth = 1;
    private int mRepaintTimeMS = 16; //?????
    private double mDropInitialVelocity = 20;
        
    private Color mColor = new Color(0,0, 255);
    //SETTINGS; NEEDS CLASSES
    
    private ArrayList<Rain> rainV; //in Java, arrays are fixed 
    //private ArrayList<Drop> dropV; //whilst arraylist are not
    private UpdateThread mUpdateThread;
    
    public RPanel() {
        rainV = new ArrayList<>();
        //dropV = new ArrayList<>();
        
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();
    }
    
    public void stop() {
        mUpdateThread.stopped=true;
    }
    
    public int getHeight(){
        return this.getSize().height;
    }
    public int getWidth(){
        return this.getSize().width;
    }
    private class UpdateThread extends Thread {
        public volatile boolean stopped = false; //atomic vs volatile
                                                 //i.e. how to evaluate conditions
        @Override
        public void run(){
            while(!stopped){
                RPanel.this.repaint();
                    try{
                        Thread.sleep(mRepaintTimeMS);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
     @Override
     public void paintComponent(Graphics g){
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g;
         g2.setStroke(new BasicStroke(mRainWidth));
         g2.setColor(mColor);
         
         //DRAW Rain
         Iterator<Rain> iterator = rainV.iterator(); //iterates through elements
         while (iterator.hasNext()){
             Rain rain = iterator.next();
             rain.update();
             rain.draw(g2);
             
             if(rain.y >=getHeight()){
                 iterator.remove();
             }
         }
         //CREATE NEW Rain
         if (Math.random() < mRainChance){
             rainV.add(new Rain());
         }
         
     }
     
     class Rain{
         float x;
         float y;
         float prevX;
         float prevY;
         //rain starts randomly
         public Rain(){
             Random r = new Random();
             x = r.nextInt(getWidth());
             y = 0;
         }
         public void update(){
             prevX = x;
             prevY = y;
             
             y+=mGravity;
         }
         
         public void draw(Graphics2D g2){
             Line2D line = new Line2D.Double(x, y, prevX, prevY);
             g2.draw(line);
         }
     }
}
