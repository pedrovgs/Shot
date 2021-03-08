package com.karumi.shot.permissions;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class AndroidStoragePermissions {
    private static final String WRITE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE";
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{WRITE_PERMISSION, READ_PERMISSION};

    private final Instrumentation instrumentation;

    public AndroidStoragePermissions(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public void checkPermissions() {
        Context testAppContext = instrumentation.getContext();
        for (String permission : REQUIRED_PERMISSIONS) {
            if ((permission.equals(READ_PERMISSION) && Build.VERSION.SDK_INT < 16)
                    || testAppContext.checkCallingOrSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (Build.VERSION.SDK_INT < 23) {
                throw new RuntimeException("We need " + permission + " permission for screenshot tests");
            }
            Context targetContext = instrumentation.getTargetContext();
            grantPermission(targetContext, permission);
            grantPermission(testAppContext, permission);
        }
    }

    private void grantPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        UiAutomation automation = instrumentation.getUiAutomation();
        String command =
                String.format(Locale.ENGLISH, "pm grant %s %s", context.getPackageName(), permission);
        ParcelFileDescriptor pfd = automation.executeShellCommand(command);
        InputStream stream = new FileInputStream(pfd.getFileDescriptor());
        try {
            byte[] buffer = new byte[1024];
            while (stream.read(buffer) != -1) {
                // Consume stdout to ensure the command completes
            }
        } catch (IOException ignored) {
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
            try {
                pfd.close();
            } catch (IOException ignored) {
            }
        }
    }
}