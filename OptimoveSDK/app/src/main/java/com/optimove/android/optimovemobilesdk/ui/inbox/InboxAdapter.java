package com.optimove.android.optimovemobilesdk.ui.inbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimobile.InAppInboxItem;
import com.optimove.android.optimovemobilesdk.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<InAppInboxItem> items;
    private OnItemClickListener mListener;

    public InboxAdapter(List<InAppInboxItem> items, OnItemClickListener listener) {
        this.items = items;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_inbox_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxAdapter.ViewHolder holder, int position) {

        holder.getTitleText().setText(items.get(position).getTitle());
        holder.getExpandedContent().setText(items.get(position).getSubtitle());

        URL imageUrl = items.get(position).getImageUrl();
        Picasso.get().load(String.valueOf(imageUrl)).into(holder.getExpandedImage());

        holder.getTitleLayout().setOnClickListener(v -> {
            if (holder.isExpanded()) {
                holder.getExpandedLayout().setVisibility(View.GONE);
                holder.getIndicator().setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
            } else {
                holder.getExpandedLayout().setVisibility(View.VISIBLE);
                holder.getIndicator().setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
            }
            holder.setExpanded(!holder.isExpanded);
        });

        holder.getMarkAsReadButton().setOnClickListener(v -> {
            mListener.onMarkAsRead(items.get(position));
        });

        holder.getDeleteItemsButton().setOnClickListener(v -> {
            mListener.onDelete(items.get(position));
        });

    }

    public interface OnItemClickListener {
        void onMarkAsRead(InAppInboxItem item);
        void onDelete(InAppInboxItem item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final ConstraintLayout titleLayout;
        private final ImageView indicator;

        private final ImageButton markAsReadButton;
        private final ImageButton deleteItemsButton;
        private final ConstraintLayout expandedLayout;
        private final ImageView expandedImage;
        private final TextView expandedContent;

        private boolean isExpanded;

        public ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.titleText);
            titleLayout = view.findViewById(R.id.titleLayout);
            indicator = view.findViewById(R.id.indicator);
            markAsReadButton = view.findViewById(R.id.markAsReadButton);
            deleteItemsButton = view.findViewById(R.id.deleteItemsButton);

            expandedLayout = view.findViewById(R.id.expandedLayout);
            expandedImage = view.findViewById(R.id.expandedImage);
            expandedContent = view.findViewById(R.id.expandedContent);

            isExpanded = false;
        }

        public TextView getTitleText() {
            return titleText;
        }

        public ConstraintLayout getTitleLayout() {
            return titleLayout;
        }

        public ConstraintLayout getExpandedLayout() {
            return expandedLayout;
        }

        public ImageView getExpandedImage() {
            return expandedImage;
        }

        public TextView getExpandedContent() {
            return expandedContent;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean isExpanded) {
            this.isExpanded = isExpanded;
        }

        public ImageView getIndicator() {
            return  indicator;
        }

        public ImageButton getMarkAsReadButton() {
            return markAsReadButton;
        }

        public ImageButton getDeleteItemsButton() {
            return  deleteItemsButton;
        }
    }

}
