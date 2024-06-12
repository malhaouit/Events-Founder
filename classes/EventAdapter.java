package com.amtrustdev.localeventfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amtrustdev.localeventfinder.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    // List to hold event data
    private List<Event> eventList;
    // Listener for item click events
    private OnItemClickListener listener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    // Constructor for EventAdapter
    public EventAdapter(List<Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    // ViewHolder class to hold references to the views for each data item
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDateTime, eventDescription, eventLocation;

        // Constructor for EventViewHolder
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.tvEventName);
            eventDateTime = itemView.findViewById(R.id.tvEventDateTime);
            eventDescription = itemView.findViewById(R.id.tvEventDescription);
            eventLocation = itemView.findViewById(R.id.tvEventLocation);
        }
    }

    @NonNull
    @Override
    // Create new views (invoked by the layout manager)
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        // Get the event at this position
        Event currentEvent = eventList.get(position);
        // Set the event details to the TextViews
        holder.eventName.setText(currentEvent.getEventName());
        holder.eventDateTime.setText(currentEvent.getDateTime());
        holder.eventDescription.setText(currentEvent.getDescription());
        // Set the click listener for the item view
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentEvent));
    }

    @Override
    // Return the size of the dataset (invoked by the layout manager)
    public int getItemCount() {
        return eventList.size();
    }
}
