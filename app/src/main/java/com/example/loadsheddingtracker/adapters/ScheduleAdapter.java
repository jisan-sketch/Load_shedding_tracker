package com.example.loadsheddingtracker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.models.ScheduleModel;

import java.util.List;

// LECTURE TOPIC: Custom ListView with custom ArrayAdapter and convertView reuse pattern (ViewHolder pattern)
// 
// EXPLANATION FOR VIVA:
// 1. In standard ListView, inflation of layouts via LayoutInflater is an expensive operation.
// 2. The convertView parameter passed to getView() provides a recycled View that has scrolled off the screen.
// 3. By reusing convertView if it is NOT null, we avoid calling inflate() repeatedly.
// 4. By storing a static ViewHolder object in convertView.setTag(), we avoid calling findViewById() repeatedly.
//    This pattern achieves 60 FPS smooth scrolling in ListView.
public class ScheduleAdapter extends ArrayAdapter<ScheduleModel> {

    private final Context mContext;
    private final int mResource;

    // Static ViewHolder pattern to cache view references
    static class ViewHolder {
        TextView tvTime;
        TextView tvArea;
        TextView tvStatus;
        ImageView imgIcon;
    }

    public ScheduleAdapter(@NonNull Context context, int resource, @NonNull List<ScheduleModel> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ScheduleModel item = getItem(position);
        ViewHolder holder;

        // Check if an existing view is being reused (convertView reuse pattern)
        if (convertView == null) {
            // convertView is null: Inflate a new row layout from XML
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            // Create and initialize the ViewHolder
            holder = new ViewHolder();
            holder.tvTime = convertView.findViewById(R.id.tv_schedule_time);
            holder.tvArea = convertView.findViewById(R.id.tv_schedule_area);
            holder.tvStatus = convertView.findViewById(R.id.tv_schedule_status);
            holder.imgIcon = convertView.findViewById(R.id.img_schedule_icon);

            // Store the holder in the view tag for future fast lookup
            convertView.setTag(holder);
        } else {
            // convertView exists: Reuse the cached ViewHolder from tag
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate the views with data using item at current position
        if (item != null) {
            holder.tvTime.setText(item.getTimeRangeFormatted());
            holder.tvArea.setText(item.getArea() + " • " + item.getDayOfWeek());
            holder.tvStatus.setText(item.getStatus());

            if ("Restored".equalsIgnoreCase(item.getStatus())) {
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.imgIcon.setImageResource(R.drawable.ic_flash_on);
            } else {
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Red
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.imgIcon.setImageResource(R.drawable.ic_power_off);
            }
        }

        return convertView;
    }
}

