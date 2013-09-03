package com.btconnect.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.btconnect.variables.SVar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BtConnectService {
	private static final String TAG = "BTconnectService";
	private final BluetoothAdapter mAdapter;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private CommunicationThread mComThread;
	private Handler mHandler;

	public BtConnectService(Handler handler){
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
	}

	/**
	 * Starts the acceptThread that listens to incomming connections
	 */
	public void startListening(){
		stop();
		Log.d(TAG, "starting accept");
		mAcceptThread = new AcceptThread();
		mAcceptThread.start();
	}
	
	/**
	 * Starts the connectThread that conencts to the given device
	 * @param device
	 */
	public void connectTo(BluetoothDevice device){
		stop();
		Log.d(TAG, "start connecting");
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
	}

	/**
	 * Stops all accept and connecting threads
	 */
	public synchronized void stop() {
		Log.d(TAG, "stopping all threads");
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if(mComThread != null){
			mComThread.cancel();
			mComThread = null;
		}
	}

	private void connect(BluetoothSocket socket){
		stop();
		mComThread = new CommunicationThread(socket);
		mComThread.start();
	}
	
	public void writeToCom(byte[] buffer){
		if(mComThread != null){
			mComThread.write(buffer);
		}
	}
	
	private void connectionLost() {
        Message msg = mHandler.obtainMessage();
        msg.what = SVar.DEVICE_DISCONNECTED;
        mHandler.sendMessage(msg);
    }

	/*
	 * Listens for incomming connections
	 * calls connect() when connection is found
	 */
	private class AcceptThread extends Thread{
		private final BluetoothServerSocket mServerSocket;
		private boolean run = true;

		private AcceptThread(){
			BluetoothServerSocket tmp = null;
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(SVar.NAME_APP, SVar.APP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "listen() failed", e);
			}
			mServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			while (run) {
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				if (socket != null) {
					synchronized (this) {
						connect(socket);
						run = false;
						try {
							mServerSocket.close();
						} catch (IOException e) {
							Log.e(TAG, "serverSocket.close() failed", e);
						}
					}
				}
			}
			Log.i(TAG, "acceptThread stopped");
		}

		public void cancel() {
			try {
				mServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	/*
	 * Connects to another device
	 * calls connect() when device is found
	 */
	private class ConnectThread extends Thread{
		private final BluetoothSocket mSocket;

		public ConnectThread(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(SVar.APP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread ");
			mAdapter.cancelDiscovery();
			try {
				mSocket.connect();
			} catch (IOException e) {
				try {
					mSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (this) {
				mConnectThread = null;
			}
			if(mSocket != null)
				connect(mSocket);
		}

		public void cancel() {
			try {
				mSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect, socket failed", e);
			}
		}

	}

	private class CommunicationThread extends Thread{
		private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public CommunicationThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            Message m = mHandler.obtainMessage();
            m.what = SVar.DEVICE_CONNECTED;
            mHandler.sendMessage(m);

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
        	//String a = "hej hoe";
        	//write(a.getBytes());

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    bytes = mInStream.read(buffer);
                    Log.i(TAG, new String(buffer, 0, bytes));
                    Message msg = mHandler.obtainMessage(SVar.BT_READ, bytes, -1, buffer);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    //BluetoothChatService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mOutStream.write(buffer);

                // Share the sent message back to the UI Activity
               /* mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
	}

}
