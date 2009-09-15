package test;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

/**
 * Copyright 2007 by Gie Spaepen
 * Faculty of Medicine
 * University of Antwerp, Building T3.33
 * B-2610 Antwerp
 * Belgium
 */
public class SpinningProgressIndicator extends JPanel implements Runnable{
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new FlowLayout());
        SpinningProgressIndicator p = new SpinningProgressIndicator(true);
        p.startAnimation();
        f.getContentPane().add(p);
         p = new SpinningProgressIndicator(false);
        p.startAnimation();
        f.getContentPane().add(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
    
    
    //0.b Private Fields
    private int iSmallSize = 16;
    private int iLargeSize = 32;
    private int iTicks = 12;
    private int iSleep = 60;
    private float flStrokeWidth;
    private double dblArch = 0;
    private double dblTickArch = 360/iTicks;
    private AffineTransform atT1,atT2,atT3;
    private Color cLine;
    private Color[] cLines = new Color[4];
    private Dimension dmSize;
    private BasicStroke sTick;
    private Line2D.Double lTick;
    private Line2D.Double lSmallTick = new Line2D.Double(4,0,8,0);
    private Line2D.Double lLargeTick = new Line2D.Double(8,0,14,0);
    private Thread thread;
    
    
    /** Boolean to indicate that a large spinner (32x32 pixels) is needed*/
    public static boolean LARGESPINNER = false;
    /** Boolean to indicate that a small spinner (16x16 pixels) is needed*/
    public static boolean SMALLSPINNER = true;
    public static Color COLOR = new Color(100,100,100);
    public Color cBack = new Color(0,0,0,0);
    
    //1. Constructors
    /**
     * Void constructor.  By default a small spinner (16x16 pixels) is created
     * and the standard color (100,100,100) is used
     */
    public SpinningProgressIndicator() {
        initSpinner(this.SMALLSPINNER,this.COLOR);
    }
    /**
     * This  constructor creates a small spinner (16x16 pixels) with a custom color
     */
    public SpinningProgressIndicator(Color color) {
        initSpinner(this.SMALLSPINNER,color);
    }
    /**
     * This constructor can create a small or a large spinner with a custom color
     */
    public SpinningProgressIndicator(boolean smallsize, Color color) {
        initSpinner(smallsize,color);
    }
    /**
     * Recommended constructor to create a small or a large spinner with the default color
     */
    public SpinningProgressIndicator(boolean smallsize){
        initSpinner(smallsize,this.COLOR);
    }
    
    //2. Methods
    
    //2.a Initialization function
    private void initSpinner(boolean size,Color color){
        //Set size
        dmSize = (size == this.SMALLSPINNER)? new Dimension(iSmallSize,iSmallSize):
            new Dimension(iLargeSize,iLargeSize);
        //Set the line color
        cLine = color;
        //Set the stroke width
        flStrokeWidth = (size == this.SMALLSPINNER)?1.5f:3.0f;
        //Set the stroke of the tick
        sTick = new BasicStroke(flStrokeWidth, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        
        //Set tick line2d object
        lTick = (size == this.SMALLSPINNER)? lSmallTick:lLargeTick;
        //Generate color alpha blend
        int numOfBlends = Math.round(iTicks/2);
        cLines = new Color[numOfBlends];
        for(int i = 0;i<numOfBlends;i++){
            int alpha = Math.round(255-((255-100)*i/numOfBlends));
            cLines[i] = new Color(cLine.getRed(),cLine.getGreen(),cLine.getBlue(),alpha);
        }
        super.setOpaque(false);
    }
    
    //2.b-g Handle the size of the component as defined by dmSize
    public Dimension getMaximumSize(){return dmSize;}
    public Dimension getMinimumSize(){return dmSize;}
    public Dimension getPreferredSize(){return dmSize;}
    public void setMaximumSize(Dimension d){}
    public void setMinimumSize(Dimension d){}
    public void setPreferredSize(Dimension d){}
    
    //2.h Paint the spinner
    public void paintComponent(Graphics g){
        
        //Repaint background
        //super.repaint();
        //Get the graphics
        Graphics2D g2d = (Graphics2D) g.create();
        //Set the Graphic2D hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Paint the transparent background
        g2d.setColor(cBack);
        g2d.fillRect(0,0,getWidth(),getHeight());
        
        
        //Middle of the square
        double dblMiddle = dmSize.getHeight()/2;
        
        //Define transformations
        atT1 = AffineTransform.getTranslateInstance(dblMiddle,dblMiddle);
        atT2 = AffineTransform.getRotateInstance(Math.toRadians(dblArch));
        atT3 = AffineTransform.getRotateInstance(Math.toRadians(dblTickArch));
        
        //Make affinity transform and transform graphics
        atT1.concatenate(atT2);
        g2d.transform(atT1);
        
        //Set stroke
        g2d.setStroke(sTick);
        
        //Draw ticks
        for(int i = iTicks;i > 0;i--){
            if(i <= cLines.length){
                g2d.setColor(cLines[i-1]);
            } else{
                g2d.setColor(cLines[cLines.length-1]);
            }
            g2d.transform(atT3);
            g2d.draw(lTick);
            
        }
        
        //Get rid of the graphics
        g2d.dispose();
        
    }
    
    //2.i Function to start the animation
    /**
     * Use this function to start the spinner animation
     */
    public void startAnimation(){
        if(thread == null){
            thread = new Thread(this, "SpinningProgressIndicator");
            thread.start();
        }
    }
    
    //2.j Function to stop it
    /**
     * Use this function to stop the animation
     */
    public void stopAnimation(){
        if(thread != null){
            thread = null;
        }
    }
    
    //2.k We implemented Runnable, didn't we?
    /**
     * It is not recommended to use this function.
     * Use startAnimation() and stopAnimation() instead
     */
    public void run() {
        while(thread == Thread.currentThread()){
            if (dblArch >= 360) {
                dblArch = 0 + (360 / iTicks);
            } else {
                dblArch += 360 / iTicks;
            }
            this.repaint();
            try {
                thread.sleep(iSleep);
            } catch (InterruptedException ex) {}
        }
    }
    
    
}
