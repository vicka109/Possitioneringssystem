
package tracker;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import static tracker.ObjectPositionDetect.gui;

public class ColoredObjectTrack implements Runnable {
    final int INTERVAL = 1000;// 1sec
    final int CAMERA_NUM = 0; // Default camera for this time

    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
    static CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
    static CvScalar rgba_max = cvScalar(80, 80, 255, 0);

    GUI gui = new GUI();
    IplImage image;
    CanvasFrame canvas = new CanvasFrame("Web Cam Live");
    CanvasFrame canvas2 = new CanvasFrame("Threshold");
    CanvasFrame path = new CanvasFrame("Detection");
    int ii = 0;
    JPanel jp = new JPanel();

    public ColoredObjectTrack() {
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        path.setContentPane(jp);
        gui.setVisible(true);
        
    }

    @Override
    public void run() {
        FrameGrabber grabber = new VideoInputFrameGrabber(CAMERA_NUM);
        try {
            grabber.start();
            IplImage img;
            int posX = 0;
            int posY = 0;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                    // show image on window
                    cvFlip(img, img, 1);// l-r = 90_degrees_steps_anti_clockwise
                    canvas.showImage(img);
                    IplImage detectThrs = getThresholdImage(img);
                    canvas2.showImage(detectThrs);

                    CvMoments moments = new CvMoments();
                    cvMoments(detectThrs, moments, 1);
                    double mom10 = cvGetSpatialMoment(moments, 1, 0);
                    double mom01 = cvGetSpatialMoment(moments, 0, 1);
                    double area = cvGetCentralMoment(moments, 0, 0);
                    posX = (int) (mom10 / area);
                    posY = (int) (mom01 / area);
                    // only if its a valid position
                    if (posX > 0 && posY > 0 && gui.IsStartDrawingSet()==true) {
                        paint(img, posX, posY);
                    }
                }
                // Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
        }
    }

    private void paint(IplImage img, int posX, int posY) {
        Graphics g = jp.getGraphics();
        path.setSize(img.width(), img.height());
        // g.clearRect(0, 0, img.width(), img.height());
        g.setColor(Color.RED);
        // g.fillOval(posX, posY, 20, 20);
        g.drawOval(posX, posY, 20, 20);
        System.out.println(posX + " , " + posY);

    }

    private IplImage getThresholdImage(IplImage orgImg) {
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //
        
        //cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red
        cvInRangeS(orgImg, cvScalar(gui.H_Min.getValue(), gui.S_Min.getValue(), gui.V_Min.getValue(), 0), cvScalar(gui.H_Max.getValue(), gui.S_Max.getValue(), gui.V_Max.getValue(), 0), imgThreshold);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15);
        //cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);
        return imgThreshold;
    }

    public static void main(String[] args) {
        ColoredObjectTrack cot = new ColoredObjectTrack();
        Thread th = new Thread(cot);
        th.start();
    }

    public IplImage Equalize(BufferedImage bufferedimg) {
        IplImage iploriginal = IplImage.createFrom(bufferedimg);
        IplImage srcimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        IplImage destimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
        cvEqualizeHist(srcimg, destimg);
        return destimg;
    }
}