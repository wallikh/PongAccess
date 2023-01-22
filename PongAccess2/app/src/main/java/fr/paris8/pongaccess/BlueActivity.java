package fr.paris8.pongaccess;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BlueActivity extends Activity {

    /**
     * La ligne "private static final String TAG = "BlueTest5-MainActivity";" déclare une constante statique appelée TAG qui est un identifiant pour l'application. Cette constante est utilisée pour identifier les messages de journalisation (logs) dans les journaux de débogage de l'application. Elle est généralement utilisée pour identifier les messages de journalisation qui proviennent de cette classe spécifique, facilitant ainsi le débogage et le suivi des erreurs dans le code. En utilisant "BlueTest5-MainActivity" comme valeur pour TAG, on peut facilement identifier les messages de log qui proviennent de la classe principale de l'application BlueTest5.
     * •	mMaxChars: Il s'agit de la taille maximale de la mémoire tampon utilisée pour stocker les données reçues à partir du périphérique Bluetooth. La valeur par défaut est de 50000 caractères.
     * •	mDeviceUUID: Il s'agit d'un UUID (Universally Unique Identifier) qui est utilisé pour identifier de manière unique le périphérique Bluetooth avec lequel l'application est censée se connecter.
     * •	mBTSocket: Il s'agit d'une socket Bluetooth qui est utilisée pour établir une connexion avec le périphérique Bluetooth spécifié par l'UUID.
     * •	mReadThread: Il s'agit d'un objet de la classe interne ReadInput qui est utilisé pour démarrer un thread pour lire les données reçues à partir du périphérique Bluetooth.
     * •	mIsUserInitiatedDisconnect: Il s'agit d'un booléen qui indique si la déconnexion à partir de l'application a été initiée par l'utilisateur ou pas.
     * •	mIsBluetoothConnected: Il s'agit d'un booléen qui indique si l'application est actuellement connectée à un périphérique Bluetooth ou pas.
     * •	mDevice: Il s'agit d'un objet BluetoothDevice qui contient les informations sur le périphérique Bluetooth avec lequel l'application est censée se connecter.
     * •	progressDialog : Il s'agit d'un objet ProgressDialog qui peut afficher une boîte de dialogue indiquant à l'utilisateur que l'application est en train de se connecter au périphérique Bluetooth.
     * •	buffer : Il s'agit d'un tableau de bytes qui est utilisé pour stocker les données reçues à partir du périphérique Bluetooth avant de les stocker dans la variable reception.
     */
    private static final String TAG = "BlueTest5-MainActivity";
    private int mMaxChars = 50000;//Default
    private UUID mDeviceUUID;
    private static BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;


    private boolean mIsUserInitiatedDisconnect = false;

    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");

        Intent intent2 = new Intent(getApplicationContext(), JeuActivity.class);
        startActivity(intent2);

    }

    /**
     * La classe interne ReadInput est une classe déclarée à l'intérieur de
     * la classe BlueActivity qui est utilisée pour démarrer un thread pour lire
     * les données reçues à partir du périphérique Bluetooth connecté.
     * 	Tout d'abord, la classe contient une variable bStop qui est utilisée pour
     * 	arrêter le thread. Cette variable est initialisée à false, ce qui signifie
     * 	que le thread continuera à s'exécuter tant qu'elle n'est pas définie sur true.
     * 	Il y a une variable t de type Thread qui est instanciée avec un objet de cette
     * 	classe ReadInput, qui est ensuite démarré en appelant la méthode start() sur cet objet.
     * 	Il y a une méthode isRunning() qui est utilisée pour vérifier si le thread est
     * 	en cours d'exécution ou non en utilisant la méthode isAlive() de la classe Thread.
     * 	La méthode run() est où le thread fait réellement son travail. Il obtient un flux
     * 	d'entrée à partir de la socket Bluetooth mBTSocket. Il lit ensuite les données reçues
     * 	à partir de ce flux d'entrée et les stock dans un tableau de bytes buffer.
     * 	Il utilise ensuite ces données pour créer un objet String reception qui est
     * 	utilisé pour stocker les données reçues.
     * 	Il y a des instructions de debogage pour suivre les données reçues
     * 	dans la console de débogage (Log.d)
     * 	cette classe est utilisée pour lire les données en arrière-plan,
     * 	de sorte que l'application peut continuer à fonctionner normalement
     * 	tout en recevant les données. Cela permet également à l'application
     * 	de ne pas bloquer pendant la réception des données
     */
    public static class ReadInput implements Runnable {

        public boolean bStop = false;
        private Thread t;
        public static String reception;
        public static byte[] buffer = new byte[256];




        public ReadInput() {
            t = new Thread(this, "Input Thread");
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
                         * Ceci est nécessaire car new String(buffer) prend la totalité du tampon, soit 256 caractères sous Android 2.3.4 http://stackoverflow.com/a/8843462/1287554.
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {

                        }

                        reception = new String(buffer, 0, i);
                        System.out.println("Debogage info reception tooth**************info :***********"+ reception);
                        Log.d("info reception tooth**************","info :***********"+ reception +"******** ");

                        if (true) {
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


    }

    /**
     * DisConnectBT est utilisée pour mettre fin à la connexion Bluetooth
     * avec un périphérique connecté. Il s'agit d'une classe AsyncTask qui
     * permet de lancer des tâches en arrière-plan pour éviter de bloquer l'interface utilisateur.
     */
    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        /**
         * appelée avant le début de la tâche en arrière-plan. Elle est généralement utilisée
         * pour mettre à jour l'interface utilisateur pour indiquer que la tâche est en cours
         * d'exécution.
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * La méthode doInBackground() est où se trouve le code qui est exécutéen arrière-plan.
         * Dans ce cas, il vérifie si un thread de lecture est en cours d'exécution
         * et si c'est le cas, il l'arrête en utilisant la méthode stop() de la classe ReadInput et
         * en attendant qu'il se termine en utilisant la méthode isRunning(). Il ferme ensuite la
         * socket Bluetooth en utilisant la méthode close().
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Attendre jusqu'à ce qu'il s'arrête
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        /**
         * La méthode onPostExecute() est appelée après la fin de la tâche en arrière-plan.
         * Elle est utilisée pour mettre à jour l'interface utilisateur et pour effectuer d'autres
         * tâches de nettoyage. Dans ce cas, elle met à jour la variable mIsBluetoothConnected pour
         * indiquer que la connexion Bluetooth est fermée et si l'utilisateur a initié la déconnexion,
         * elle termine l'activité en cours en appelant la méthode finish().
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    /**
     * La méthode msg() est une méthode utilitaire qui permet d'afficher un message Toast à
     * l'utilisateur. Il prend en paramètre une chaîne de caractères qui est le message à afficher.
     * Il utilise la méthode Toast.makeText() pour créer un objet Toast avec le message fourni.
     * Il utilise ensuite la méthode show() pour afficher le message à l'utilisateur
     * La méthode getApplicationContext() est utilisée pour récupérer le contexte de l'application
     * actuelle, ce qui est nécessaire pour créer un objet Toast.
     * @param s
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    /**
     * La méthode onPause() est appelée lorsque l'activité est mise en pause, par exemple lorsque
     * l'utilisateur quitte l'application ou lorsque l'application passe en arrière-plan.
     * Dans cette méthode, il vérifie si une connexion Bluetooth est en cours d'exécution et
     * si c'est le cas, il lance une tâche asynchrone pour la fermer en utilisant la classe
     * DisConnectBT définie précédemment
     */
    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    /**
     * La méthode onResume() est appelée lorsque l'activité reprend, par exemple lorsque l'utilisateur
     * revient à l'application. Dans cette méthode, il vérifie si une connexion Bluetooth n'est pas
     * en cours d'exécution et si c'est le cas, il lance une tâche asynchrone pour la connecter en
     * utilisant la classe ConnectBT définie précédemment.
     */
    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    /**
     * La méthode onStop() est appelée lorsque l'activité est arrêtée, par exemple lorsque
     * l'utilisateur quitte l'application ou lorsque l'application passe en arrière-plan.
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    /**
     * La méthode onSaveInstanceState() est appelée lorsque l'application est en train d'être
     * sauvegardée. Cette méthode permet de sauvegarder des données dans l'application pour qu'elles
      puissent être restaurées lorsque l'application est relancée.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    /**
     * La classe ConnectBT est une sous-classe AsyncTask qui permet de connecter l'application à un
     * périphérique Bluetooth. Elle définit 3 méthodes
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        /**
         * affiche une boîte de dialogue indiquant que la connexion est en cours.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BlueActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        /**
         * essaie de créer un socket Bluetooth en utilisant l'UUID du périphérique et en appelant la méthode
         * createInsecureRfcommSocketToServiceRecord() sur l'objet BluetoothDevice.
         * Il annule également la recherche de périphériques Bluetooth en cours en appelant cancelDiscovery()
         * sur l'adaptateur Bluetooth par défaut, puis il tente de se connecter au périphérique en appelant
         * connect() sur le socket Bluetooth. Si la connexion échoue, une exception IOException est levée et
         * mConnectSuccessful est définie sur false.
         * @param devices
         * @return
         */
        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if(ContextCompat.checkSelfPermission(BlueActivity.this, Manifest.permission.BLUETOOTH_CONNECT)== PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(BlueActivity.this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},2);

                }
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Impossible de se connecter au périphérique
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        /**
         * La méthode onPostExecute() est appelée après l'exécution de la tâche en arrière-plan.
         * Si la connexion n'a pas réussi, un message Toast est affiché indiquant que la connexion a échoué.
         * Si la connexion a réussi, un message Toast est affiché indiquant que la connexion a réussi et
         * la variable mIsBluetoothConnected est définie sur true, le thread de lecture d'entrée est
         * démarré pour lire les données envoyées par le périphérique Bluetooth . La boîte de dialogue de
         * progression est également fermée
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Impossible de se connecter au Joystick", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connecté au joystick");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }

}

