package videorawtest.example.com.videorawtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class GestureZoomActivity extends AppCompatActivity {

    private Bitmap mBitmap;
    private ImageView mImageView;

    private int mScreenWidth, mScreenHeight;//屏幕宽高

    private Matrix mCurrentMatrix = new Matrix();
    private Matrix mPreMatrix = new Matrix();

    private int mode = 0;
    private float mCurDistance;
    private float mPreDistance;

    private PointF mMidPointF = new PointF();//两指中点
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesturezoom);

        context = GestureZoomActivity.this;
        mImageView = (ImageView) findViewById(R.id.myImageView);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        mCurrentMatrix.setScale(0.5f, 0.5f); //显示先缩小一些
        center();//缩小后居中
        mImageView.setImageMatrix(mCurrentMatrix);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    //单个手指触摸
                    case MotionEvent.ACTION_DOWN:
                        mode = 1;
                        break;
                    //两指触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mPreDistance = getDistance(event);
                        //当两指间距大于10时，计算两指中心点
                        if (mPreDistance > 10f) {
                            mMidPointF = getMid(event);
                            mPreMatrix.set(mCurrentMatrix);
                            mode = 2;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = 0;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //当两指缩放，计算缩放比例
                        if (mode == 2) {
                            mCurDistance = getDistance(event);
                            if (mCurDistance > 10f) {
                                mCurrentMatrix.set(mPreMatrix);
                                float scale = mCurDistance / mPreDistance;
                                mCurrentMatrix.postScale(scale, scale, mMidPointF.x, mMidPointF.y);//缩放比例和中心点坐标
                            }

                        }
                        break;
                }
                view.setImageMatrix(mCurrentMatrix);

                center();  //回弹，令图片居中
                return true;
            }
        });


    }

    /*获取两指之间的距离*/
    private float getDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float distance = (float) Math.sqrt(x * x + y * y);//两点间的距离
        return distance;
    }

    /*使图片居中*/
    private void center() {
        Matrix m = new Matrix();
        m.set(mCurrentMatrix);

        int nImageViewHeight = mImageView.getHeight();
        int nImageViewWidth = mImageView.getWidth();


        //绘制图片矩形
        //这样rect.left，rect.right,rect.top,rect.bottom分别就就是当前屏幕离图片的边界的距离
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;

        //屏幕的宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); //获取屏幕分辨率
        mScreenWidth = dm.widthPixels;  //屏幕宽度
        mScreenHeight = dm.heightPixels;  //屏幕高度

        //获取ActionBar的高度
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        //计算Y到中心的距离
        if (height < mScreenHeight) {
            deltaY = (mScreenHeight - height) / 2 - rect.top - actionBarHeight;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < mScreenHeight) {
            deltaY = mImageView.getHeight() - rect.bottom;
        }

        //计算X到中心的距离
        if (width < mScreenWidth) {
            deltaX = (mScreenWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < mScreenWidth) {
            deltaX = mScreenWidth - rect.right;
        }
        mCurrentMatrix.postTranslate(deltaX, deltaY);

    }

    /*取两指的中心点坐标*/
    public static PointF getMid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}
