package robotsmom.growow;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by luke on 3/24/16.
 */
public class FarmView extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private SurfaceView vidSurface;
    private SurfaceHolder vidHolder;
    private MediaPlayer mediaPlayer;
    private String vSource ="rtsp://178.214.221.154:1935/live/myStream";



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.farm_view, container, false);

        //get the VideoView from the layout file
        vidSurface = (SurfaceView) view.findViewById(R.id.surView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);


        return view;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setDataSource(vSource);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

    }
}
