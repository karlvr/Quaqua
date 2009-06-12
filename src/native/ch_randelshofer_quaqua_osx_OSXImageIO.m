/*
 * @(#)ch_randelshofer_quaqua_osx_OSXImageIO.m
 *
 * Copyright (c) 2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer.
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * Werner Randelshofer. For details see accompanying license terms.
 */

/**
 * Native code for class ch.randelshofer.quaqua.osx.OSXImageIO.
 *
 * @version $Id$
 */

#include <stdio.h>
#include <jni.h>
#include "ch_randelshofer_quaqua_osx_OSXImageIO.h"
#import <Cocoa/Cocoa.h>
#import <CoreServices/CoreServices.h>
/*
 * Class:     ch_randelshofer_quaqua_osx_OSXImageIO
 * Method:    nativeRead
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXImageIO_nativeRead__Ljava_lang_String_2
(JNIEnv *env, jclass javaClass, jstring file) {

    if(file == NULL) return NULL;

    jbyteArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, file, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
                                               length:(*env)->GetStringLength(env, file)];
    // Release the C char array
    (*env)->ReleaseStringChars(env, file, pathC);

    // Get the image
    NSImage* image = [[NSImage alloc] initWithContentsOfFile:pathNS];
    if (image != NULL) {
        NSData* data = [image TIFFRepresentation];
        if (data != NULL) {
            unsigned len = [data length];
            void* bytes = malloc(len);
            [data getBytes:bytes];

            result = (*env)->NewByteArray(env, len);
            (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)bytes);
            free(bytes);
        }
    }

    // Release memory pool
	[pool release];

	return result;
}
/*
 * Class:     ch_randelshofer_quaqua_osx_OSXImageIO
 * Method:    nativeRead
 * Signature: (Ljava/lang/String;II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXImageIO_nativeRead__Ljava_lang_String_2II
(JNIEnv *env, jclass javaClass, jstring file, jint width, jint height) {

    if(file == NULL) return NULL;

    jbyteArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, file, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
                                               length:(*env)->GetStringLength(env, file)];
    // Release the C char array
    (*env)->ReleaseStringChars(env, file, pathC);

    // Get the icon image
    NSImage* image = [[NSImage alloc] initWithContentsOfFile:pathNS];
    if (image != NULL) {
        // Set the desired size of the image
        NSSize desiredSize = { width, height };
        [image setSize:desiredSize];

        // Unfortunately, setting the desired size does not always have an effect,
        // we need to choose the best image representation by ourselves.
        NSImageRep* imageRep;
        NSData* data = NULL;
        NSArray* reps = [image representations];
        NSEnumerator *enumerator = [reps objectEnumerator];
        while (imageRep = [enumerator nextObject]) {
            if ([imageRep pixelsWide] == width &&
                [imageRep pixelsHigh] == height &&
                [imageRep isKindOfClass: [NSBitmapImageRep class]]) {
                NSBitmapImageRep* bitmapRep = imageRep;
                data = [bitmapRep TIFFRepresentation];
                break;
            }
        }
        if (data == NULL) {
            data = [image TIFFRepresentation];
        }
        if (data != NULL) {
            unsigned len = [data length];
            void* bytes = malloc(len);
            [data getBytes:bytes];

            result = (*env)->NewByteArray(env, len);
            (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)bytes);
            free(bytes);
        }
    }

    // Release memory pool
	[pool release];

	return result;
}
/*
 * Class:     ch_randelshofer_quaqua_osx_OSXImageIO
 * Method:    nativeRead
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXImageIO_nativeRead___3B
  (JNIEnv *env, jclass javaClass, jbyteArray dataJ) {

    jbyteArray result = NULL;

    //printf( "Hello World \n");
    //fflush(stdout);

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert the Java data array into a NSData object
    jbyte *dataC;
    dataC = (*env)->GetByteArrayElements(env, dataJ, FALSE);
    if (dataC == NULL) {
        // Exception occured!

        // Release memory pool
        [pool release];

        return NULL;
    }
    NSData* dataNS = [NSData dataWithBytes: (void *)dataC length: (*env)->GetArrayLength(env, dataJ)];
    (*env)->ReleaseByteArrayElements(env, dataJ, dataC, 0);

    // Get the image and convert it to a TIFF data array
    NSImage* imageNS = [[NSImage alloc] initWithData: dataNS];
    if (imageNS != NULL) {
        NSData* tiffNS = [imageNS TIFFRepresentation];
        if (tiffNS != NULL) {
            unsigned len = [tiffNS length];
            void* tiffC = malloc(len);
            [tiffNS getBytes: tiffC];

            result = (*env)->NewByteArray(env, len);
            (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)tiffC);
            free(tiffC);
        }
    }

    // Release memory pool
	[pool release];
	return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXImageIO
 * Method:    nativeReadSystemClipboard
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXImageIO_nativeReadSystemClipboard
  (JNIEnv *env, jclass javaClass) {

    jbyteArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Get the NSPasteboard
    NSPasteboard *pb = [NSPasteboard generalPasteboard];

    // Get the image
    NSImage* image = [[NSImage alloc] initWithPasteboard: pb];
    if (image != NULL) {
        NSData* data = [image TIFFRepresentation];
        if (data != NULL) {
            unsigned len = [data length];
            void* bytes = malloc(len);
            [data getBytes:bytes];

            result = (*env)->NewByteArray(env, len);
            (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)bytes);
            free(bytes);
        }
    }

    // Release memory pool
	[pool release];

	return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXImageIO
 * Method:    nativeGetNativeCodeVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ch_randelshofer_quaqua_osx_OSXImageIO_nativeGetNativeCodeVersion
  (JNIEnv *env, jclass javaClass) {
  return 1;
}
