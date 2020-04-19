package com.example.serviceinnotification;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MusicFragment extends Fragment {
    private View mView;
    private Intent svcIntent;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    public MusicFragment(Context context){
        //svcIntent = new Intent(context,MusicService.class);
        svcIntent = new Intent("com.example.serviceinnotification.MusicPlay");
        svcIntent.setPackage("com.example.serviceinnotification");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.notification,container,false);
        TextView tvMsg = (TextView)mView.findViewById(R.id.tv_msg);
        ImageView btnPlay = (ImageView)mView.findViewById(R.id.btn_play);
        ImageView btnStop = (ImageView)mView.findViewById(R.id.btn_stop);
        //由于在主窗口，所以启动和关闭主窗口的两个按钮隐藏
        ImageView btnClose = (ImageView)mView.findViewById(R.id.btn_close);
        btnClose.setVisibility(View.GONE);
        ImageView btnApp = (ImageView)mView.findViewById(R.id.iv_icon);
        btnApp.setVisibility(View.GONE);

        /*由于只有一首曲子播放，因此*/
        tvMsg.setText("music demo");
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().bindService(svcIntent,sc, Service.BIND_AUTO_CREATE);
                //getActivity().startService(svcIntent);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().unbindService(sc);
                //getActivity().stopService(svcIntent);
            }
        });






        return mView;
    }
}
