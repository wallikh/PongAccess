/*package fr.paris8.pongaccess;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Gerer implements Runnable{
    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private static BluetoothSocket mBTSocket;
    private BlueActivity.ReadInput mReadThread = null;


    private boolean mIsUserInitiatedDisconnect = false;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;

    // modif du 07/01
    public static String strInput;
    public static byte[] buffer = new byte[256];
    public boolean bStop = false;
    private Thread t;

    private Joueur mjoueur;
    public Gerer(Joueur mJoueur){
       this.mjoueur = mJoueur;
        t = new Thread(this, "Gerer");
        t.start();
    }
    public boolean isRunning() {
        return t.isAlive();
    }
    @Override
    public void run() {
        InputStream inputStream;

        try {
            inputStream = mBTSocket.getInputStream();
            while (!bStop) {

                if (inputStream.available() > 0) {
                    inputStream.read(buffer);
                    int i = 0;
                    /*
                     * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                     */
                    /*for (i = 0; i < buffer.length && buffer[i] != 0; i++) {

                    }
                    strInput = new String(buffer, 0, i);
                    //strInputstrInput = mReadThread.strInput;
                    System.out.println("__________________________________"+strInput);

                    Log.d("_____________________","_____________"+strInput+"________");







                    /*
                     * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                     */



                   /* if (true) {
                        new Runnable() {
                            @Override
                            public void run() {


                            }
                        };
                    }

                }

                Thread.sleep(200);
            }

        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    public void stop() {
        bStop = true;
    }
}*/
