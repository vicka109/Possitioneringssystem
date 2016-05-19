package tracker_v5;
import java.awt.Dimension;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
public class VideoObjectDetect implements Runnable {
    //Deklarera de fönster som bilder skall visas i
    CanvasFrame canvas = new CanvasFrame("Threshold WebCam");
    CanvasFrame canvas2 = new CanvasFrame("BGR WebCam");
    CanvasFrame canvas3 = new CanvasFrame("HSV WebCam");
    CanvasFrame canvas4 = new CanvasFrame("Eroded and Dilated");
    CanvasFrame canvas5 = new CanvasFrame("YCrCb WebCam");
    CanvasFrame canvas6 = new CanvasFrame("Org WebCam");
    CanvasFrame canvas8 = new CanvasFrame("HLS WebCam");
    CanvasFrame canvas9 = new CanvasFrame("CIE_Luv WebCam");
    CanvasFrame canvas10 = new CanvasFrame("CIE_Lab WebCam");
    //Deklarera ObjektPositionDetect som inehåller funktioner som används för 
    //att bearbeta bilder
    ObjectPositionDetect opd = new ObjectPositionDetect();
    //Deklarera de bildobjekt som behövs
    IplImage img_ORG;
    IplImage img_BGR;
    IplImage img_HSV;
    IplImage img_YCrCb;
    IplImage img_HLS;
    IplImage img_CIE_Lab;
    IplImage img_CIE_Luv;
    IplImage thresholdImage;
    public VideoObjectDetect() {
    }
    @Override
    public void run() {
    	System.out.println("Starting camera");
        //Starta kommunikation med kameran
        // 0 datorns kamera
        // 1 extern kamera 
        // "TESTrörelse2.mp4" för test fil
        OpenCVFrameGrabber grabber_ORG = new OpenCVFrameGrabber(0); 
        /*
        //Använd denna kod om en bild ska behandlas istället för en video.......
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("ljusEx2.jpg") );
        } catch (IOException ex) {
            Logger.getLogger(VideoObjectDetect.class.getName()).log(Level.SEVERE, null, ex);
        }
        IplImage origImg = IplImage.createFrom(img);
        //......................................................................
        */
        /*
        //Använd om FPS ska beräknas
        long millisStart = System.currentTimeMillis();
        long millisElapsed = System.currentTimeMillis();
        double FPS=0;
        */
        try {
            grabber_ORG.start();
            Thread.sleep(5000);
            /*
            //Använd om FPS ska beräknas
            millisStart=System.currentTimeMillis();
            double count2=0;
            */
            while (true) {
                /*
                //Använd denna kod om en bild ska behandlas istället för en video.......
                try {
                    img = ImageIO.read(new File("ljusEx2.jpg"));
                } catch (IOException ex) {
                    Logger.getLogger(VideoObjectDetect.class.getName()).log(Level.SEVERE, null, ex);
                }
                origImg = IplImage.createFrom(img);
                //..............................................................
                */
                img_ORG = grabber_ORG.grab(); //om bild istället för video (origImg;) 
                canvas6.showImage(img_ORG);
                /*
                //Använd om BGR ska användas
                img_BGR=grabber_ORG.grab();         
                //img_BGR = ObjectPositionDetect.gammaCorrect(img_BGR);
                //img_BGR = ObjectPositionDetect.HistogramEqualize(img_BGR);   
                //img_BGR = ObjectPositionDetect.process(img_BGR);
                canvas2.showImage(img_BGR);
                */
                /* 
                //Använd om HSV ska användas
                img_HSV = grabber_ORG.grab();
                cvCvtColor(img_HSV,  img_HSV, CV_BGR2HSV);                
                img_HSV = ObjectPositionDetect.gammaCorrect(img_HSV);
                img_HSV = ObjectPositionDetect.HistogramEqualize(img_HSV);
                canvas3.showImage(img_HSV);
                */
                /* 
                //Använd om YCrCb ska användas
                img_YCrCb = grabber_ORG.grab();
                cvCvtColor(img_YCrCb, img_YCrCb, CV_BGR2YCrCb);
                img_YCrCb = ObjectPositionDetect.gammaCorrect(img_YCrCb);
                img_YCrCb = ObjectPositionDetect.HistogramEqualize(img_YCrCb);
                canvas5.showImage(img_YCrCb);
                */
                
                
                img_HLS = grabber_ORG.grab(); 
                cvCvtColor(img_HLS, img_HLS, CV_BGR2HLS); 
                img_HLS = ObjectPositionDetect.gammaCorrect(img_HLS);
                img_HLS = ObjectPositionDetect.HistogramEqualize(img_HLS);
                canvas8.showImage(img_HLS);
                
                
                /*
                //Använd om CIE_Lab ska användas
                img_CIE_Lab = grabber_ORG.grab();
                cvCvtColor(img_CIE_Lab, img_CIE_Lab, CV_BGR2Lab);
                img_YCrCb = ObjectPositionDetect.gammaCorrect(img_CIE_Lab);
                img_YCrCb = ObjectPositionDetect.HistogramEqualize(img_CIE_Lab);
                canvas10.showImage(img_CIE_Lab);
                */
                
                /*
                //Använd om CIE_Luv ska användas
                img_CIE_Luv = grabber_ORG.grab();
                cvCvtColor(img_CIE_Luv, img_CIE_Luv, CV_BGR2Luv);
                img_CIE_Luv = ObjectPositionDetect.gammaCorrect(img_CIE_Luv);
                img_CIE_Luv = ObjectPositionDetect.HistogramEqualize(img_CIE_Luv);
                canvas9.showImage(img_CIE_Luv);
                */
                
                if (img_ORG != null || img_HLS != null) {
                    // convert image
                    thresholdImage = ObjectPositionDetect.hsvThreshold(img_HLS);
                    
                    Dimension position = ObjectPositionDetect.getCoordinates(thresholdImage);
                    System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
                    System.out.println("Position of object    : x : " + position.width + " , y : " + position.height);
                    
                    //Visa theshold image
                    canvas.showImage(thresholdImage);
                    thresholdImage=ObjectPositionDetect.Erode_and_Dilate(thresholdImage);
                    //Visa eroded and dilated image
                    canvas4.showImage(ObjectPositionDetect.smoothImg(thresholdImage));
                    
                    //Frigör bildobjektet för att minska "memory leak"
                    thresholdImage.release(); System.gc();
                }
                
                /*
                //Använd om FPS ska beräknas
                millisElapsed = System.currentTimeMillis();
                count2=count2+1;
                
                FPS=(count2/((double)(millisElapsed-millisStart)/1000));
                System.out.println(count2);
                System.out.println(((double)(millisElapsed-millisStart)/1000));
                System.out.println(FPS);
                */
            }
        } catch (Exception e) {
        }
    }

  public static void main(String[] args) {
        VideoObjectDetect gs = new VideoObjectDetect();
        
        Thread th = new Thread(gs);
        
        th.start();
    }
}