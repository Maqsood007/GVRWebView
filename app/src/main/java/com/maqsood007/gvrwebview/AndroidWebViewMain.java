/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maqsood007.gvrwebview;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

import org.gearvrf.FutureWrapper;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBaseSensor;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRCursorController;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.ISensorEvents;
import org.gearvrf.SensorEvent;
import org.gearvrf.accessibility.GVRAccessibilityZoom;
import org.gearvrf.io.CursorControllerListener;
import org.gearvrf.io.GVRControllerType;
import org.gearvrf.io.GVRInputManager;
import org.gearvrf.scene_objects.GVRViewSceneObject;
import org.gearvrf.scene_objects.view.GVRFrameLayout;

import java.util.List;

class AndroidWebViewMain extends GVRMain {
    private static final String TAG = AndroidWebViewMain.class.getSimpleName();
    private static final int KEY_EVENT = 1;
    private static final int MOTION_EVENT = 2;

    private GVRContext context;

    private static final float QUAD_X = 6.0f;
    private static final float QUAD_Y = 6.0f;
    private static final float HALF_QUAD_X = QUAD_X / 2.0f;
    private static final float HALF_QUAD_Y = QUAD_Y / 2.0f;
    private static final float DEPTH = -3.0f;

    private GVRFrameLayout frameLayout;

    private int frameWidth;
    private int frameHeight;

    private GVRScene mainScene;
    private Handler mainThreadHandler;

    private final static PointerProperties[] pointerProperties;
    private final static PointerCoords[] pointerCoordsArray;
    private final static PointerCoords pointerCoords;
    private GVRSceneObject cursor;
    private GVRAccessibilityZoom gvrAccessibilityZoom;
    private AndroidWebViewActivity mActivity;
    private GVRViewSceneObject webViewSceneObject;

    private float count = 1, zoomCount = -4;


    static {
        PointerProperties properties = new PointerProperties();
        properties.id = 0;
        properties.toolType = MotionEvent.TOOL_TYPE_MOUSE;
        pointerProperties = new PointerProperties[]{properties};
        pointerCoords = new PointerCoords();
        pointerCoordsArray = new PointerCoords[]{pointerCoords};
    }


    AndroidWebViewMain(AndroidWebViewActivity activity) {

        this.mActivity = activity;

        mainThreadHandler = new Handler(mActivity.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // dispatch motion event

                if (msg.what == MOTION_EVENT) {
                    MotionEvent motionEvent = (MotionEvent) msg.obj;
                    frameLayout.dispatchTouchEvent(motionEvent);
                    frameLayout.invalidate();
                    motionEvent.recycle();
                }
            }
        };
    }


    @Override
    public void onInit(final GVRContext gvrContext) throws Throwable {
        context = gvrContext;
        gvrAccessibilityZoom = new GVRAccessibilityZoom();
        mainScene = gvrContext.getNextMainScene();
//        createSkybox();
        frameLayout = mActivity.getFrameLayout();

        DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        webViewSceneObject = new GVRViewSceneObject(gvrContext, frameLayout,
                context.createQuad(6.0f, 6.0f));



//        webViewSceneObject = new GVRWebViewSceneObject(gvrContext, 6.0f, 6.0f, mActivity.webView);


        mainScene.addSceneObject(webViewSceneObject);




        webViewSceneObject.getTransform().setPosition(0.0f, 0.0f, -4.0f);

        frameWidth = frameLayout.getWidth();
        frameHeight = frameLayout.getHeight();

        // set up the input manager for the main scene
        GVRInputManager inputManager = gvrContext.getInputManager();
        inputManager.addCursorControllerListener(listener);
        for (GVRCursorController cursor : inputManager.getCursorControllers()) {
            listener.onCursorControllerAdded(cursor);
        }
        GVRBaseSensor sensor = new GVRBaseSensor(gvrContext);
        webViewSceneObject.getEventReceiver().addListener(eventListener);
        webViewSceneObject.setSensor(sensor);

    }


    public boolean onTouchEvent(KeyEvent event) {


        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (count > 1) {
                    count--;
                    zoomCount++;
//                    imageView.setScaleX(count);
//                    imageView.setScaleY(count);

                    webViewSceneObject.getTransform().setScaleX(count);
                    webViewSceneObject.getTransform().setScaleY(count);
                    webViewSceneObject.getTransform().setPosition(0.0f, 0.0f, zoomCount);
                    Log.d("Zoom", "Applying zoom out");

                    if (gvrAccessibilityZoom != null && mainScene != null) {
                        gvrAccessibilityZoom.zoomOut(mainScene);
                    }
                }

                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {

                if (count < 4) {
                    count++;
                    zoomCount--;
//                imageView.setScaleX(count);
//                imageView.setScaleY(count);

                    webViewSceneObject.getTransform().setScaleX(count);
                    webViewSceneObject.getTransform().setScaleY(count);
                    webViewSceneObject.getTransform().setPosition(0.0f, 0.0f, zoomCount);
                    Log.d("Zoom", "Applying zoom in");

                    if (gvrAccessibilityZoom != null && mainScene != null) {
                        gvrAccessibilityZoom.zoomIn(mainScene);
                    }
                }


                return true;
            }
        }
        return true;
    }




//    @Override
//    public GVRTexture getSplashTexture(GVRContext gvrContext) {
//        Bitmap bitmap = BitmapFactory.decodeResource(
//                gvrContext.getContext().getResources(),
//                R.drawable.photo_pdf);
//        // return the correct splash screen bitmap
//        return new GVRBitmapTexture(gvrContext, bitmap);
//    }

    public static float applyRatioAt(double d) {
        return (float) (d * 0.5f);
    }

    private ISensorEvents eventListener = new ISensorEvents() {
        private static final float SCALE = 10.0f;
        private float savedMotionEventX, savedMotionEventY, savedHitPointX,
                savedHitPointY;

        @Override
        public void onSensorEvent(SensorEvent event) {
            List<MotionEvent> motionEvents = event.getCursorController().getMotionEvents();

            for (MotionEvent motionEvent : motionEvents) {
                int cont = motionEvent.getPointerCount();

                if (cont > 1) {
                    return;
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    pointerCoords.x = savedHitPointX
                            + ((motionEvent.getX() - savedMotionEventX) * SCALE);
                    pointerCoords.y = savedHitPointY
                            + ((motionEvent.getY() - savedMotionEventY) * SCALE);
                } else {
                    float[] hitPoint = event.getHitPoint();
                    pointerCoords.x = ((hitPoint[0] + HALF_QUAD_X) / QUAD_X) * frameWidth;
                    pointerCoords.y = (-(hitPoint[1] - HALF_QUAD_Y) / QUAD_Y) * frameHeight;

                    if (motionEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        // save the co ordinates on down
                        savedMotionEventX = motionEvent.getX();
                        savedMotionEventY = motionEvent.getY();

                        savedHitPointX = pointerCoords.x;
                        savedHitPointY = pointerCoords.y;
                    }
                }

                final MotionEvent clone = MotionEvent.obtain(
                        motionEvent.getDownTime(), motionEvent.getEventTime(),
                        motionEvent.getAction(), 1, pointerProperties,
                        pointerCoordsArray, 0, 0, 1f, 1f, 0, 0,
                        InputDevice.SOURCE_TOUCHSCREEN, 0);

                Message message = Message.obtain(mainThreadHandler, MOTION_EVENT, 0, 0,
                        clone);
                mainThreadHandler.sendMessage(message);
            }

            List<KeyEvent> keyEvents = event.getCursorController().getKeyEvents();
            for (KeyEvent keyEvent : keyEvents) {
                Message message = Message.obtain(mainThreadHandler, KEY_EVENT,
                        keyEvent.getKeyCode(), 0, null);
                mainThreadHandler.sendMessage(message);
            }
        }
    };

    private CursorControllerListener listener = new CursorControllerListener() {

        @Override
        public void onCursorControllerRemoved(GVRCursorController controller) {
            if (controller.getControllerType() == GVRControllerType.GAZE) {
                if (cursor != null) {
                    mainScene.getMainCameraRig().removeChildObject(cursor);
                }
                controller.setEnable(false);
            }
        }

        @Override
        public void onCursorControllerAdded(GVRCursorController controller) {
            // Only allow only gaze
            if (controller.getControllerType() == GVRControllerType.GAZE) {
                cursor = new GVRSceneObject(context,
                        new FutureWrapper<GVRMesh>(context.createQuad(0.1f, 0.1f)),
                        context.loadFutureTexture(new GVRAndroidResource(context, R.raw.dot_green)));
                cursor.getTransform().setPosition(0.0f, 0.0f, -3.0f);
                mainScene.getMainCameraRig().addChildObject(cursor);
                cursor.getRenderData().setDepthTest(false);
                cursor.getRenderData().setRenderingOrder(100000);
                controller.setPosition(0.0f, 0.0f, DEPTH);
                controller.setNearDepth(DEPTH);
                controller.setFarDepth(DEPTH);
            } else {
                // disable all other types
                controller.setEnable(false);
            }
        }
    };

    @Override
    public void onStep() {
    }


}
