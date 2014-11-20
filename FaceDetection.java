/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacv;

/**
 *
 * 
 */
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class FaceDetection {

    public static final String XML_FILE
            = "C:\\ocv\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_default.xml";

    public static void main(String[] args) {

        IplImage img = cvLoadImage("D:\\asd\\test.jpg");
        detect(img);
    }

    public static void detect(IplImage src) {

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
        CvMemStorage storage = CvMemStorage.create();
        CvSeq sign = cvHaarDetectObjects(
                src,
                cascade,
                storage,
                1.5,
                3,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);

        int total_Faces = sign.total();

        for (int i = 0; i < total_Faces; i++) {
            CvRect r = new CvRect(cvGetSeqElem(sign, i));
            cvRectangle(
                    src,
                    cvPoint(r.x(), r.y()),
                    cvPoint(r.width() + r.x(), r.height() + r.y()),
                    CvScalar.RED,
                    2,
                    CV_AA,
                    0);

        }

        cvShowImage("Result", src);
        cvSaveImage("D:\\asd\\a.jpg", src);
        cvWaitKey(0);

    }
}
