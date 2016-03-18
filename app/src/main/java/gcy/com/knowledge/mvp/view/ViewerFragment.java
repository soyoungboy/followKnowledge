package gcy.com.knowledge.mvp.view;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import gcy.com.knowledge.R;
import gcy.com.knowledge.libraries.TouchImageView;
import gcy.com.knowledge.ui.BaseFragment;
import gcy.com.knowledge.utils.BlurBuilder;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Share;
import gcy.com.knowledge.utils.Tool;
import gcy.com.knowledge.utils.UI;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ViewerFragment extends BaseFragment implements View.OnLongClickListener,View.OnClickListener{

    @Bind(R.id.headImage)
    ImageView imageView;
    //TouchImageView imageView;

    private DetailActivity activity;
    private String url;
    private AsyncTask loadPicture;
    private AsyncTask share;
    private AsyncTask save;
    private  Bitmap bitmap;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_viewer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=loadPicture){
            loadPicture.cancel(true);
        }
    }

    public static ViewerFragment newInstance(String Url) {
        Bundle args = new Bundle();
        args.putString(Constants.URL,Url);
        ViewerFragment fragment = new ViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        url = getArguments().getString(Constants.URL);
        activity = (DetailActivity) getActivity();
        ViewCompat.setTransitionName(imageView, url);
        loadPicture = new LoadPictureTask().execute();
    }
    protected class LoadPictureTask extends AsyncTask<Void,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Void... voids) {
            if(isCancelled()){
                return null;
            }
                       try {
                bitmap= Glide.with(ViewerFragment.this).load(url)
                        .asBitmap()
                        .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap picture) {
            if(isCancelled()){
                return ;
            }
            imageView.setImageBitmap(picture);
            activity.supportStartPostponedEnterTransition();//提前处理共享元素
        }
    }
    private class ShareIntentTask extends AsyncTask<Bitmap, Void, Uri> {

        @Override
        protected Uri doInBackground(Bitmap... params) {
            if (isCancelled()) {
                return null;
            }
            return Tool.bitmapToUri(params[0]);
        }

        @Override
        protected void onPostExecute(Uri result) {
            Share.shareImage(activity, result);
        }
    }
    private class SaveImageTask extends AsyncTask<Bitmap, Void, File> {

        @Override
        protected File doInBackground(Bitmap... params) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
            try {
                FileOutputStream stream = new FileOutputStream(file);
                params[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            if (file.exists()) {
                Snackbar.make(rootView, getString(R.string.save_img_success)
                        + file.getAbsolutePath(), Snackbar.LENGTH_SHORT).show();
            } else {
                UI.showSnack(rootView, R.string.save_img_failed);
            }
        }
    }

    /**
     * this is a test for blur bitmap, the result seems good.
     */
    private void blurImage() {
        new AsyncTask<Bitmap, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Bitmap... bitmaps) {
                //change the 'reuseBitmap' to true to blur the image persistently
                return BlurBuilder.blur(bitmaps[0], BlurBuilder.BLUR_RADIUS_MEDIUM, false);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);

            }
        }.execute(bitmap);
    }

    @Override
    protected void initData() {
        imageView.setOnLongClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        activity.toggleUI();
    }

    @Override
    public boolean onLongClick(View v) {
        blurImage();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String[] items = {getString(R.string.share_to), getString(R.string.save_img)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    share = new ShareIntentTask().execute(bitmap);
                } else if (which == 1) {
                    activity.hideSystemUi();
                    save = new SaveImageTask().execute(bitmap);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                imageView.setImageBitmap(bitmap);
            }
        });
        dialog.show();
        return true;
    }
}
