//
// Created by 0rbianta
//
#include <jni.h>
#include <string>

#include <opencv2/opencv.hpp>

#include "app.h"




extern "C"
JNIEXPORT void JNICALL
Java_github_orbianta_androidcascadendk_MainActivity_detect_1faces(JNIEnv *env, jobject thiz, jlong image_addr, jstring cascade_path) {

    Mat& image = *(Mat*)image_addr;

    string ccascade_path = env->GetStringUTFChars(cascade_path,0);

    load_cascade(ccascade_path);

    img_opr imgOpr;

    Mat image_grayscale = imgOpr.RGB2GRAY(image);

    vector<Rect> faces = detect_objects(face_cascade, image_grayscale);

    draw_rects_by_vector(image, faces, Scalar(/*Purple RGB color*/103,61,252));

    showToast(env, thiz, "Cascade detection with NDK complete.");

}