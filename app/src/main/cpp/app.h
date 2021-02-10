//
// Created by 0rbianta
//
#ifndef ANDROID_CASCADE_NDK_APP_H
#define ANDROID_CASCADE_NDK_APP_H
#if __cplusplus

using namespace std;
using namespace cv;


CascadeClassifier face_cascade;

jobject getApplicationContext(JNIEnv *env, jobject thiz){
    jclass cc = env->FindClass("android/content/Context");
    jmethodID cm = env->GetMethodID(cc,"getApplicationContext", "()Landroid/content/Context;");
    return (jobject)env->CallObjectMethod(thiz,cm);
}


void showToast(JNIEnv *env, jobject thiz, string content){

    jobject context = getApplicationContext(env, thiz);
    jstring jcontent = env->NewStringUTF(content.c_str());




    jclass toast_class = env->FindClass("android/widget/Toast");
    jmethodID makeText = env->GetStaticMethodID(toast_class,"makeText","(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jobject toast_init = env->CallStaticObjectMethod(toast_class,makeText,context,jcontent,1);
    jmethodID show = env->GetMethodID(toast_class,"show", "()V");
    env->CallVoidMethod(toast_init,show);


}


void load_cascade(string cascade_path_l){
    if(face_cascade.empty())    face_cascade.load(cascade_path_l);
}

vector<Rect> detect_objects(CascadeClassifier cc, Mat image_grays_d){
    vector<Rect> build;
    cc.detectMultiScale(image_grays_d, build);
    return build;
}


Mat draw_rect(Mat image, Rect obj, Scalar color_RGB){

    rectangle(image,Point(obj.x,obj.y),
            Point(obj.x+obj.width,obj.y+obj.height),
            color_RGB, 2);
    return image;
}

Mat draw_rects_by_vector(Mat image, vector<Rect> objs, Scalar color){

    for(size_t i=0;i<objs.size();i++){
        Rect prect = objs[i];
        image = draw_rect(image,prect,color);

    }

    return image;
}


class img_opr{

public:

    Mat RGB2GRAY(Mat image){
        Mat build;
        cvtColor(image, build, COLOR_RGB2GRAY);
        equalizeHist(build, build);
        return build;
    }
    Mat GRAY2RGB(Mat image){
        Mat build;
        cvtColor(image, build, COLOR_GRAY2RGB);
        equalizeHist(build, build);
        return build;
    }

};




#endif //__cplusplus
#endif //ANDROID_CASCADE_NDK_APP_H
