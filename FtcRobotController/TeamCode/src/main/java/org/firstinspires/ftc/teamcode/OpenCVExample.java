package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name="EasyOpenCV", group="Auto")
public class OpenCVExample extends OpMode {

    OpenCvWebcam webcam1= null;

    @Override
    public void init(){

        WebcamName webcamName = hardwareMap.get(WebcamName.class, "webcam1");
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam1 = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);


        webcam1.setPipeline(new examplePipeline());

        webcam1.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
           public void onOpened() {
               webcam1.startStreaming(640,360, OpenCvCameraRotation.UPRIGHT);
           }


            public void onError(int errorCode) {

            }
        });



    }

    @Override
    public void loop(){

    }

    class examplePipeline extends OpenCvPipeline{
        Mat YCbCr = new Mat();
        Mat leftCrop;
        Mat rightCrop;
        double leftavgfin;
        double rightavgfin;
        Mat outPut = new Mat();
        Scalar rectColor = new Scalar(255.0, 255.0, 0.0);


        public Mat processFrame(Mat input){

            Imgproc.cvtColor(input,YCbCr,Imgproc.COLOR_RGB2YCrCb);
            telemetry.addLine("pipeline running");

            Rect leftRect = new Rect(1, 1,319,359);
            Rect rightRect = new Rect(320, 1,319,359);

            input.copyTo(outPut);
            Imgproc.rectangle(outPut, leftRect, rectColor, 2);
            Imgproc.rectangle(outPut, rightRect, rectColor, 2);

            leftCrop = YCbCr.submat(leftRect);
            rightCrop = YCbCr.submat(rightRect);

            Core.extractChannel(leftCrop, leftCrop, 1);
            Core.extractChannel(rightCrop, rightCrop, 1);

            Scalar leftavg = Core.mean(leftCrop);
            Scalar rightavg = Core.mean(rightCrop);

            leftavgfin = leftavg.val[0];
            rightavgfin = rightavg.val[0];

            if(rightavgfin < 126.5 && leftavgfin < 126.5){
                telemetry.addLine("Left");
                telemetry.addData("left ",leftavgfin);
                telemetry.addData("right ",rightavgfin);
            }
            else if (leftavgfin > rightavgfin){
                telemetry.addLine("Center");
                telemetry.addData("left", leftavgfin);
                telemetry.addData("right", rightavgfin);
            }
            else if(rightavgfin > leftavgfin){
                telemetry.addLine("Right");
                telemetry.addData("left ",leftavgfin);
                telemetry.addData("right ",rightavgfin);
            }

            return(outPut);
        }
    }

}
