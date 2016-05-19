package tracker_v5;

import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_MINMAX;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import java.awt.Dimension;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvMerge;
import static com.googlecode.javacv.cpp.opencv_core.cvNormalize;
import static com.googlecode.javacv.cpp.opencv_core.cvPow;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_core.cvSub;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HLS;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2YCrCb;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;

public class ObjectPositionDetect {
    static GUI gui = new GUI();
    static IplImage gr, b1, b2, gf, tr;
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
    
    
    static CanvasFrame canv1 = new CanvasFrame("Blur1");
    static CanvasFrame canv2 = new CanvasFrame("Blur2");
    static CanvasFrame canv3 = new CanvasFrame("DOG");
    
    
    private static IplImage imgThreshold;
    
    public ObjectPositionDetect(){
        gui.setVisible((true));
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
        
        moments.deallocate();
        return new Dimension(posX, posY);
    }

    static IplImage hsvThreshold(IplImage orgImg) {
        imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        
        cvInRangeS(orgImg, cvScalar(gui.H_Min.getValue(), gui.S_Min.getValue(), gui.V_Min.getValue(),0), cvScalar(gui.H_Max.getValue(), gui.S_Max.getValue(), gui.V_Max.getValue(),0), imgThreshold);

        System.gc();
        return imgThreshold;
    }
    static IplImage smoothImg (IplImage orgImg) {
        for (int i=1; i<=gui.Nothing.getValue(); i=i+1){
            cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 23);
        }
        
        System.gc(); 
        return imgThreshold;
        
    }
    
    public static IplImage gammaCorrect(IplImage img) {
        if (gui.Gamma.getValue() != 100) {
            //dela upp och utför gamma på B,G och R
            IplImage temp =  cvCreateImage(cvGetSize(img), 32, 3);
            cvConvertScale(img, temp, 1.0 / 255, 0);
            double gamma = ((double) gui.Gamma.getValue()) / 100;

            cvPow(temp, temp, gamma);
            cvConvertScale(temp, img, 255, 0);
            
            temp.release(); cvReleaseImage(temp); System.gc();
        }
        return img;
    }

    public static IplImage HistogramEqualize(IplImage img) {
        if (gui.jToggleButton1.isSelected() || gui.jToggleButton2.isSelected() || gui.jToggleButton3.isSelected()) {
            IplImage b = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);    //b
            IplImage g = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);    //g
            IplImage r = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);    //r

            cvSplit(img, b, g, r, null);       //OpenCV likes it in BGR format

            if (gui.jToggleButton1.isSelected()) {
                cvEqualizeHist(b, b);    //equalise b
                cvNormalize(b, b, 0, 255, CV_MINMAX, null);
            }
            if (gui.jToggleButton2.isSelected()) {
                cvEqualizeHist(g, g);    //equalise g
                cvNormalize(g, g, 0, 255, CV_MINMAX, null);
            }
            if (gui.jToggleButton3.isSelected()) {
                cvEqualizeHist(r, r);    //equalise r
                cvNormalize(r, r, 0, 255, CV_MINMAX, null);
            }
            cvMerge(b, g, r, null, img);
            
            b.release(); cvReleaseImage(b); System.gc();
            g.release(); cvReleaseImage(g); System.gc();
            r.release(); cvReleaseImage(r); System.gc();
        }
        return img;
    }
        
    public static IplImage DoG(IplImage img, int sigmaZero, int sigmaOne) {
        IplImage blur1 = null ,blur2 = null, temp = null;
        blur1 = cvCreateImage(cvGetSize(img), 32, 1);
        blur2 = cvCreateImage(cvGetSize(img), 32, 1);
        temp = cvCreateImage(cvGetSize(img), 8, 1);
        CvArr mask = IplImage.create(0, 0, 32, 1);
        cvCvtColor(img, temp, CV_BGR2GRAY);
        cvSmooth(temp, blur1, CV_GAUSSIAN, sigmaZero, 0, 0, 0);
        cvSmooth(temp, blur2, CV_GAUSSIAN, sigmaOne, 0, 0, 0);
        
        canv1.showImage(blur1);
        canv2.showImage(blur2);
        
        cvSub(blur1, blur2, blur2, mask);

        cvConvertScale(b2, temp, 128, 128);
        
        canv3.showImage(temp);
        
        return img;
    }
    
    public static IplImage Erode_and_Dilate(IplImage im) {
        if (gui.Erode_Replications.getValue() != 0 || gui.Dilate_Replications.getValue() != 0) {
            int erode_rep = gui.Erode_Replications.getValue();
            int dilate_rep = gui.Dilate_Replications.getValue();

            cvErode(im, im, null, erode_rep);
            cvDilate(im, im, null, dilate_rep);
        }
        return im;
    }   
}