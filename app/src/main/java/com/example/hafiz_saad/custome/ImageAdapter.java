package com.example.hafiz_saad.custome;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    public ArrayList<String> mImagesList;
    public ArrayList<String> multiSelect;
    private Context mContext;
    private SparseBooleanArray mSparseBooleanArray;

    public ImageAdapter(Context context, ArrayList<String> imageList, ArrayList<String> multiSelect) {
        mContext = context;
        mSparseBooleanArray = new SparseBooleanArray();
        mImagesList = new ArrayList<String>();
        this.mImagesList = imageList;
        this.multiSelect = multiSelect;

    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<mImagesList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(mImagesList.get(i));
            }
        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiphoto_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String imageUrl = mImagesList.get(position);

        Glide.with(mContext)
                .load("file://"+imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(holder.imageView);
        if(multiSelect.contains(mImagesList.get(position))) {
            holder.checked.setVisibility(View.VISIBLE);
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        }
        else {
            holder.checked.setVisibility(View.GONE);
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));
        }

//        holder.checkBox.setTag(position);
//        holder.checkBox.setChecked(mSparseBooleanArray.get(position));
//        holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CheckBox checked;
        private RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView1);
            checked = (CheckBox) view.findViewById(R.id.checked);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.lists);
        }
    }

}
