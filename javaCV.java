/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javacv;

import com.googlecode.javacv.CanvasFrame;//
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;//
import com.googlecode.javacv.cpp.opencv_core.CvRect;//
import com.googlecode.javacv.cpp.opencv_core.CvScalar;//
import com.googlecode.javacv.cpp.opencv_core.CvSeq;//
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;//
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import com.googlecode.javacv.cpp.opencv_highgui;//
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;//
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;//
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;




/**
 *
 * @author dell
 */
public class JavaCV {
    
    /**
     * @param args the command line arguments
     */
    static jssc connect = new jssc();
    
    public static void main(String[] args) {
        // TODO code application logic here
        //TO open individual images
        //IplImage originalImage = cvLoadImage("D:\\asd\\test.jpg",1);
        //connect.con();
        CvCapture img = opencv_highgui.cvCreateCameraCapture(0);
        opencv_highgui.cvSetCaptureProperty(img, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 360);
        opencv_highgui.cvSetCaptureProperty(img, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 360);
        
        IplImage grabbed = opencv_highgui.cvQueryFrame(img);
        //IplImage grabbed = originalImage;
        CanvasFrame frame = new CanvasFrame("Frame");
        int j =1;
        while(frame.isVisible() && (grabbed = opencv_highgui.cvQueryFrame(img))!=null){
            try {
                j++;
                grabbed = FD.detect(grabbed,j,connect);
            } catch (Exception ex) {
                Logger.getLogger(JavaCV.class.getName()).log(Level.SEVERE, null, ex);
            }
            frame.showImage(grabbed);
            //System.out.println("doing");
        }        
        
    }
    
}

class FD {
 
    // The cascade definition to be used for detection.
    private static final String CASCADE_FILE = "C:\\ocv\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml";
 
    public static IplImage detect(IplImage originalImage,int j,jssc connect) throws Exception {
         
 
        // Load the original image.
        //IplImage originalImage = cvLoadImage("D:\\FaceDetection\\test\\aloeL.jpg",1);
 
        // We need a grayscale image in order to do the recognition, so we
        // create a new image of the same size as the original one.
        IplImage grayImage = IplImage.create(originalImage.width(),
                originalImage.height(), IPL_DEPTH_8U, 1);
 
        // We convert the original image to grayscale.
         cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);
 
        CvMemStorage storage = CvMemStorage.create();
 
        // We instantiate a classifier cascade to be used for detection, using
        // the cascade definition.
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
                cvLoad(CASCADE_FILE));
 
        // We detect the faces.
        CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, 1,
                0);
        
        // We iterate over the discovered faces and draw yellow rectangles
        // around them.
        //System.out.println(faces.total());
        for (int i = 0; i < faces.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            cvRectangle(originalImage, cvPoint(r.x(), r.y()),
                    cvPoint(r.x() + r.width(), r.y() + r.height()),
                    CvScalar.YELLOW, 1, CV_AA, 0);
        }
                
        // Save the image to a new file.
        if(faces.total()>0){
            connect.con();
            connect.send(49);
            //cvSaveImage("D:\\asd\\"+j+".jpg", originalImage);
        }    
        
        return originalImage;
    }
}


class jssc{
    char res;
    SerialPort sp = new SerialPort("COM54");

    public void con() {
        try {
            System.out.println("starting");
            sp.openPort();
            sp.setParams(9600, 8, 1, 0);
            //System.out.println(send(49));
            System.out.println("Success!!");
        } catch (SerialPortException ex) {
            System.out.println("fail!!!");            
            Logger.getLogger(jssc.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
    public String send(int val){
        try {
            sp.writeInt(val);
            sp.closePort();
            System.out.println("port closed 1");
        } catch (SerialPortException ex) {
            try {
                sp.closePort();System.out.println("port closed 2");
                Logger.getLogger(jssc.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SerialPortException ex1) {
                Logger.getLogger(jssc.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }        
        return "done"; 
    }
}

