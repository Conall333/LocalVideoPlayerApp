package conall.ucc.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    private Uri videoFileUri = null;
    private boolean videoPaused = false;
    private Button browseVideoButton = null;
    private Button playButton = null;
    private VideoView videoPlayView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Local Video Playback");

        initVideoControls();


        // get permissions to read external storage and select video to play

        browseVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int externalStoragePermiss = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                if(externalStoragePermiss != PackageManager.PERMISSION_GRANTED)
                {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(MainActivity.this, requirePermission, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                }else {
                    selectVideoFile();
                }
            }
        });

        // plays video
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    videoPlayView.setVideoURI(videoFileUri);
                    videoPlayView.setVisibility(View.VISIBLE);
                    videoPlayView.start();
                    playButton.setEnabled(false);


                }

        });


    }

    // initialize buttons
    private void initVideoControls()
    {
            browseVideoButton = findViewById(R.id.browse_video_file_button);
            playButton = findViewById(R.id.play_video_start_button);
            videoPlayView = findViewById(R.id.play_video_view);

        setContinueVideoAfterSeekComplete();
    }

    // intent to get video file
    private void selectVideoFile()
    {
        Intent selectVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);

        selectVideoIntent.setType("video/*");

        startActivityForResult(selectVideoIntent, REQUEST_CODE_SELECT_VIDEO_FILE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_SELECT_VIDEO_FILE)
        {
            if(resultCode==RESULT_OK)
            {

                videoFileUri = data.getData();
                // String videoFileName = videoFileUri.getLastPathSegment();
                playButton.setEnabled(true);

            }
        }
    }

    // get permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE_READ_EXTERNAL_PERMISSION)
        {
            if(grantResults.length > 0)
            {
                int grantResult = grantResults[0];
                if(grantResult == PackageManager.PERMISSION_GRANTED)
                {
                    selectVideoFile();
                }else
                {
                    Toast.makeText(getApplicationContext(), "You denied read external storage permission.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // plays video
    private void setContinueVideoAfterSeekComplete()
    {
        videoPlayView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        if(videoPaused)
                        {
                            videoPlayView.start();
                            videoPaused = false;
                        }
                    }
                });
            }
        });
    }
}

