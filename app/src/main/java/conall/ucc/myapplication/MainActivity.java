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


    private Button browseVideoFileButton = null;
    private Button playVideoButton = null;
    private VideoView playVideoView = null;
    private static final int REQUEST_CODE_SELECT_VIDEO_FILE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    private Uri videoFileUri = null;
    private boolean isVideoPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Local Video Playback");

        initVideoControls();


        // get permissions to read external storage and select video to play

        browseVideoFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

                if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED)
                {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(MainActivity.this, requirePermission, REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                }else {
                    selectVideoFile();
                }
            }
        });

        // plays video
        playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    playVideoView.setVideoURI(videoFileUri);
                    playVideoView.setVisibility(View.VISIBLE);
                    playVideoView.start();
                    playVideoButton.setEnabled(false);


                }

        });


    }

    // initialize buttons
    private void initVideoControls()
    {
            browseVideoFileButton = findViewById(R.id.browse_video_file_button);
            playVideoButton = findViewById(R.id.play_video_start_button);
            playVideoView = findViewById(R.id.play_video_view);

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
                playVideoButton.setEnabled(true);

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
        playVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        if(isVideoPaused)
                        {
                            playVideoView.start();
                            isVideoPaused = false;
                        }
                    }
                });
            }
        });
    }
}

