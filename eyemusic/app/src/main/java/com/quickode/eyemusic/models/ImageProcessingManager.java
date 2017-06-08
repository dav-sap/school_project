package com.quickode.eyemusic.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.GlobalParameters;

public class ImageProcessingManager {

	private Context mContext;
	private String[][] mFilesMatrix;
	private float[][] mVolumeMatrix;
	float[] hsvReverse= new float[3];
	public ImageProcessingManager(Context context){
		this.mContext=context;
	}
	public Bitmap analizePhoto(Bitmap img){

		mFilesMatrix= new String[CommonConstans.IMAGE_HEIGHT][CommonConstans.IMAGE_WIDTH];
		mVolumeMatrix=new float[CommonConstans.IMAGE_HEIGHT][CommonConstans.IMAGE_WIDTH];;
		Bitmap flatImg= Bitmap.createBitmap(CommonConstans.IMAGE_WIDTH,CommonConstans.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
		float[] hsv= new float[3];
		
		for (int y=0;y<CommonConstans.IMAGE_HEIGHT;y++){
		for (int x=0;x < CommonConstans.IMAGE_WIDTH;x++){

				int pixel = img.getPixel(x,y);
				int redValue = Color.red(pixel);
				int blueValue = Color.blue(pixel);
				int greenValue = Color.green(pixel);
			
				
			
				Color.RGBToHSV(redValue, greenValue, blueValue, hsv);
				HSL hsl = HSVtoHSL(new HSV(hsv));
				int flatColor = hslToColor(new HSV(hsv));
				float volume=hsl.Luminance;
				
				if (AppSettings.isBlackAndWhite()){
					flatColor=flatColor==Color.BLACK?Color.BLACK:Color.WHITE;
				}
				if (AppSettings.isNegative()){
					if (flatColor==Color.WHITE){
						flatColor=Color.BLACK;
						volume=0;
					}
					else if(flatColor==Color.BLACK){
						flatColor=Color.WHITE;
						volume=1-volume;
					}
				}
			int viewColor=handleColor(flatColor,volume);
				
				flatImg.setPixel(x, y, viewColor);
				mFilesMatrix[y][x]=getFileName(flatColor, y);
				mVolumeMatrix[y][x]=volume;
			
			}
			
			
		}
		return flatImg;
		

	}
	

	public String[][] getFilesMatrix(){
		return mFilesMatrix;
	}
	
	public float[][]getVolumeMatrix(){
		return mVolumeMatrix;
	}
	

	private HSL HSVtoHSL(HSV hsv){
		
		HSL hsl = new HSL();
		hsl.Hue=hsv.Hue;
		hsl.Luminance=(2-hsv.Saturation)*hsv.Brightness;
		hsl.Saturation=hsv.Saturation*hsv.Brightness;
		//decide between this 2 options.
		hsl.Saturation/=hsl.Saturation<=1?hsl.Luminance:2-hsl.Luminance;
		//hsl.Saturation/=hsl.Luminance<=1?hsl.Luminance:2-hsl.Luminance;
		hsl.Luminance/=2;;
		return hsl;
	}
	
	private HSL HSVtoHSLlum(HSV hsv){
		
		HSL hsl = new HSL();
		hsl.Hue=hsv.Hue;
		hsl.Luminance=(2-hsv.Saturation)*hsv.Brightness;
		hsl.Saturation=hsv.Saturation*hsv.Brightness;
		//decide between this 2 options.
		//hsl.Saturation/=hsl.Saturation<=1?hsl.Luminance:2-hsl.Luminance;
		hsl.Saturation/=hsl.Luminance<=1?hsl.Luminance:2-hsl.Luminance;
		hsl.Luminance/=2;;
		return hsl;
	}
	

	
	private String getFileName(int color, int row){
		String sign = null;
		String fileName=null;
		switch(color){
		case Color.WHITE:
			sign="a";
			break;
		case Color.RED:
			sign="b";
			break;
		case Color.GREEN:
			sign="c";
			break;
		case Color.YELLOW:
			sign="d";
			break;
		case Color.BLUE:
			sign="e";
			break;

		default:
			fileName="silence.wav";
			break;
		}
		if (fileName==null){
			fileName="f"+sign+"_"+Integer.toString(CommonConstans.IMAGE_HEIGHT-1-row)+".wav";
		}
		return fileName;
	}
	
	
	private int hslToColor(HSV hsv){
		GlobalParameters globalParameters= GlobalParameters.getInstance(mContext);
		HSL hsl=HSVtoHSLlum(hsv);
		
		if (hsl.Luminance>globalParameters.BRIGHTNESS || (hsl.Luminance>globalParameters.DARKNESS && hsl.Saturation< globalParameters.SITURATION)){
			return Color.WHITE;
		}
		else if (hsl.Luminance<globalParameters.DARKNESS){
			return Color.BLACK;
		}
		
		int [] colors={Color.RED,Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW};
		int[] hues={0,360,120,240,60};
		float min=360;
		int color=-1;
		for (int i=0;i<5;i++){
			if(min>Math.abs(hues[i]-hsl.Hue)){
				min=Math.abs(hues[i]-hsl.Hue);
				color=colors[i];
			}
		}
		return color;
	}
	
	private HSV HSLtoHSV(HSL hsl){
		
		HSV hsv = new HSV();
		hsv.Hue=hsl.Hue;
		hsl.Luminance*=2;//(2-hsv.Saturation)*hsv.Brightness;
	
		hsl.Saturation*=hsl.Luminance<=1?hsl.Luminance:2-hsl.Luminance;

hsv.Brightness=(hsl.Luminance+hsl.Saturation)/2;
hsv.Saturation=(2*hsl.Saturation)/(hsl.Luminance+hsl.Saturation);
		return hsv;
	}
	private int handleColor(int color, float sourceColorLuminance){
		float[] hsv= new float[3];
		Color.RGBToHSV(Color.red(color), Color.green(color),Color.blue(color), hsv);
		HSL flatColorHSL=HSVtoHSL(new HSV(hsv));
		//replace luminance of flat color to luminance of the sorce color
		flatColorHSL.Luminance=sourceColorLuminance;
		HSV newHSV=HSLtoHSV(flatColorHSL);
		int newColor= Color.HSVToColor(newHSV.HSVtoArray());
		return newColor;
		
		
	}

	
	
	
	private class HSV{
		public float Hue;
		public float Saturation;
		public float Brightness;
		
		public HSV(float[] hsv){
			Hue=hsv[0];
			Saturation=hsv[1];
			Brightness=hsv[2];
		}
		public HSV(){
		
		}
		
		public float[] HSVtoArray(){
			float[] hsv= new float[3];
			hsv[0]=this.Hue;
			hsv[1]=this.Saturation;
			hsv[2]=this.Brightness;
			return hsv;
		}
	}
	
	private class HSL{
		public float Hue;
		public float Saturation;
		public float Luminance;
	}

}
