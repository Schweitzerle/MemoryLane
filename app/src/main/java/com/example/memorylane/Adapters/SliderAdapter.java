package com.example.memorylane.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.memorylane.Classes.SliderItem;
import com.example.memorylane.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<SliderItem> sliderItemList;
    private ViewPager2 viewPager2;

    public SliderAdapter(Context context, List<SliderItem> sliderItemList, ViewPager2 viewPager2) {
        this.context = context;
        this.sliderItemList = sliderItemList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_container, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItemList.get(position));
        if (position == sliderItemList.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItemList.size();
    }


    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SliderItem sliderItem) {
            Glide.with(context).load(sliderItem.getImageUrl()).into(imageView);
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItemList.addAll(sliderItemList);
            notifyDataSetChanged();
        }
    };
}
