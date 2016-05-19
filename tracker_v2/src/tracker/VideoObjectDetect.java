package tracker;

import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import java.awt.Dimension;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.text.StyleConstants.Size;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.MORPH_CROSS;

public class VideoObjectDetect implements Runnable {
    //final int INTERVAL=1000;///you may use interval
    IplImage image;
    CanvasFrame canvas = new CanvasFrame("Threshold WebCam");
    CanvasFrame canvas2 = new CanvasFrame("BGR WebCam");
    CanvasFrame canvas3 = new CanvasFrame("HSV WebCam");
    CanvasFrame canvas4 = new CanvasFrame("Eroded and Dilated");
    ObjectPositionDetect opd = new ObjectPositionDetect();
    IplImage img_ORG;
    IplImage img_BGR;
    IplImage img_HSV;
    
    
    
    public VideoObjectDetect() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas3.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            System.out.println("grab");
            OpenCVFrameGrabber grabber_ORG = new OpenCVFrameGrabber(0); // 1 for next camera
            OpenCVFrameGrabber grabber_BGR = grabber_ORG;
            OpenCVFrameGrabber grabber_HSV = grabber_BGR;
            int i=0;
            try {
            grabber_ORG.start();
            grabber_BGR.start();
            grabber_HSV.start();
            
            
            while (true) {
            img_ORG = grabber_ORG.grab();
            img_BGR = grabber_BGR.grab();
            img_HSV = grabber_HSV.grab();
            
            canvas2.showImage(img_BGR);
            
            cvCvtColor(img_BGR, img_HSV, CV_BGR2HSV);
            
            
            
            canvas3.showImage(img_HSV);
            if (img_ORG != null) {
            System.out.println("writing image");
            //cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
            //cvSaveImage((i++)+"-aa.jpg", img);
            
            // convert image
            IplImage thresholdImage = ObjectPositionDetect.hsvThreshold(img_ORG);
            
            cvSaveImage("hsvthreshold.jpg", thresholdImage);
            Dimension position = ObjectPositionDetect.getCoordinates(thresholdImage);
            System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
            System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
            canvas.showImage(thresholdImage);
            
            canvas4.showImage(ObjectPositionDetect.Erode_and_Dilate(thresholdImage));
            }
            //Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        }
        } catch (InterruptedException ex) {
            Logger.getLogger(VideoObjectDetect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
  public static void main(String[] args) {
        VideoObjectDetect gs = new VideoObjectDetect();
        Thread th = new Thread(gs);
        th.start();
    }
}