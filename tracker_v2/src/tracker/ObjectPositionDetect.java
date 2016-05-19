package tracker;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import java.awt.Dimension;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import javax.swing.JSlider;

public class ObjectPositionDetect {
    //static int hueLowerR = 160;
    //static int hueUpperR = 180;
    static GUI gui = new GUI();
    static private javax.swing.JSlider H_Max;
    static private javax.swing.JSlider H_Min;
    static private javax.swing.JSlider S_Max;
    static private javax.swing.JSlider S_Min;
    static private javax.swing.JSlider V_Max;
    static private javax.swing.JSlider V_Min;
    static private javax.swing.JSlider hueLowerR;
    static private javax.swing.JSlider hueUpperR;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    
    public ObjectPositionDetect(){
        
        gui.setVisible((true));
    }
    

    public static void main(String[] args) {
        IplImage orgImg = cvLoadImage("C:\\Users\\Victor\\Desktop\\TNK111\\Individuellt med Noor\\Test Objectetect\\redSpot.jpg");
        IplImage thresholdImage = hsvThreshold(orgImg);
        cvSaveImage("hsvthreshold.jpg", thresholdImage);
        Dimension position = getCoordinates(thresholdImage);
        System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
        System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
    
    }

    static Dimension getCoordinates(IplImage thresholdImage) {
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)•xj•yi)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);
        return new Dimension(posX, posY);
    }

    static IplImage hsvThreshold(IplImage orgImg) {
        
        // 8-bit, 3- color =(RGB)
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        // cvScalar : ( H , S , V, A)
        //cvInRangeS(imgHSV, cvScalar(hueLowerR, gui.getValueofSlider(H_Min), gui.getValueofSlider(S_Min), gui.getValueofSlider(V_Min)), cvScalar(hueUpperR, gui.getValueofSlider(H_Max), gui.getValueofSlider(S_Max), gui.getValueofSlider(V_Max)), imgThreshold);
        cvInRangeS(imgHSV, cvScalar(gui.H_Min.getValue(), gui.S_Min.getValue(), gui.V_Min.getValue(), 0), cvScalar(gui.H_Max.getValue(), gui.S_Max.getValue(), gui.V_Max.getValue(), 0), imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
    
   static IplImage Erode_and_Dilate(IplImage im){
       
        int erode_rep=gui.Erode_Replications.getValue();
        int dilate_rep= gui.Dilate_Replications.getValue();       
        
        cvErode(im, im, null, erode_rep);
        cvDilate(im, im, null, dilate_rep);
        
        return im;
    }
}