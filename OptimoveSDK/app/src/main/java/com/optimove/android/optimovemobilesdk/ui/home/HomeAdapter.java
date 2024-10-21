package com.optimove.android.optimovemobilesdk.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimovemobilesdk.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final AtomicReference<List<MenuItem>> items = new AtomicReference<>();
    private final OnItemClickListener mListener;

    public HomeAdapter(List<MenuItem> items, OnItemClickListener listener) {
        this.items.set(items);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_home_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {

        holder.getPageIcon().setBackgroundResource(items.get().get(position).getImage());
        holder.getTitleText().setText(items.get().get(position).getText());

        holder.getTitleLayout().setOnClickListener(v -> mListener.onClick(items.get().get(position)));

    }

    public interface OnItemClickListener {
        void onClick(MenuItem item);
    }

    @Override
    public int getItemCount() {
        return items.get().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView pageIcon;
        private final TextView titleText;
        private final ConstraintLayout titleLayout;

        public ViewHolder(View view) {
            super(view);
            pageIcon = view.findViewById(R.id.pageIcon);
            titleText = view.findViewById(R.id.titleText);
            titleLayout = view.findViewById(R.id.titleLayout);
        }

        public TextView getTitleText() {
            return titleText;
        }

        public ConstraintLayout getTitleLayout() {
            return titleLayout;
        }

        public ImageView getPageIcon() {
            return pageIcon;
        }
    }

}
