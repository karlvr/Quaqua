/*
 * @(#)ch_randelshofer_quaqua_osx_OSXFile.m
 *
 * Copyright (c) 2004-2007 Werner Randelshofer, Switzerland.
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer.
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * Werner Randelshofer. For details see accompanying license terms.
 */

/**
 * Native code for class ch.randelshofer.quaqua.osx.OSXFile.
 *
 * @version $Id$
 */

#include <stdio.h>
#include <jni.h>
#include "ch_randelshofer_quaqua_osx_OSXFile.h"
#import <Cocoa/Cocoa.h>
#import <CoreServices/CoreServices.h>

/*
 * Related documentation:
 * ----------------------
 * Serializing an Alias into a stream of bytes:
 * http://developer.apple.com/qa/qa2004/qa1350.html
 */



/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getFileType
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetFileType
  (JNIEnv *env, jclass instance, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return false;

    jint result = -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSFileManager *fileManagerNS = [NSFileManager defaultManager];
    NSDictionary* d = [fileManagerNS attributesOfItemAtPath:pathNS error:nil];
    if (d != nil) {
        NSString* fileType = [d fileType];
        if (fileType != nil) {
            if ([fileType isEqualToString:NSFileTypeRegular]) {
                result = 0;
            } else if ([fileType isEqualToString:NSFileTypeDirectory]) {
                result = 1;
            } else if ([fileType isEqualToString:NSFileTypeSymbolicLink]) {
                result = 2;
            }
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    resolveAlias
 * Signature: (Ljava/lang/String;Z)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeResolveAlias__Ljava_lang_String_2Z
 (JNIEnv *env, jclass instance, jstring aliasPathJ, jboolean noUI)
{
    // Assert arguments
    if (aliasPathJ == NULL) return false;

    jstring result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, aliasPathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, aliasPathJ)];
    (*env)->ReleaseStringChars(env, aliasPathJ, pathC);

    // Do the API calls
    NSString *resultNS = [pathNS stringByResolvingSymlinksInPath];
    if (resultNS != nil) {
        // Convert NSString to jstring
        result = (*env)->NewStringUTF(env, [resultNS UTF8String]);
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    jniResolveAlias
 * Signature: ([BZ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeResolveAlias___3BZ
  (JNIEnv *env, jclass instance, jbyteArray serializedAlias, jboolean noUI)
{
    // Assert arguments
    if (serializedAlias == NULL) return false;

    CFDataRef dataRef;
    CFDataRef bookmarkDataRef;
    UInt8* serializedAliasBytes; // bytes of serializedAlias
    int length; // length of serializedAlias
    UInt8 resolvedPathC[2048];
    jstring result = NULL;

    length = (*env)->GetArrayLength(env, serializedAlias);
    serializedAliasBytes = (UInt8 *) (*env)->GetByteArrayElements(env, serializedAlias, NULL);
    if (serializedAliasBytes != NULL) {
        dataRef = CFDataCreate(NULL, serializedAliasBytes, length);
        if (dataRef != NULL) {
            bookmarkDataRef = CFURLCreateBookmarkDataFromAliasRecord(NULL, dataRef);
            if (bookmarkDataRef != NULL) {
                CFURLBookmarkResolutionOptions opt = (noUI) ? kCFBookmarkResolutionWithoutUIMask : 0;
                Boolean isStale;
                CFErrorRef error;
                CFURLRef u = CFURLCreateByResolvingBookmarkData(NULL, bookmarkDataRef, opt, NULL, NULL, &isStale, &error);
                if (u != NULL) {
                    Boolean success = CFURLGetFileSystemRepresentation(u, true, resolvedPathC, 2048);
                    if (success) {
                        result = (*env)->NewStringUTF(env, (const char *) resolvedPathC);
                    }
                    CFRelease(u);
                }
                CFRelease(bookmarkDataRef);
            }
            CFRelease(dataRef);
        }
        (*env)->ReleaseByteArrayElements(env, serializedAlias, (jbyte *) serializedAliasBytes, JNI_ABORT);
    }
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getLabel
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetLabel
  (JNIEnv *env, jclass instance, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jint result = -1;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        CFErrorRef error;
        CFNumberRef fileLabel;
        Boolean success = CFURLCopyResourcePropertyForKey((CFURLRef) u, kCFURLLabelNumberKey, &fileLabel, &error);
        if (success) {
            CFNumberGetValue(fileLabel, kCFNumberSInt32Type, &result);
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}
/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    nativeGetTagNames
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetTagNames
  (JNIEnv *env, jclass instance, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Prepare result
    jobjectArray result = NULL;

    if (&NSURLTagNamesKey != NULL) {
        // Convert Java String to NS String
        const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
        NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
        (*env)->ReleaseStringChars(env, pathJ, pathC);

        // Do the API calls
        NSURL *u = [NSURL fileURLWithPath:pathNS];
        if (u != nil) {
            NSError *error;
            NSArray *tagNames;
            Boolean success = [u getResourceValue: &tagNames forKey:NSURLTagNamesKey error:&error];
            if (success && tagNames != NULL) {
                int count = [tagNames count];
                jclass stringClass = (*env)->FindClass(env, "java/lang/String");
                result = (*env)->NewObjectArray(env, count, stringClass, NULL);
                for (int i=0; i< count; i++) {
                    jstring tagNameJ = (*env)->NewStringUTF(env, [tagNames[i] UTF8String]);
                    (*env)->SetObjectArrayElement(env, result, i, tagNameJ);
                }
            }
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getKindString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetKindString
  (JNIEnv *env, jclass instance, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jstring result = NULL;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        CFErrorRef error;
        CFStringRef kind;
        Boolean success = CFURLCopyResourcePropertyForKey((CFURLRef) u, kCFURLLocalizedTypeDescriptionKey, &kind, &error);
        if (success) {
            CFRange range;
            range.location = 0;
            // Note that CFStringGetLength returns the number of UTF-16 characters,
            // which is not necessarily the number of printed/composed characters
            range.length = CFStringGetLength(kind);
            UniChar charBuf[range.length];
            CFStringGetCharacters(kind, range, charBuf);
            result = (*env)->NewString(env, (jchar *)charBuf, (jsize)range.length);
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getIconImage
 * Signature: (Ljava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetIconImage
  (JNIEnv *env, jclass javaClass, jstring pathJ, jint size) {

    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Prepare result
    jbyteArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
        length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Get the icon image
    NSWorkspace* workspace = [NSWorkspace sharedWorkspace];
    NSImage* image = [workspace iconForFile:pathNS];
    //NSLog (@"%@", image);
    if (image != NULL) {

        // Create a scaled version of the image by choosing the best
        // representation.
        NSSize desiredSize = { size, size };
        NSImage* scaledImage = [[[NSImage alloc] initWithSize:desiredSize] autorelease];
        [scaledImage setSize: desiredSize];
        NSImageRep* imageRep;
        NSImageRep* bestRep = NULL;
        NSEnumerator *enumerator = [[image representations] objectEnumerator];
        while (imageRep = [enumerator nextObject]) {
            if ([imageRep pixelsWide] >= size &&
                (bestRep == NULL || [imageRep pixelsWide] < [bestRep pixelsWide]) ) {

                bestRep = imageRep;
            }
        }
        if (bestRep != NULL) {
            [scaledImage addRepresentation: bestRep];
        } else {
            // We should never get to here, but if we do, we use the
            // original image.
            scaledImage = image;
            [scaledImage setSize: desiredSize];
        }

        // Convert image to TIFF
        NSData* dataNS = [scaledImage TIFFRepresentation];
        if (dataNS != NULL) {
            unsigned len = [dataNS length];
            void* bytes = malloc(len);
            if (bytes != NULL) {
                [dataNS getBytes: bytes length:len];
                result = (*env)->NewByteArray(env, len);
                if (result != NULL) {
                    (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)bytes);
                }
            }
            free(bytes);
        }
    }


    // Release memory pool
    [pool release];

    return result;
}


/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    nativeGetQuickLookThumbnailImage
 * Signature: (Ljava/lang/String;I)[B
 */
typedef long (*QuickLookRequest)(CFAllocatorRef, CFURLRef, CGSize, CFDictionaryRef);
JNIEXPORT jbyteArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetQuickLookThumbnailImage
(JNIEnv *env, jclass javaClass, jstring pathJ, jint size) {
    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Prepare result
    jbyteArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
                                               length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);
    NSURL *fileURL = [NSURL fileURLWithPath:pathNS];

    if (fileURL != nil) {
        // Load the QuickLook bundle
        NSURL *bundleURL = [NSURL fileURLWithPath:@"/System/Library/Frameworks/QuickLook.framework"];
        CFBundleRef cfBundle = CFBundleCreate(kCFAllocatorDefault, (CFURLRef)bundleURL);
        NSBitmapImageRep *image = nil;
        // If we didn't succeed, the framework does not exist. The image is null, so null is returned
        if (cfBundle) {
            NSDictionary *dict = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:YES]
                                                             forKey:@"IconMode"];
            // Get the thumbnail function pointer
            QuickLookRequest functionRef = CFBundleGetFunctionPointerForName(cfBundle,
                                                                             CFSTR("QLThumbnailImageCreate"));
            // Perform the request
            CGImageRef ref = (CGImageRef) functionRef(kCFAllocatorDefault,
                                         (CFURLRef)fileURL,
                                         CGSizeMake(size, size),
                                         (CFDictionaryRef)dict);

            CFRelease(cfBundle);
            // Use the created image to create a bitmap image representation
            if (ref) {
                image = [[NSBitmapImageRep alloc] initWithCGImage:ref];
                CFRelease(ref);
            }
        }

        //NSLog (@"%@", image);
        if (image != NULL) {
            // Convert image to TIFF
            NSData* dataNS = [image TIFFRepresentation];
            if (dataNS != NULL) {
                unsigned len = [dataNS length];
                void* bytes = malloc(len);
                [dataNS getBytes: bytes length:len];
                result = (*env)->NewByteArray(env, len);
                (*env)->SetByteArrayRegion(env, result, 0, len, (jbyte*)bytes);
                free(bytes);
            }
        }
    }

    // Release memory pool
    [pool release];

    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getBasicItemInfoFlags
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetBasicItemInfoFlags
  (JNIEnv *env, jclass javaClass, jstring pathJ) {
    // Assert arguments
    if (pathJ == NULL) return -1;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    jint result = 0;

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        OSStatus err;
        LSItemInfoRecord itemInfoRecord;
        err = LSCopyItemInfoForURL((CFURLRef) u, kLSRequestBasicFlagsOnly, &itemInfoRecord);
        if (err == 0) {
            result = itemInfoRecord.flags;
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

JNIEXPORT jstring JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetDisplayName
  (JNIEnv *env, jclass javaClass, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
        length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Do the API calls
    NSFileManager *fileManagerNS = [NSFileManager defaultManager];
    NSString *displayNameNS = [fileManagerNS displayNameAtPath: pathNS];

    // Convert NSString to jstring
    jstring displayNameJ = (*env)->NewStringUTF(env, [displayNameNS UTF8String]);

    // Release memory pool
    [pool release];

    // Return the result
    return displayNameJ;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    nativeGetLastUsedDate
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jlong JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetLastUsedDate
  (JNIEnv *env, jclass javaClass, jstring pathJ) {

    // Assert arguments
    if (pathJ == NULL) return 0;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
        length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    jlong result = 0;

    // Do the API calls
    NSURL *u = [NSURL fileURLWithPath:pathNS];
    if (u != nil) {
        MDItemRef item = MDItemCreateWithURL(NULL, CFBridgingRetain(u));
        if (item != NULL) {
            CFDateRef date = (CFDateRef) MDItemCopyAttribute(item, kMDItemLastUsedDate);
            if (date != NULL) {
                CFAbsoluteTime /* double */ at = CFDateGetAbsoluteTime(date);	/* seconds since Jan 1 2001 */
                long long jtime = (long long) at;
                jtime += (60 * 60 * 24) * (31 * 365 + 8);
                jtime *= 1000;
                result = (jlong) jtime;
            }
        }
    }

    // Release memory pool
    [pool release];

    // Return the result
    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    nativeExecuteSavedSearch
 * Signature: (Ljava/lang/String)[Ljava/lang/String
 */
JNIEXPORT jobjectArray JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeExecuteSavedSearch
(JNIEnv *env, jclass javaClass, jstring pathJ) {
    // Assert arguments
    if (pathJ == NULL) return NULL;

    // Prepare result
    jobjectArray result = NULL;

    // Allocate a memory pool
    NSAutoreleasePool* pool = [NSAutoreleasePool new];

    // Convert Java String to NS String
    const jchar *pathC = (*env)->GetStringChars(env, pathJ, NULL);
    NSString *pathNS = [NSString stringWithCharacters:(UniChar *)pathC
                                               length:(*env)->GetStringLength(env, pathJ)];
    (*env)->ReleaseStringChars(env, pathJ, pathC);

    // Read the saved search file and execute the query synchronously
    NSData *data = [NSData dataWithContentsOfFile:pathNS];
    if (data != nil) {
        NSPropertyListReadOptions readOptions = NSPropertyListImmutable;
        NSError *error;
        NSDictionary *plist = (NSDictionary *)[NSPropertyListSerialization propertyListWithData:data options:readOptions format:NULL error:&error];
        if (plist != nil) {
            NSString *queryString = (NSString *) [plist objectForKey:@"RawQuery"];
            NSDictionary *searchCriteria = (NSDictionary *) [plist objectForKey:@"SearchCriteria"];
            if (queryString != nil && searchCriteria != nil) {
                NSArray *scopeDirectories = (NSArray *) [searchCriteria objectForKey:@"FXScopeArrayOfPaths"];
                if (scopeDirectories != nil) {
                    MDQueryRef query = MDQueryCreate(NULL, CFBridgingRetain(queryString), NULL, NULL);
                    if (query != NULL) {
                        OptionBits scopeOptions = 0;
                        MDQuerySetSearchScope(query, CFBridgingRetain(scopeDirectories), scopeOptions);
                        CFOptionFlags optionFlags = kMDQuerySynchronous;
                        Boolean b = MDQueryExecute(query, optionFlags);
                        if (b) {
                            CFIndex count = MDQueryGetResultCount(query);
                            jclass stringClass = (*env)->FindClass(env, "java/lang/String");
                            result = (*env)->NewObjectArray(env, count, stringClass, NULL);
                            for (CFIndex i = 0; i < count; i++) {
                                MDItemRef item = (MDItemRef) MDQueryGetResultAtIndex(query, i);
                                CFStringRef path = (CFStringRef) MDItemCopyAttribute(item, kMDItemPath);
                                NSString *pathNS = (NSString *) path;
                                jstring pathJ = (*env)->NewStringUTF(env, [pathNS UTF8String]);
                                (*env)->SetObjectArrayElement(env, result, i, pathJ);
                            }
                        }
                    }
                }
            }
        }
    }

    // Release memory pool
    [pool release];

    return result;
}

/*
 * Class:     ch_randelshofer_quaqua_osx_OSXFile
 * Method:    getNativeCodeVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ch_randelshofer_quaqua_osx_OSXFile_nativeGetNativeCodeVersion
  (JNIEnv *env, jclass javaClass) {
    return 6;
}


/*JNI function definitions end*/
