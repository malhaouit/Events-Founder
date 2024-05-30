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
    private List<Event> eventList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public EventAdapter(List<Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDateTime, eventDescription, eventLocation;

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
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        Event currentEvent = eventList.get(position);
        holder.eventName.setText(currentEvent.getEventName());
        holder.eventDateTime.setText(currentEvent.getDateTime());
        holder.eventDescription.setText(currentEvent.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentEvent));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
