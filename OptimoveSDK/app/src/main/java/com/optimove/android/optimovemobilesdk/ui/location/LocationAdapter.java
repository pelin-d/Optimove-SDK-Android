package com.optimove.android.optimovemobilesdk.ui.location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.optimove.android.optimovemobilesdk.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private final AtomicReference<List<CoordinatesItem>> items = new AtomicReference<>();
    private final OnCoordinateClickListener mListener;

    public LocationAdapter(List<CoordinatesItem> items, OnCoordinateClickListener listener) {
        this.items.set(items);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_location_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        holder.getTitle().setText(items.get().get(position).getName());
        holder.getBody().setText(items.get().get(position).getLocationAsString());
        holder.getButton().setOnClickListener(v -> mListener.onClick(items.get().get(position)));
    }

    public interface OnCoordinateClickListener {
        void onClick(CoordinatesItem item);
    }

    @Override
    public int getItemCount() {
        return items.get().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title, body;
        private final ConstraintLayout button;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body);
            button = view.findViewById(R.id.button);
        }

        public ConstraintLayout getButton() {
            return button;
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getBody() {
            return body;
        }
    }

}
