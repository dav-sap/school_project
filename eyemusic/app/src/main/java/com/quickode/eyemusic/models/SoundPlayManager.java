package com.quickode.eyemusic.models;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.quickode.eyemusic.interfaces.SoundProccessListener;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.GlobalParameters;

public class SoundPlayManager {

	private static final String TAG = "SoundPlayManager";
	private static final double SAMPLE_RATE = 44100;
	private static final double BYTE_PER_SAMPLE = 2.0;
	private static final double MILI_SECOND_IN_SECOND = 1000.0;
	private static final double BYTE_IN_MILI_SECOND = (SAMPLE_RATE * BYTE_PER_SAMPLE)/ MILI_SECOND_IN_SECOND;

	private Context mContext;

	private HashMap<String, byte[]> mSounds;
	private int mDuration;
	private SoundProccessListener mProcessListsner;
	//	private SoundSlice[][] mSoundsMatrix;
	private byte[] mSoundArray;
	private byte[] mFinalSound;
	private int bytesInPixel;
	long totalAudioLen = 0;
	String[][] fileMatrix;
	float[][] volumeMatrix;
	long startnow;
	long endnow;
//	AudioTrack track;

	public SoundPlayManager(Context context) {
		mContext = context;
		mSounds = GlobalParameters.getInstance(mContext).getFiles();
		

	}

	public void convertPhotoToSound( int duration,
			String[][] fileMatrix, float[][] volumeMatrix){
		
		mSoundArray = new byte[CommonConstans.IMAGE_WIDTH];
		
		mDuration = duration;
		this.fileMatrix=fileMatrix;
		this.volumeMatrix=volumeMatrix;
		bytesInPixel =  (int) (mDuration * BYTE_IN_MILI_SECOND);
		if (bytesInPixel % 2 != 0) {
			bytesInPixel--;
		}
		mFinalSound = new byte[(bytesInPixel * CommonConstans.IMAGE_WIDTH)];
		//mFinalSound=new ChunkedByte((bytesInPixel * CommonConstans.IMAGE_WIDTH),bytesInPixel);
	//	pictureToSounds(fileMatrix);
		//	byte[] dataBytes = new byte[2];
		//	for (int col = 0; col < CommonConstans.IMAGE_WIDTH; col++) {
		mergeColumn();
		//	}		
		// writeWavHeader();
		//	concatSounds();
		//playSound(processListsner);
		// playMp3(mFinalSound);

	}






	public void playSound(SoundProccessListener listener, int startFrom) {
		boolean toStop=false;
		
try{
	int bufSize = AudioTrack.getMinBufferSize(44100,
			AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
	AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
			AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
			bufSize, AudioTrack.MODE_STREAM);
		track.play();
		if (startFrom==0){
			byte[] beep=mSounds.get("beep.wav");
			track.write(beep,0,beep.length);
		}
		for (int col=startFrom;col<CommonConstans.IMAGE_WIDTH && !toStop;col++){
			track.write(mFinalSound, col*bytesInPixel,bytesInPixel);
		//	track.write(mFinalSound.chunk(col), 0, bytesInPixel);
			toStop=listener.onFinishPlayColumn(col);
		}
	//	System.out.println(mFinalSound.length);

		track.pause();
		track.flush();
		track.release();
}catch(Exception e){
	e.printStackTrace();
//	track.release();
//	int bufSize = AudioTrack.getMinBufferSize(44100,
//			AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
//	track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//			AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
//			bufSize, AudioTrack.MODE_STREAM);
}

	}








	private void mergeColumn() {
	byte[] dataBytes = new byte[2];
	short dataInShort = 0;
	String fileName;
	int sum=0;
	int added=0;
	int dataInInt;
	int outputInt;
	short outputSample;
	int columnNumber=-1;
	int len=bytesInPixel*CommonConstans.IMAGE_WIDTH;
	long startnow;
	 long endnow;
	 ByteBuffer buffer=ByteBuffer.allocateDirect(2);
	 buffer.order(ByteOrder.LITTLE_ENDIAN);
	startnow = android.os.SystemClock.uptimeMillis();

	for (int i = 0; i < len ; i += 2) {
		sum=0;
		added=0;
		if (i%bytesInPixel==0){
			columnNumber++;
			
		}
		if (i%(10*bytesInPixel)==0){
			Log.d("AAA","convert to sound, bytes:"+i);
		}
			for (int row = 0; row < CommonConstans.IMAGE_HEIGHT; row++) {
			fileName = fileMatrix[row][columnNumber];
			byte[] data = mSounds.get(fileName);
				
			
			try{
if (data==null){
	Log.d("DEBUG",fileName);
}
			dataInShort= (short) ((data[44 +i] & 0xFF) | (data[44+i + 1] & 0xFF) << 8);
			}catch(IndexOutOfBoundsException ex){
				if (fileName.equals("scilence")){
					dataInShort=0;
				}
			}
			

			dataInShort*=volumeMatrix[row][columnNumber];
			
			
			dataInInt=(int)dataInShort;
			if (dataInInt!=0){
				added++;
			}
			sum+=dataInInt;
			}
			outputInt=added!=0?(int) (sum/(Math.sqrt(added))):sum;
			outputSample = (short)(outputInt);

			mFinalSound[i]=(byte) (outputSample & 0xff);
					mFinalSound[i+1]=(byte) ((outputSample >> 8) & 0xff);

	}
	endnow = android.os.SystemClock.uptimeMillis();
	Log.d("MYTAG", "Excution time: "+(endnow-startnow)+" ms");
}
}




