/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED,
INCLUDING WITHOUT LIMITATION ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
MERCHANTABLITY OR NON-INFRINGEMENT.

See the Apache 2 License for the specific language governing permissions and limitations under the License.
*/

package com.msopentech.thali.android.toronionproxy;

import android.content.Context;
import com.msopentech.thali.toronionproxy.OnionProxyContext;
import com.msopentech.thali.toronionproxy.WriteObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class AndroidOnionProxyContext extends OnionProxyContext {
    private final Context context;
    private static final String TAG = "AndroidOnionProxyContex";

    public AndroidOnionProxyContext(Context context, String workingSubDirectoryName) {
        super(context.getDir(workingSubDirectoryName, MODE_PRIVATE));
        this.context = context;
    }

    @Override
    public WriteObserver generateWriteObserver(File file) {
        return new AndroidWriteObserver(file);
    }

    @Override
    protected InputStream getAssetOrResourceByName(String fileName) throws IOException {
        return context.getResources().getAssets().open(fileName);
    }

    @Override
    protected InputStream getJNIBinary(String fileName) throws IOException {
        File fileNativeDir = new File(getNativeLibraryDir(context));
        File fileTor = new File(fileNativeDir, "tor.so");
        if (!fileTor.exists()) {
            if (getNativeLibraryDir(context).endsWith("arm")) {
                fileTor = new File(getNativeLibraryDir(context) + "eabi", "tor.so");
            } else if (getNativeLibraryDir(context).endsWith("arm64")) {
                fileNativeDir = new File(fileNativeDir.getParentFile(), "armeabi");
                fileTor = new File(fileNativeDir, "tor.so");
            }
        }

        return new FileInputStream(fileTor);
    }

    @Override
    public String getProcessId() {
        return String.valueOf(android.os.Process.myPid());
    }

    private static String getNativeLibraryDir(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        return appInfo.nativeLibraryDir;
    }
}
