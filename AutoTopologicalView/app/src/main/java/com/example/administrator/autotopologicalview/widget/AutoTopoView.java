package com.example.administrator.autotopologicalview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import com.example.administrator.autotopologicalview.R;
import com.example.administrator.autotopologicalview.entity.BitmapEntity;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/8/008.
 */

public class AutoTopoView extends FrameLayout {

    private Context mContext;
    private Paint mTextPaint;//文本画笔
    private Paint mCirclePaint;
    private Paint mDLinePaint;//虚线画笔
    private Paint mRLinePaint;//实线画笔
    private Path mPath;
    private PathEffect effects;//虚线化类

    private int radiusX;//中心圆心X
    private int radiusY;//中心圆心Y
    private int DeviceRadiusX;//其他圆心X
    private int DeviceRadiusY;//其他圆心Y
    private float centerRadius;//中间设备半径
    private float deviceRadius;//其他设备半径
    private float wifiRadius;//WIFI设备半径

    private int centerPointX;//连接线中起始点X
    private int centerPointY;//连接线中起始点Y
    private int devicePointX;//连接线中终止点X
    private int devicePointY;//连接线中终止点X
    private int betweenPointX;//连接线中中间点X
    private int betweenPointY;//连接线中中间点X

    //绘制的相关DATA
    private ArrayList<BitmapEntity> dataList;
    //需要先创建的Btimap的list
    private ArrayList<Bitmap> bitmapList = new ArrayList<>();
    /**
     * 控制要绘图的部分
     */
    private Rect mSrcRect;
    /**
     * 控制要绘图的位置与大小
     */
    private RectF mDesRect;

    //平移缩放操作--->
    // 屏幕宽高
    private int screenHeight;
    private int screenWidth;
    private ViewDragHelper mDragHelper;
    private long lastMultiTouchTime;// 记录多点触控缩放后的时间
    private ScaleGestureDetector mScaleGestureDetector = null;

    public  boolean isScale = false;
    private float scale;
    private float preScale = 1;// 默认前一次缩放比例为1

    private boolean canTranslate = false;//是否能平移控制变量
    private boolean canScale = false;//是否能缩放控制变量
    private PointF lastPointF;//记录上次手指的位置
    private float moveDistanceX = 0;//平移X距离
    private float moveDistanceY = 0;//平移Y距离
    private boolean isAllowTranslateLayout = true;//默认是否可以平移布局 默认为可以 true

    public AutoTopoView(@NonNull Context context) {
        super(context);

    }

    public AutoTopoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoTopoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStrokeWidth(6);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        //创建文本画笔
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(16);

        //创建实线画笔
        mRLinePaint = new Paint();
        mRLinePaint.setAntiAlias(true);
        mRLinePaint.setColor(Color.WHITE);
        mRLinePaint.setStrokeWidth(3);
        mRLinePaint.setStyle(Paint.Style.STROKE);

        //创建虚线画笔
        mDLinePaint = new Paint();
        mDLinePaint.setAntiAlias(true);
        mDLinePaint.setColor(Color.WHITE);
        mDLinePaint.setStrokeWidth(3);
        effects = new DashPathEffect(new float[] { 10f, 10f }, 0);
        mDLinePaint.setPathEffect(effects);
        mDLinePaint.setStyle(Paint.Style.STROKE);

        //路径Path
        mPath = new Path();

        //缩放平移操作
        mDragHelper = ViewDragHelper.create(this, callback);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());
        lastPointF = new PointF();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);

        return isScale;
    }

    private boolean needToHandle=true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()){

            case MotionEvent.ACTION_DOWN:
                if (isAllowTranslateLayout){
                    canScale = false;
                    canTranslate = true;
                    lastPointF.set(event.getRawX(), event.getRawY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                translateLayout(new PointF(event.getRawX(), event.getRawY()));
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2){
                    canScale = true;
                    canTranslate = false;
                }
                break;

            case MotionEvent.ACTION_UP:
                canTranslate = false;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastMultiTouchTime > 200&&needToHandle) {
//                  多点触控全部手指抬起后要等待200毫秒才能执行单指触控的操作，避免多点触控后出现颤抖的情况
                    try {
                        mDragHelper.processTouchEvent(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                canTranslate = false;
                break;
        }

        if (canScale){
            return mScaleGestureDetector.onTouchEvent(event);//让mScaleGestureDetector处理触摸事件
        }else {
            return true;
        }
    }


    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);

        return isScale;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radiusX = getWidth()/2;
        radiusY = getHeight()/2;
//        Log.e("TAG", "radiusX; "+ radiusX+"  radiusY:"+radiusY);
    }

    //初始化Bitmap
    private void initBitmap() {
        if (dataList != null){
            if (dataList.size()>0){
                for (int i=0; i<dataList.size(); i++){
                    Bitmap b = ((BitmapDrawable)mContext.getResources().getDrawable(dataList.get(i).getId())).getBitmap();
                    bitmapList.add(b);
                }
                //准备需要绘制bitmap的区域（公共）
                mSrcRect = new Rect(0,0,bitmapList.get(0).getWidth(),bitmapList.get(0).getHeight());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //postInvalidate() 会重新调用onDraw方法
        //刷新onDraw时重置bitmapList
        if (bitmapList.size()>0){
            bitmapList.clear();
        }
        initBitmap();

        //先画中间的bitmap
        deviceRadius = getWidth()/(2*10);
        centerRadius = deviceRadius*(1.5f);
        mDesRect = new RectF(radiusX - centerRadius, radiusY - centerRadius, radiusX + centerRadius, radiusY + centerRadius);
        canvas.drawBitmap(((BitmapDrawable)mContext.getResources().getDrawable(R.mipmap.logo_shanzhai)).getBitmap(),
                mSrcRect, mDesRect, null);

        //画设备和连接线
        drawDeviceAndLine(canvas);

    }

    /**
     * 画文本
     * @param canvas
     * @param count 计数
     */
    private void drawText(Canvas canvas, int count) {
        //根据象限画对应文本
        String name = dataList.get(count).getName().trim();
        float strLength = mTextPaint.measureText(name);
        if (betweenPointX>=radiusX&&betweenPointY>=radiusY){//第二象限
            canvas.drawText(name, DeviceRadiusX, DeviceRadiusY + 20 + deviceRadius, mTextPaint);
        }else if (betweenPointX>=radiusX&&betweenPointY<=radiusY){//第一象限
            canvas.drawText(name, DeviceRadiusX, DeviceRadiusY - deviceRadius - 3, mTextPaint);
        }else if (betweenPointX<=radiusX&&betweenPointY>=radiusY){//第三象限
            canvas.drawText(name, DeviceRadiusX - strLength, DeviceRadiusY + 20 + deviceRadius, mTextPaint);
        } else if (betweenPointX<=radiusX&&betweenPointY<=radiusY){//第四象限
            canvas.drawText(name, DeviceRadiusX - strLength, DeviceRadiusY - 3 - deviceRadius, mTextPaint);
        }
    }

    /**
     * 画其他设备和连接线
     * @param canvas
     */
    private void drawDeviceAndLine(Canvas canvas) {
        if (dataList != null){
            if (dataList.size()>0){
                int averageAngle = 360/(dataList.size());
                for (int i = 0;i<dataList.size();i++){
                    calculatePoint(i*averageAngle, averageAngle);
                    mDesRect = new RectF(DeviceRadiusX - deviceRadius, DeviceRadiusY - deviceRadius,
                            DeviceRadiusX + deviceRadius, DeviceRadiusY + deviceRadius);

                    canvas.drawBitmap(bitmapList.get(i), mSrcRect, mDesRect, null);

                    if (dataList.get(i).getType() == 0){
                        mPath.reset();
                        mPath.moveTo(centerPointX, centerPointY);
                        mPath.cubicTo(centerPointX, centerPointY, betweenPointX,
                                betweenPointY, devicePointX, devicePointY);
                        canvas.drawPath(mPath, mDLinePaint);

                        //如果是虚线连接需要加WI-FI图标
                        mDesRect = new RectF(betweenPointX - (deviceRadius*0.4f), betweenPointY - (deviceRadius*0.4f),
                                betweenPointX + (deviceRadius*0.4f), betweenPointY + (deviceRadius*0.4f));
                        canvas.drawBitmap(((BitmapDrawable)mContext.getResources().getDrawable(R.mipmap.wifi)).getBitmap(),
                                mSrcRect, mDesRect, null);

                    }else if (dataList.get(i).getType() == 1){
                        //每次重置path 不然会错乱
                        mPath.reset();
                        mPath.moveTo(centerPointX, centerPointY);
                        mPath.cubicTo(centerPointX, centerPointY, betweenPointX,
                                betweenPointY, devicePointX, devicePointY);
                        canvas.drawPath(mPath, mRLinePaint);
                    }
                    //画文本
                    drawText(canvas, i);
                }
            }
        }
    }

    /**
     * 计算坐标和偏移值
     * @param initAngle 每个设备区间的初始角度
     * @param avAngle 每个设备区间的终止角度
     */
    private void calculatePoint(int initAngle, int avAngle){
        //计算每个区间的中间角度值
        double confirmAngle = (double)(initAngle + (avAngle/2));
        //生成一个随机数来控制其他设备的圆心点的随机位置
        Random random = new Random();
        int offset = random.nextInt(50);
//        Log.e("TAG", "offset; "+ offset);
        //计算其他设备的圆心
        DeviceRadiusX = radiusX + (int)((radiusX - 70 - offset)*Math.sin(Math.toRadians(confirmAngle)));
        DeviceRadiusY = radiusY - (int)((radiusY - 70 - offset)*Math.cos(Math.toRadians(confirmAngle)));
//        Log.e("TAG", "initAngle: "+initAngle+"  averageAngle: "+avAngle+"  ANGLE: "+confirmAngle+"  X; "+ DeviceRadiusX+"  Y: "+DeviceRadiusY);
        //计算连接线起始点的坐标
        centerPointX = radiusX + (int)(centerRadius*Math.sin(Math.toRadians(confirmAngle)));
        centerPointY = radiusY - (int)(centerRadius*Math.cos(Math.toRadians(confirmAngle)));
        //计算连接线终止点的坐标
        devicePointX = DeviceRadiusX - (int)(deviceRadius*Math.sin(Math.toRadians(confirmAngle)));
        devicePointY = DeviceRadiusY + (int)(deviceRadius*Math.cos(Math.toRadians(confirmAngle)));
        //计算连接线中间点的坐标
        int offsetX = (int)((centerRadius*1.5f)*Math.sin(Math.toRadians(avAngle/2)));
        if (devicePointX>radiusX){
            betweenPointX = radiusX + (devicePointX - radiusX)/2  ;
        }else {
            betweenPointX = devicePointX + (radiusX - devicePointX)/2 ;
        }

        if (devicePointY>radiusY){
            betweenPointY = devicePointY + (radiusY - devicePointY)/2 ;
        }else {
            betweenPointY = radiusY + (devicePointY - radiusY)/2 ;
        }

        //以中间圆心为坐标原点根据象限计算贝塞尔中间点的坐标(制造伪随机)
        if (betweenPointX>=radiusX&&betweenPointY>=radiusY){//第二象限
            betweenPointX = betweenPointX + offsetX;
            betweenPointY = betweenPointY - offsetX;
        }else if (betweenPointX>=radiusX&&betweenPointY<=radiusY){//第一象限
            betweenPointX = betweenPointX - offsetX;
            betweenPointY = betweenPointY - offsetX;
        }else if (betweenPointX<=radiusX&&betweenPointY>=radiusY){//第三象限
            betweenPointX = betweenPointX - offsetX;
            betweenPointY = betweenPointY - offsetX;
        } else if (betweenPointX<=radiusX&&betweenPointY<=radiusY){//第四象限
            betweenPointX = betweenPointX + offsetX;
            betweenPointY = betweenPointY - offsetX;
        }

    }

    //调用此方法可重新执行onDraw方法 新建View和数据改变是调用此方法
    public void setData(ArrayList<BitmapEntity> be){
        this.dataList = be;
        postInvalidate();
    }

    /**
     * 开放给外部设置是否允许平移布局
     * @param isAllowTranslate
     */
    public void canTranslateLayout(boolean isAllowTranslate){
        this.isAllowTranslateLayout = isAllowTranslate;
    }

    /************************************************平移缩放********************************************************/
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         *
         * @param child
         *            当前触摸的子view
         * @param pointerId
         * @return true就捕获并解析；false不捕获
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (preScale > 1){
                return true;
            }
            return false;
        }

        /**
         * 控制水平方向上的位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (left < (screenWidth - screenWidth * preScale) / 2)
                left = (int) (screenWidth - screenWidth * preScale) / 2;// 限制mainView可向左移动到的位置
            if (left > (screenWidth * preScale - screenWidth) / 2)
                left = (int) (screenWidth * preScale - screenWidth) / 2;// 限制mainView可向右移动到的位置
            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {

            if (top < (screenHeight - screenHeight * preScale) / 2) {
                top = (int) (screenHeight - screenHeight * preScale) / 2;// 限制mainView可向上移动到的位置
            }
            if (top > (screenHeight * preScale - screenHeight) / 2) {
                top = (int) (screenHeight * preScale - screenHeight) / 2;// 限制mainView可向上移动到的位置
            }
            return top;
        }

    };

    public class ScaleGestureListener implements
            ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan();// 前一次双指间距
            float currentSpan = detector.getCurrentSpan();// 本次双指间距
            if (currentSpan < previousSpan) {
                // 缩小
                scale = preScale - (previousSpan - currentSpan) / 750;
            } else {
                // 放大
                scale = preScale + (currentSpan - previousSpan) / 750;
            }
            // 缩放view
            if (scale > 0.5 && scale<1.5) {
                ViewHelper.setScaleX(AutoTopoView.this, scale);// x方向上缩放
                ViewHelper.setScaleY(AutoTopoView.this, scale);// y方向上缩放
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale;// 记录本次缩放比例
            lastMultiTouchTime = System.currentTimeMillis();// 记录双指缩放后的时间
        }
    }

    /**
     * 平移布局操作
     * @param pf
     */
    private void translateLayout(PointF pf){

        if (canTranslate){
            moveDistanceX = pf.x - lastPointF.x;
            moveDistanceY = pf.y - lastPointF.y;
            ViewHelper.setTranslationX(AutoTopoView.this, moveDistanceX);
            ViewHelper.setTranslationY(AutoTopoView.this, moveDistanceY);
        }
    }

}
