package com.project_aurora.emu;

import static android.system.Os.getuid;
import static android.system.Os.getenv;

import android.annotation.SuppressLint;
import android.app.IActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Keep;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

@Keep
@SuppressLint({"StaticFieldLeak", "UnsafeDynamicallyLoadedCode"})
public class CmdEntryPoint extends ICmdEntryInterface.Stub {
    public static final String ACTION_START = "com.project_aurora.emu.CmdEntryPoint.ACTION_START";
    public static final int PORT = 7892;
    public static final byte[] MAGIC = "0xDEADBEEF".getBytes();
    private static Handler handler;
    public static Context ctx;
    public static boolean load = false;
    public static ServerSocket listeningSocket;
    public static Socket sock_fd;

    /**
     * Command-line entry point.
     *
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
        if (Looper.getMainLooper() == null) {
            Looper.prepareMainLooper();
        }
        handler = new Handler(Looper.getMainLooper());

        ctx = createContext();

        handler.post(() -> new CmdEntryPoint(args));
        Looper.loop();
    }

    CmdEntryPoint(String[] args) {
        if (!start(args))
            System.exit(1);

        spawnListeningThread();
        sendBroadcastDelayed();
    }

    @SuppressLint({"WrongConstant", "PrivateApi"})
    void sendBroadcast() {
        String targetPackage = "com.project_aurora.emu";
        Bundle bundle = new Bundle();
        bundle.putBinder("", this);

        Intent intent = new Intent(ACTION_START);
        intent.putExtra("", bundle);
        intent.setPackage(targetPackage);

        if (getuid() == 0 || getuid() == 2000)
            intent.setFlags(0x00400000 /* FLAG_RECEIVER_FROM_SHELL */);

        try {
            ctx.sendBroadcast(intent);
        } catch (Exception e) {
            if (e instanceof NullPointerException && ctx == null)
                Log.i("Broadcast", "Context is null, falling back to manual broadcasting");
            else
                Log.e("Broadcast", "Falling back to manual broadcasting, failed to broadcast intent through Context:", e);

            String packageName;
            try {
                packageName = android.app.ActivityThread.getPackageManager().getPackagesForUid(getuid())[0];
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            IActivityManager am;
            try {
                //noinspection JavaReflectionMemberAccess
                am = (IActivityManager) android.app.ActivityManager.class
                        .getMethod("getService")
                        .invoke(null);
            } catch (Exception e2) {
                try {
                    am = (IActivityManager) Class.forName("android.app.ActivityManagerNative")
                            .getMethod("getDefault")
                            .invoke(null);
                } catch (Exception e3) {
                    throw new RuntimeException(e3);
                }
            }

            assert am != null;
            IIntentSender sender = am.getIntentSender(1, packageName, null, null, 0, new Intent[]{intent},
                    null, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT, null, 0);
            try {
                //noinspection JavaReflectionMemberAccess
                IIntentSender.class
                        .getMethod("send", int.class, Intent.class, String.class, IBinder.class, IIntentReceiver.class, String.class, Bundle.class)
                        .invoke(sender, 0, intent, null, null, new IIntentReceiver.Stub() {
                            @Override
                            public void performReceive(Intent i, int r, String d, Bundle e, boolean o, boolean s, int a) {
                            }
                        }, null, null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void sendBroadcastDelayed() {
        if (!connected())
            sendBroadcast();

        handler.postDelayed(this::sendBroadcastDelayed, 1000);
    }

    void spawnListeningThread() {
        new Thread(() -> {
            Log.e("CmdEntryPoint", "Listening port " + PORT);
                
            try {
            	listeningSocket = new ServerSocket(PORT, 0, InetAddress.getByName("127.0.0.1"));
            } catch(IOException err) {
            	err.getMessage();
            }
                
            try {
                listeningSocket.setReuseAddress(true);
                while (true) {
                    try (Socket client = listeningSocket.accept()) {
                        Log.e("CmdEntryPoint", "Somebody connected!");
                        byte[] b = new byte[MAGIC.length];
                        DataInputStream reader = new DataInputStream(client.getInputStream());
                        reader.readFully(b);
                        if (Arrays.equals(MAGIC, b)) {
                            Log.e("CmdEntryPoint", "New client connection!");
                            sendBroadcast();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }).start();
    }

    public static void requestConnection() {
        System.err.println("Requesting connection...");
        new Thread(() -> {
            try(Socket socket = new Socket("127.0.0.1", CmdEntryPoint.PORT)) {
                socket.getOutputStream().write(CmdEntryPoint.MAGIC);
                sock_fd = socket;    
            } catch (ConnectException e) {
                if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                    Log.e("CmdEntryPoint", "ECONNREFUSED: Connection has been refused by the server");
                } else
                    Log.e("CmdEntryPoint", "Something went wrong when we requested connection", e);
            } catch (Exception e) {
                Log.e("CmdEntryPoint", "Something went wrong when we requested connection", e);
            }
        }).start();
    }
    
    public static void closeConnection() {
        try {
        	sock_fd.close();
            listeningSocket.close();
        } catch(IOException err) {
        	
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static Context createContext() {
        Context context;
        PrintStream err = System.err;
        try {
            java.lang.reflect.Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Object unsafe = f.get(null);
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int arg0) {
                }
            }));
            if (System.getenv("OLD_CONTEXT") != null) {
                context = android.app.ActivityThread.systemMain().getSystemContext();
            } else {
                context = ((android.app.ActivityThread) Class.
                        forName("sun.misc.Unsafe").
                        getMethod("allocateInstance", Class.class).
                        invoke(unsafe, android.app.ActivityThread.class))
                        .getSystemContext();
            }
        } catch (Exception e) {
            Log.e("Context", "Failed to instantiate context:", e);
            context = null;
        } finally {
            System.setErr(err);
        }
        return context;
    }

    public static native boolean start(String[] args);

    public native void windowChanged(Surface surface);

    public native ParcelFileDescriptor getXConnection();

    public native ParcelFileDescriptor getLogcatOutput();

    private static native boolean connected();

    static {
        try {
            if (Looper.getMainLooper() == null) {
                Looper.prepareMainLooper();
            }
            handler = new Handler(Looper.getMainLooper());

            ctx = createContext();

            String path = "lib/" + Build.SUPPORTED_ABIS[0] + "/libXlorie.so";
            ClassLoader loader = CmdEntryPoint.class.getClassLoader();
            URL res = loader != null ? loader.getResource(path) : null;
            String libPath = res != null ? res.getFile().replace("file:", "") : null;
            if (libPath != null && !load) {
                try {
                    System.load(libPath);
                    load = true;
                } catch (Exception e) {
                    Log.e("CmdEntryPoint", "Failed to dlopen " + libPath, e);
                    System.err.println("Failed to load native library. Did you install the right apk? Try the universal one.");
                    System.exit(134);
                }
            } else {
                if (XserverActivity.getInstance() == null) {
                    System.err.println("Failed to acquire native library. Did you install the right apk? Try the universal one.");
                    System.exit(134);
                }
            }
        } catch (Exception e) {
            Log.e("CmdEntryPoint", "Something went wrong during static initialization", e);
        }
    }
}
