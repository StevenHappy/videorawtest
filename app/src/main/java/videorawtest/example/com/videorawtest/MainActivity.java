package videorawtest.example.com.videorawtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidlibzxing.zxing.activity.CaptureActivity;

public class MainActivity extends AppCompatActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;

    private TextView mTextView;
    private ImageView mImageView;

    private Boolean bTouchImage = false;
    private Bitmap mBitmap;

    private float mScaleWidth;
    private float mScaleHeight;

    private float mScreenWidth;
    private float mScreenHeight;


    //动态的ImageView
    private ImageView getImageView() {
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        Matrix matrix = new Matrix();
        float nScale = (mScaleWidth > mScaleHeight) ? mScaleHeight : mScaleWidth;
        matrix.postScale(nScale, nScale);
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        iv.setImageBitmap(newBitmap);

        return iv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBitmap = null;

        DisplayMetrics dm = new DisplayMetrics();//创建矩阵
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels; //得到屏幕的宽度
        mScreenHeight = dm.heightPixels; //得到屏幕的高度

        bTouchImage = false;

        Button btnPlayVideo = (Button) findViewById(R.id.btnPlayVideo);
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });

        String action = this.getIntent().getAction();
        Parcelable[] rawMsgs = this.getIntent()
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Toast.makeText(this, "action",
                    Toast.LENGTH_SHORT).show();
        }

        mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
        mTextView = (TextView) findViewById(R.id.qrcode_title);

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                mTextView.setText(ZxingUtils.DecodeBitmap(bitmap));
                return true;// 必须返回true,防止事件继续往下派发，比如响应点击事件
            }
        });

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBitmap == null)
                    return;
                final Dialog dialog = new Dialog(MainActivity.this,R.style.edit_AlertDialog_style);
                ImageView newImageView = getImageView();
                dialog.setContentView(newImageView);
                MainActivity.this.setVisible(false);
                newImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        MainActivity.this.setVisible(true);
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        MainActivity.this.setVisible(true);
                    }
                });
                dialog.show();
            }
        });
        Button btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });

        Button btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("输入二维码信息")
                        .setView(input)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog01, int whichButton01) {

                                String value = input.getText().toString();
                                mBitmap = ZxingUtils.createBitmap(value, 400, 400);
                                mImageView.setImageBitmap(mBitmap);

                                int bitmapWidth = mBitmap.getWidth();
                                int bitmapHeight = mBitmap.getHeight();
                                DisplayMetrics dm = new DisplayMetrics();//创建矩阵
                                getWindowManager().getDefaultDisplay().getMetrics(dm);
                                mScreenWidth = dm.widthPixels; //得到屏幕的宽度
                                mScreenHeight = dm.heightPixels; //得到屏幕的高度
                                mScaleWidth = ((float) mScreenWidth) / bitmapWidth;
                                mScaleHeight = ((float) mScreenHeight) / bitmapHeight;

                            }
                        }).setCancelable(true).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mTextView.setText(bundle.getString("result"));
                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

}
