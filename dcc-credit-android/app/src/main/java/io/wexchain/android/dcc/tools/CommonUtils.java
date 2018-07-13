package io.wexchain.android.dcc.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import io.wexchain.android.dcc.App;
import io.wexchain.dcc.R;

/**
 * @author Created by Wangpeng on 2018/7/11 14:59.
 * usage:
 */
public class CommonUtils {
    
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images
                    .ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    
    private static Uri getUri(Context context, String path) {
        Uri uri = null;
        if (path != null) {
            path = Uri.decode(path);
            Log.d("CommonUtils", "path2 is " + path);
            ContentResolver cr = context.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path +
                    "'").append(")");
            Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore
                    .Images.ImageColumns._ID}, buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                // set _id value
                index = cur.getInt(index);
            }
            if (index == 0) {
                //do nothing
            } else {
                Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                Log.d("CommonUtils", "uri_temp is " + uri_temp);
                if (uri_temp != null) {
                    uri = uri_temp;
                }
            }
        }
        return uri;
    }
    
    public static Uri str2Uri(String str) {
        if (TextUtils.isEmpty(str) || str == "null") {
            return getUriFromDrawableRes(App.get(), R.drawable.icon_default_avatar);
        } else {
            return Uri.parse(str);
        }
    }
    
    private static Uri getUriFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName
                (id) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }
    
    public static String setStatusName(int code) {
        if (0 == code) {
            return "添加";
        } else {
            return "已添加";
        }
    }
    
}
