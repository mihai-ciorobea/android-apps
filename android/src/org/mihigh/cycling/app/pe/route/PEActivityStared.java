package org.mihigh.cycling.app.pe.route;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.mihigh.cycling.app.R;
import org.mihigh.cycling.app.pe.route.test.WiFiDirectBroadcastReceiver;
import org.mihigh.cycling.app.utils.LoadingUtils;

import java.util.HashMap;
import java.util.Map;

public class PEActivityStared extends Fragment {

    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";


    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WiFiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WifiP2pDnsSdServiceRequest serviceRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity(), getActivity().getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, getActivity());

        startRegistrationAndDiscovery();

        return inflater.inflate(R.layout.pe_activity_stared, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();


        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        runAt10Sec();

    }

    private void runAt10Sec() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                discover();
            }
        }, 10000);
    }

    private void discover() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.discoverPeers(mChannel, this);
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void startRegistrationAndDiscovery() {
        final Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");


        final WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE + android.os.Build.MODEL, SERVICE_REG_TYPE, record);
        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                LoadingUtils.makeToast(getActivity(), "Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                LoadingUtils.makeToast(getActivity(), "Failed to add a service");
            }
        });


        discoverService();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mManager.removeLocalService(mChannel, service, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        final WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE + "_gigi", SERVICE_REG_TYPE, record);
                        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                LoadingUtils.makeToast(getActivity(), "Added Local Service");
                            }

                            @Override
                            public void onFailure(int error) {
                                LoadingUtils.makeToast(getActivity(), "Failed to add a service");
                            }
                        });
                    }

                    @Override
                    public void onFailure(int reason) {

                    }
                });
            }
        }, 30000);
    }

    private void discoverService() {

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        mManager.setDnsSdResponseListeners(mChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered. Is this our app?

                        if (instanceName.contains(SERVICE_INSTANCE)) {

                            LoadingUtils.makeToast(getActivity(), instanceName);

                        }

                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                    }
                }
        );

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        LoadingUtils.makeToast(getActivity(), "Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        LoadingUtils.makeToast(getActivity(), "Failed adding service discovery request");
                    }
                });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                LoadingUtils.makeToast(getActivity(), "Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                LoadingUtils.makeToast(getActivity(), "Service discovery failed");

            }
        });
    }


}





/*
new ExceptionHandler(getActivity()).sendError(new RuntimeException("Name: " + device.getName() + " " + device.getAddress()), false);
LoadingUtils.makeToast(getActivity(), "Name: " + device.getName() + " " + device.getAddress());
 */