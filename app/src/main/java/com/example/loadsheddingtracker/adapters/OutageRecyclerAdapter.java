package com.example.loadsheddingtracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.activities.OutageDetailActivity;
import com.example.loadsheddingtracker.models.Outage;

import java.util.List;

// LECTURE TOPIC: RecyclerView & ViewHolder Pattern
// 
// EXPLANATION FOR VIVA:
// 1. Unlike ListView where ViewHolder was an optional pattern, RecyclerView enforces the ViewHolder pattern by design.
// 2. onCreateViewHolder: Inflates the XML item layout and creates a new OutageViewHolder.
// 3. onBindViewHolder: Binds data at 'position' to the ViewHolder views.
// 4. getItemCount: Returns total number of items in the dataset.
// 5. holder.itemView.setOnClickListener: Handles clicks on items and starts OutageDetailActivity via explicit Intent.
public class OutageRecyclerAdapter extends RecyclerView.Adapter<OutageRecyclerAdapter.OutageViewHolder> {

    private final Context mContext;
    private final List<Outage> mOutageList;

    public OutageRecyclerAdapter(Context context, List<Outage> outageList) {
        this.mContext = context;
        this.mOutageList = outageList;
    }

    // Static ViewHolder class extending RecyclerView.ViewHolder
    public static class OutageViewHolder extends RecyclerView.ViewHolder {
        TextView tvArea;
        TextView tvDuration;
        TextView tvTimes;
        TextView tvNotes;
        TextView tvReporter;

        public OutageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArea = itemView.findViewById(R.id.tv_outage_area);
            tvDuration = itemView.findViewById(R.id.tv_outage_duration);
            tvTimes = itemView.findViewById(R.id.tv_outage_times);
            tvNotes = itemView.findViewById(R.id.tv_outage_notes);
            tvReporter = itemView.findViewById(R.id.tv_outage_reporter);
        }
    }

    @NonNull
    @Override
    public OutageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_outage, parent, false);
        return new OutageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutageViewHolder holder, int position) {
        // Get model at position
        Outage outage = mOutageList.get(position);

        holder.tvArea.setText((outage.area != null ? outage.area : "Area") + " Outage");
        holder.tvDuration.setText(outage.durationMinutes + " min");
        holder.tvTimes.setText("Time: " + (outage.startTime != null ? outage.startTime : "--") + " - " + (outage.endTime != null ? outage.endTime : "--"));
        holder.tvNotes.setText("Notes: " + (outage.notes != null && !outage.notes.isEmpty() ? outage.notes : "Scheduled outage"));
        holder.tvReporter.setText("User: " + (outage.userId != null ? outage.userId : "Anonymous"));

        // LECTURE TOPIC: Explicit Intent passing data with putExtra
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, OutageDetailActivity.class);
            intent.putExtra("EXTRA_DOC_ID", outage.id);
            intent.putExtra("EXTRA_AREA", outage.area);
            intent.putExtra("EXTRA_START_TIME", outage.startTime);
            intent.putExtra("EXTRA_END_TIME", outage.endTime);
            intent.putExtra("EXTRA_DURATION", outage.durationMinutes);
            intent.putExtra("EXTRA_NOTES", outage.notes);
            intent.putExtra("EXTRA_USER_ID", outage.userId);
            intent.putExtra("EXTRA_SOURCE", "FIRESTORE");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mOutageList != null ? mOutageList.size() : 0;
    }
}

