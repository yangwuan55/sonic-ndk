package org.vinuxproject.sonic;

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.os.Bundle;

public class Sonic extends Activity
{	    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);                      
 
        new Thread( new Runnable( ) 
        {
            public void run( )
            {        		
                AndroidAudioDevice device = new AndroidAudioDevice(22050, 1);
                SonicAudio sonic = new SonicAudio(22050, 1);
                float speed = 2.0f; // The amount for sonic to speed up audio
                byte samples[] = new byte[2048];
                byte modifiedSamples[] = new byte[2048];
                InputStream soundFile = getResources().openRawResource(R.raw.talking);
				int bytesRead;

				if(soundFile != null) {
				    sonic.setSpeed(speed);
				    do {
				        try {
							bytesRead = soundFile.read(samples, 0, samples.length);
							if((bytesRead & 1) != 0) {
							    finish();
							}
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
				        if(bytesRead > 0) {
				        	sonic.putBytes(samples, bytesRead);
				        } else {
						    sonic.flush();
				        }
			        	int available = sonic.availableBytes(); 
			        	if(available > 0) {
			        		if(modifiedSamples.length < available) {
			        		    modifiedSamples = new byte[available*2];
			        		}
			        		sonic.receiveBytes(modifiedSamples, available);
			        		device.writeSamples(modifiedSamples, available);
			        	}
				    } while(bytesRead > 0);
				    device.flush();
				    finish();
				}
            }
        } ).start();
    }
}
