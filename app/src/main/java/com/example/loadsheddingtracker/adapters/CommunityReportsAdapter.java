package com.example.loadsheddingtracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.activities.OutageDetailActivity;
import com.example.loadsheddingtracker.models.Report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// LECTURE TOPIC: RecyclerView Adapter for Live Firebase Realtime Database items (Report.java)
public class CommunityReportsAdapter extends RecyclerView.Adapter<CommunityReportsAdapter.ReportViewHolder> {

    public interface OnReportActionListener {
        void onDeleteReport(Report report);
    }

    private final Context mContext;
    private final List<Report> mReportList;
    private final OnReportActionListener mListener;

    public CommunityReportsAdapter(Context context, List<Report> reportList, OnReportActionListener listener) {
        this.mContext = context;
        this.mReportList = reportList;
        this.mListener = listener;
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvArea;
        TextView tvCause;
        TextView tvDesc;
        TextView tvMeta;
        Button btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArea = itemView.findViewById(R.id.tv_report_area);
            tvCause = itemView.findViewById(R.id.tv_report_cause);
            tvDesc = itemView.findViewById(R.id.tv_report_desc);
            tvMeta = itemView.findViewById(R.id.tv_report_meta);
            btnDelete = itemView.findViewById(R.id.btn_delete_report);
        }
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_community_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = mReportList.get(position);

        holder.tvArea.setText((report.area != null ? report.area : "Area") + " Outage");
        holder.tvCause.setText(report.cause != null ? report.cause : "Load-shedding");
        holder.tvDesc.setText(report.description != null ? report.description : "");

        long reportTime = report.time > 0 ? report.time : System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault());
        String timeStr = sdf.format(new Date(reportTime));
        String userDisplay = (report.userId != null && report.userId.length() > 6)
                ? "User " + report.userId.substring(0, 6)
                : (report.userId != null ? report.userId : "User");
        holder.tvMeta.setText("By " + userDisplay + " • " + timeStr);

        holder.btnDelete.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDeleteReport(report);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, OutageDetailActivity.class);
            intent.putExtra("EXTRA_DOC_ID", report.id);
            intent.putExtra("EXTRA_AREA", report.area);
            intent.putExtra("EXTRA_START_TIME", "Reported Live");
            intent.putExtra("EXTRA_END_TIME", "In Progress");
            intent.putExtra("EXTRA_DURATION", 0);
            intent.putExtra("EXTRA_NOTES", report.cause + " - " + report.description);
            intent.putExtra("EXTRA_EMAIL", userDisplay);
            intent.putExtra("EXTRA_USER_ID", report.userId);
            intent.putExtra("EXTRA_SOURCE", "RTDB");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mReportList != null ? mReportList.size() : 0;
    }
}
