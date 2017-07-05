#include <jni.h>
#include <string>
#include <bits/stdc++.h>
//#include <asio.hpp>
using namespace std;
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_cppproject_MainActivity_foo(JNIEnv *env, jobject instance, jstring json_) {
    setlocale(LC_ALL, "Rus");
    const char *json = env->GetStringUTFChars(json_, 0);
    string ss = json;
    string s1 = "";
    string ms[6];
    int q = ss.length();
    int i = 0;
    while(i < 5){
        int j = ss.find_first_of('"');
        ss.erase(0, j + 1);
        int k = ss.find_first_of('"');
        s1 += ss.substr(0, k);
        s1 += "|";
        ss.erase(0, k + 1);
        i++;
    }
    env->ReleaseStringUTFChars(json_, json);
    return env->NewStringUTF(s1.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_example_cppproject_MainActivity_fo(JNIEnv *env, jobject instance, jstring json_, jint q) {
    setlocale(LC_ALL, "Rus");
    const char *json = env->GetStringUTFChars(json_, 0);
    string ss = json;
    string s1 = "";
    int i = 0;
    while(i < 5){
        int j = ss.find_first_of('"');
        ss.erase(0, j + 1);
        int k = ss.find_first_of('"');
        s1 = ss.substr(0, k);
        ss.erase(0, k + 1);
        i++;
    }

    env->ReleaseStringUTFChars(json_, json);
    return env->NewStringUTF(s1.c_str());

}
JNIEXPORT jstring JNICALL
Java_com_example_cppproject_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {


    string s1 = "";



    return env->NewStringUTF(s1.c_str());
}

JNIEXPORT jobjectArray JNICALL
Java_com_example_cppproject_MainActivity_yeah(JNIEnv *env, jobject instance, jstring s_) {
    const char *s = env->GetStringUTFChars(s_, 0);

    setlocale(LC_ALL, "Rus");
    string ss = s;

}
