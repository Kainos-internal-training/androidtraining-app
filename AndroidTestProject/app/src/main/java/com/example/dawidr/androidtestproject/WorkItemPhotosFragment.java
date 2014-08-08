package com.example.dawidr.androidtestproject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkItemPhotosFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private View rootView;
    private String mCurrentPhotoPath;

    public WorkItemPhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_work_item_photos, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        File file = createImageFile();
                        if (file != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            getActivity().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                            WorkPhoto workPhoto = new WorkPhoto();
                            workPhoto.path = file.getAbsolutePath();

                            WorkItemActivity.workItem.photos.add(workPhoto);
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);

                    intent.setDataAndType(Uri.fromFile(new File(WorkItemActivity.workItem.photos.get(position - 1).path)), "image/jpg");

                    getActivity().startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        imageAdapter = new ImageAdapter(rootView.getContext());
        gridView.setAdapter(imageAdapter);

        super.onResume();
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File image = new File(storageDir, "/androidtest/" + imageFileName);

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return WorkItemActivity.workItem.photos.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            if (position == 0)
                imageView.setImageResource(R.drawable.add_photo);
            else {
                imageView.setImageBitmap(decodeSampledBitmapFromFile(WorkItemActivity.workItem.photos.get(position - 1).path, 150, 150));
            }

            return imageView;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}