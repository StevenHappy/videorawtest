package videorawtest.example.com.videorawtest;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * Created by steven on 2018/4/29.
 */

//https://repo1.maven.org/maven2/com/google/zxing/android-core/3.2.0/

public class ZxingUtils {
    public static Bitmap createBitmap(String str,int ninWidth,int ninHeight){

        Bitmap bitmap = null;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(str,
                    BarcodeFormat.QR_CODE, ninWidth, ninHeight, hints);

            // BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
           // bitmap = barcodeEncoder.createBitmap(bitMatrix);

            int[] pixels = new int[ninWidth * ninHeight];
            for (int y = 0; y < ninHeight; y++) {
                for (int x = 0; x < ninWidth; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * ninWidth + x] = Color.BLACK;
                    } else {
                        pixels[y * ninWidth + x] = Color.WHITE;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(ninWidth, ninHeight,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, ninWidth, 0, 0, ninWidth, ninHeight);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String DecodeBitmap(Bitmap bitmap){
        String strValue = "";

        if (bitmap == null)
            return  strValue;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

       // MyRGBLuminanceSource rgbLuminanceSource = new MyRGBLuminanceSource(bitmap);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(width,height,pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        try {
            Result result = reader.decode(binaryBitmap, null);
            if(result != null)
                 strValue = result.getText();
        } catch (com.google.zxing.FormatException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return  strValue;
    }
}
