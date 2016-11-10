package com.alick.cameraphoto;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alick.cameraphoto.base.BasePermissionActivity;
import com.alick.cameraphoto.utils.BLog;
import com.alick.cameraphoto.utils.FileUtils;
import com.alick.cameraphoto.utils.GlideUtils;
import com.alick.cameraphoto.utils.T;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BasePermissionActivity {
//常量定义
    /**
     * 打开相机
     */
    public static final int OPEN_CAMERA = 10;
    /**
     * 打开相册:4.3及以一下
     */
    public static final int SELECT_PHOTO = 20;
    /**
     * 打开相册:4.4及以上
     */
    public static final int SELECT_PHOTO_KITKAT = 50;
    /**
     * 普通剪裁
     */
    public static final int CROP = 30;
    /**
     * 4.4及以上剪裁
     */
    public static final int CROP_KITKAT = 40;
    @BindView(R.id.btn_camera)
    Button btnCamera;
    @BindView(R.id.btn_photo)
    Button btnPhoto;
    @BindView(R.id.iv_result)
    ImageView ivResult;

    private Dialog updateHeadDialog;
    private File cameraFile;

    private static final int CODE_CAMERA_REQUEST = 1000;
    private static final int CODE_GALLERY_REQUEST = 2000;

    private static final int CODE_CROP = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_camera, R.id.btn_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                requestCamera();
                break;
            case R.id.btn_photo:
                requestStorage();
                break;
        }
    }

    @Override
    public void onGetCameraPerm(boolean isSuccessed) {
        if (!isSuccessed) {
            T.showShort(this, "未获得打开相机权限");
            return;
        }
        openCamera();
    }

    @Override
    public void onGetStoragePerm(boolean isSuccessed) {
        if (!isSuccessed) {
            T.showShort(this, "未获得打开相册权限");
            return;
        }
        openGallery();
    }

    //    ####################################
    private void openCamera() {
        try {
            cameraFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".png");
            if (!cameraFile.getParentFile().exists()) {
                cameraFile.getParentFile().mkdirs();
            }
            try {
                cameraFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //camera.setDataAndType(Uri.fromFile(picture), "image/*");
//            camera.putExtra("return-data", true);
            camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(camera, CODE_CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            T.showShort(this, "相机不存在");
        }
    }


    private void openGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
//    ####################################


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //1.相册选择完成后
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            //保存选择的相册图片
//            mePresenter.saveSelectPhoto(getActivity(), data);
            //2.拍照完成后
        } else if (requestCode == CODE_CAMERA_REQUEST && resultCode == RESULT_OK) {
            //保存图片类型的消息
            BLog.i("拍照后保存到图片路径:" + cameraFile.getAbsolutePath() + ",文件长度:" + cameraFile.length());
            if (cameraFile.length() == 0) {
                //有些手机拍照后的得到图片大小为0,所以需要剪切一下文件
                File desFile = new File(getExternalCacheDir(),System.currentTimeMillis()+ ".png");
                FileUtils.cutFile(cameraFile, desFile);
                cameraCropImageUri(Uri.parse(desFile.getAbsolutePath()));
//                mePresenter.saveCameraPhoto(getActivity(), desFile);
            } else {
//                mePresenter.saveCameraPhoto(getActivity(), cameraFile);
                cameraCropImageUri(Uri.parse(cameraFile.getAbsolutePath()));
            }
        } else if(requestCode == CODE_CROP && resultCode == RESULT_OK){
            GlideUtils.showImage(this,cameraFile.getAbsolutePath(),ivResult);
        }

    }











    /*====================照相和选择相册的方法-begin==================*/

    /**
     * <br>功能简述:裁剪图片方法实现---------------------- 相册
     * <br>功能详细描述:
     * <br>注意:
     */
    private void cropImageUri() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, SELECT_PHOTO);
    }


    /**
     * <br>功能简述:4.4以上裁剪图片方法实现---------------------- 相册
     * <br>功能详细描述:
     * <br>注意:
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void selectImageUriAfterKikat() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO_KITKAT);
    }

    /**
     * <br>功能简述:裁剪图片方法实现----------------------相机
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param uri
     */
    private void cameraCropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP);
    }

    /**
     * <br>功能简述: 4.4及以上改动版裁剪图片方法实现 --------------------相机
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param uri
     */
    private void cropImageUriAfterKikat(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_KITKAT);
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param uri
     * @return
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * <br>功能简述:4.4及以上获取图片的方法
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    /*====================照相和选择相册的方法-end==================*/
}
